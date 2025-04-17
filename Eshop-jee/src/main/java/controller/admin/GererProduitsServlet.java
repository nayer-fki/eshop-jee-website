package controller.admin;

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
import model.Categorie_model;
import model.CategorieDB;
import model.Produit_model;
import model.ProduitDB;

@WebServlet("/gererProduits")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)    // 50MB
public class GererProduitsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GererProduitsServlet.class.getName());
    private static final String UPLOAD_DIR = "images";

    private ProduitDB produitDB = new ProduitDB();
    private CategorieDB categorieDB = new CategorieDB();

    @Override
    public void init() throws ServletException {
        LOGGER.log(Level.INFO, "GererProduitsServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null || !(boolean) session.getAttribute("admin")) {
            response.sendRedirect(request.getContextPath() + "/LoginAdmin");
            return;
        }

        try {
            // Load categories for the filter dropdown
            List<Categorie_model> categories = categorieDB.getAllCategories();
            request.setAttribute("categories", categories);

            // Load products with filtering
            List<Produit_model> produits = produitDB.getAllProducts();
            String query = request.getParameter("query");
            String categorieId = request.getParameter("categorieId");

            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLDecoder.decode(query, "UTF-8").trim();
                final String searchQuery = query.toLowerCase();
                produits = produits.stream()
                    .filter(p -> p.getNom() != null && p.getNom().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
                request.setAttribute("searchQuery", query);
            }

            if (categorieId != null && !categorieId.isEmpty()) {
                final String filterCategorieId = categorieId;
                produits = produits.stream()
                    .filter(p -> p.getIdCategorie() != null && p.getIdCategorie().equals(filterCategorieId))
                    .collect(Collectors.toList());
                request.setAttribute("selectedCategorieId", categorieId);
            }

            LOGGER.log(Level.INFO, "Nombre de produits récupérés : {0}", produits != null ? produits.size() : 0);
            request.setAttribute("produits", produits);
            request.setAttribute("page", "/jsp/admin/gererProduits.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des données", e);
            request.setAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererProduits.jsp");
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
            // Reload categories for the filter dropdown
            List<Categorie_model> categories = categorieDB.getAllCategories();
            request.setAttribute("categories", categories);

            if ("ajouter".equals(action)) {
                Produit_model produit = new Produit_model();
                produit.setId("PROD" + System.currentTimeMillis());
                produit.setNom(request.getParameter("nom"));
                produit.setDescription(request.getParameter("description"));
                produit.setPrix(Double.parseDouble(request.getParameter("prix")));
                produit.setQuantite(Integer.parseInt(request.getParameter("quantite")));
                produit.setIdCategorie(request.getParameter("idCategorie"));
                produit.setRemise(Double.parseDouble(request.getParameter("remise") != null ? request.getParameter("remise") : "0"));

                // Handle image upload (required for adding)
                Part filePart = request.getPart("image");
                String imagePath = handleImageUpload(filePart);
                if (imagePath == null) {
                    throw new ServletException("Une image est requise pour ajouter un produit.");
                }
                produit.setImage(imagePath);

                if (produit.getNom() != null && !produit.getNom().trim().isEmpty() &&
                    produit.getIdCategorie() != null && !produit.getIdCategorie().isEmpty()) {
                    produitDB.ajouterProduit(produit);
                    request.setAttribute("success", "Produit ajouté avec succès.");
                    LOGGER.log(Level.INFO, "Produit ajouté avec succès : ID={0}", produit.getId());
                } else {
                    throw new ServletException("Le nom et la catégorie du produit sont requis.");
                }
            } else if ("modifier".equals(action)) {
                String id = request.getParameter("id");
                Produit_model produit = produitDB.getProductById(id);
                if (produit != null) {
                    produit.setNom(request.getParameter("nom"));
                    produit.setDescription(request.getParameter("description"));
                    produit.setPrix(Double.parseDouble(request.getParameter("prix")));
                    produit.setQuantite(Integer.parseInt(request.getParameter("quantite")));
                    produit.setIdCategorie(request.getParameter("idCategorie"));
                    produit.setRemise(Double.parseDouble(request.getParameter("remise") != null ? request.getParameter("remise") : "0"));

                    // Handle image upload (optional for modifying)
                    Part filePart = request.getPart("image");
                    String imagePath = handleImageUpload(filePart);
                    produit.setImage(imagePath != null ? imagePath : produit.getImage());

                    if (produit.getNom() != null && !produit.getNom().trim().isEmpty() &&
                        produit.getIdCategorie() != null && !produit.getIdCategorie().isEmpty()) {
                        produitDB.modifierProduit(produit);
                        request.setAttribute("success", "Produit modifié avec succès.");
                        LOGGER.log(Level.INFO, "Produit modifié avec succès : ID={0}", id);
                    } else {
                        throw new ServletException("Le nom et la catégorie du produit sont requis.");
                    }
                } else {
                    throw new ServletException("Produit non trouvé.");
                }
            } else if ("supprimer".equals(action)) {
                String id = request.getParameter("id");
                if (id != null) {
                    produitDB.supprimerProduit(id);
                    request.setAttribute("success", "Produit supprimé avec succès.");
                    LOGGER.log(Level.INFO, "Produit supprimé avec succès : ID={0}", id);
                } else {
                    throw new ServletException("L'ID du produit est requis.");
                }
            }

            // Reload products and forward to the same page to show updated list
            List<Produit_model> produits = produitDB.getAllProducts();
            String query = request.getParameter("query");
            String categorieId = request.getParameter("categorieId");

            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLDecoder.decode(query, "UTF-8").trim();
                final String searchQuery = query.toLowerCase();
                produits = produits.stream()
                    .filter(p -> p.getNom() != null && p.getNom().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
                request.setAttribute("searchQuery", query);
            }

            if (categorieId != null && !categorieId.isEmpty()) {
                final String filterCategorieId = categorieId;
                produits = produits.stream()
                    .filter(p -> p.getIdCategorie() != null && p.getIdCategorie().equals(filterCategorieId))
                    .collect(Collectors.toList());
                request.setAttribute("selectedCategorieId", categorieId);
            }

            request.setAttribute("produits", produits);
            request.setAttribute("page", "/jsp/admin/gererProduits.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'opération sur le produit", e);
            request.setAttribute("error", "Erreur lors de l'opération : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererProduits.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (ServletException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'opération", e);
            request.setAttribute("error", e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererProduits.jsp");
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
                    fileName = UUID.randomUUID().toString() + "_" + fileName;
                    File file = new File(uploadDir, fileName);
                    filePart.write(file.getAbsolutePath());
                    return UPLOAD_DIR + "/" + fileName;
                } else {
                    throw new ServletException("Type de fichier invalide. Veuillez uploader une image.");
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