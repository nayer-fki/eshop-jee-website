package controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;

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

@WebServlet("/parametresCompte")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)    // 50MB
public class CompteAdmin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CompteAdmin.class.getName());
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

        // Load admin details into session if not already present
        Utilisateur_model admin = (Utilisateur_model) session.getAttribute("adminUser");
        if (admin == null) {
            String adminId = (String) session.getAttribute("adminId");
            try {
                admin = utilisateurDB.trouverUtilisateur(adminId);
                if (admin != null) {
                    session.setAttribute("adminUser", admin);
                } else {
                    request.setAttribute("error", "Utilisateur admin introuvable.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des détails de l'admin", e);
                request.setAttribute("error", "Erreur lors de la récupération des détails : " + e.getMessage());
            }
        }

        request.setAttribute("page", "/jsp/admin/parametresCompte.jsp");
        request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
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
        if ("update".equals(action)) {
            try {
                Utilisateur_model admin = (Utilisateur_model) session.getAttribute("adminUser");
                if (admin == null) {
                    request.setAttribute("error", "Utilisateur admin introuvable.");
                    request.setAttribute("page", "/jsp/admin/parametresCompte.jsp");
                    request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
                    return;
                }

                // Validate input
                String nom = request.getParameter("nom");
                String email = request.getParameter("email");
                String oldPassword = request.getParameter("oldPassword");
                String motDePasse = request.getParameter("motDePasse");

                if (nom == null || nom.trim().isEmpty()) {
                    throw new ServletException("Le nom est requis.");
                }

                String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
                if (email == null || !email.matches(emailRegex)) {
                    throw new ServletException("Veuillez entrer une adresse email valide.");
                }

                // Handle password change
                if (motDePasse != null && !motDePasse.trim().isEmpty()) {
                    // Validate new password
                    if (motDePasse.length() < 8 || !motDePasse.matches(".*[a-zA-Z].*") || !motDePasse.matches(".*[0-9].*")) {
                        throw new ServletException("Le mot de passe doit contenir au moins 8 caractères, incluant des lettres et des chiffres.");
                    }

                    // Verify old password
                    if (oldPassword == null || oldPassword.trim().isEmpty()) {
                        throw new ServletException("L'ancien mot de passe est requis pour changer le mot de passe.");
                    }

                    // Fetch the current admin from the database to get the hashed password
                    Utilisateur_model currentAdmin = utilisateurDB.trouverUtilisateur(admin.getId());
                    if (!utilisateurDB.verifyPassword(oldPassword, currentAdmin.getMotDePasse())) {
                        throw new ServletException("L'ancien mot de passe est incorrect.");
                    }

                    // Update the password (UtilisateurDB will hash it)
                    admin.setMotDePasse(motDePasse);
                }

                // Update admin details
                admin.setNom(nom);
                admin.setEmail(email);

                // Handle image upload
                Part filePart = request.getPart("image");
                String imagePath = handleImageUpload(filePart);
                if (imagePath != null) {
                    admin.setImage(imagePath);
                }

                // Save updates to the database
                utilisateurDB.modifierUtilisateur(admin);
                session.setAttribute("adminUser", admin); // Update session

                LOGGER.log(Level.INFO, "Compte admin mis à jour avec succès : ID={0}", admin.getId());
                request.setAttribute("success", "Compte mis à jour avec succès.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du compte admin", e);
                request.setAttribute("error", "Erreur lors de la mise à jour : " + e.getMessage());
            }
        }

        request.setAttribute("page", "/jsp/admin/parametresCompte.jsp");
        request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
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
                    LOGGER.log(Level.INFO, "Image téléchargée avec succès : {0}", file.getAbsolutePath());
                    return UPLOAD_DIR + "/" + fileName;
                } else {
                    throw new ServletException("Type de fichier invalide. Veuillez téléverser une image.");
                }
            }
        }
        return null;
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