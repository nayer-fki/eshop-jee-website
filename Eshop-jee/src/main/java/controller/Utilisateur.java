package controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.Utilisateur_model;
import model.UtilisateurDB;

@WebServlet("/gererUtilisateurs") // Ensure correct servlet mapping
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)    // 50MB
public class Utilisateur extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Utilisateur.class.getName());
    private static final String UPLOAD_DIR = "images";
    
    private UtilisateurDB utilisateurDB = new UtilisateurDB();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null || !(boolean) session.getAttribute("admin")) {
            response.sendRedirect(request.getContextPath() + "/LoginAdmin");
            return;
        }

        try {
            List<Utilisateur_model> utilisateurs = utilisateurDB.listerUtilisateurs();
            String query = request.getParameter("query");
            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLDecoder.decode(query, "UTF-8").trim();
                final String searchQuery = query.toLowerCase();
                utilisateurs = utilisateurs.stream()
                    .filter(u -> (u.getNom() != null && u.getNom().toLowerCase().contains(searchQuery)) ||
                                 (u.getEmail() != null && u.getEmail().toLowerCase().contains(searchQuery)))
                    .collect(Collectors.toList());
                request.setAttribute("searchQuery", query);
            }
            LOGGER.log(Level.INFO, "Nombre d'utilisateurs récupérés : {0}", utilisateurs != null ? utilisateurs.size() : 0);
            request.setAttribute("utilisateurs", utilisateurs);
            request.setAttribute("page", "/jsp/admin/gererUtilisateurs.jsp"); // Forward to layout
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des utilisateurs", e);
            request.setAttribute("error", "Erreur lors de la récupération des utilisateurs");
            request.setAttribute("page", "/jsp/admin/gererUtilisateurs.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null || !(boolean) session.getAttribute("admin")) {
            response.sendRedirect(request.getContextPath() + "/LoginAdmin");
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("ajouter".equals(action)) {
                Utilisateur_model utilisateur = new Utilisateur_model();
                utilisateur.setId(UUID.randomUUID().toString());
                utilisateur.setNom(request.getParameter("nom"));
                utilisateur.setEmail(request.getParameter("email"));
                utilisateur.setMotDePasse(request.getParameter("motDePasse"));
                utilisateur.setEstAdmin("true".equals(request.getParameter("estAdmin")));
                
                // Handle image upload
                Part filePart = request.getPart("image");
                String imagePath = handleImageUpload(filePart);
                if (imagePath == null) {
                    throw new ServletException("Image upload failed or no image provided.");
                }
                utilisateur.setImage(imagePath);

                utilisateurDB.ajouterUtilisateur(utilisateur);
                LOGGER.log(Level.INFO, "Utilisateur ajouté avec succès : ID={0}", utilisateur.getId());
            } else if ("modifier".equals(action)) {
                String id = request.getParameter("id");
                Utilisateur_model utilisateur = utilisateurDB.trouverUtilisateur(id);
                if (utilisateur != null) {
                    utilisateur.setNom(request.getParameter("nom"));
                    utilisateur.setEmail(request.getParameter("email"));
                    utilisateur.setMotDePasse(request.getParameter("motDePasse"));
                    utilisateur.setEstAdmin("true".equals(request.getParameter("estAdmin")));
                    
                    // Handle image upload
                    Part filePart = request.getPart("image");
                    String imagePath = handleImageUpload(filePart);
                    utilisateur.setImage(imagePath != null ? imagePath : utilisateur.getImage());

                    utilisateurDB.modifierUtilisateur(utilisateur);
                    LOGGER.log(Level.INFO, "Utilisateur modifié avec succès : ID={0}", id);
                } else {
                    LOGGER.log(Level.WARNING, "Utilisateur non trouvé pour modification : ID={0}", id);
                }
            } else if ("supprimer".equals(action)) {
                String id = request.getParameter("id");
                utilisateurDB.supprimerUtilisateur(id);
                LOGGER.log(Level.INFO, "Utilisateur supprimé avec succès : ID={0}", id);
            }
            String query = request.getParameter("query");
            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLEncoder.encode(query, "UTF-8").replace("+", "%20");
                response.sendRedirect(request.getContextPath() + "/gererUtilisateurs?query=" + query);
            } else {
                response.sendRedirect(request.getContextPath() + "/gererUtilisateurs");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'opération sur l'utilisateur", e);
            request.setAttribute("error", "Erreur lors de l'opération sur l'utilisateur : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererUtilisateurs.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (ServletException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du traitement de l'image", e);
            request.setAttribute("error", "Erreur lors du téléchargement de l'image : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererUtilisateurs.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        }
    }

    // Helper method to handle image upload
    private String handleImageUpload(Part filePart) throws IOException, ServletException {
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = extractFileName(filePart);
            if (fileName != null && !fileName.isEmpty()) {
                String contentType = filePart.getContentType();
                if (contentType != null && contentType.startsWith("image/")) {
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }
                    fileName = UUID.randomUUID().toString() + "_" + fileName; // Unique file name
                    File file = new File(uploadDir, fileName);
                    filePart.write(file.getAbsolutePath());
                    return UPLOAD_DIR + "/" + fileName;
                } else {
                    throw new ServletException("Invalid file type. Please upload an image.");
                }
            }
        }
        return null; // Return null if no image is uploaded, but this should trigger the required validation
    }

    // Helper method to extract file name from Part
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp != null) {
            String[] tokens = contentDisp.split(";");
            for (String token : tokens) {
                if (token.trim().startsWith("filename")) {
                    return token.substring(token.indexOf("=") + 2, token.length() - 1);
                }
            }
        }
        return null;
    }
}