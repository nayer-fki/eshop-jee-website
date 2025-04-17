package controller.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import model.ProduitDB;
import model.CategorieDB;
import model.PromotionalVideoDB;
import model.Produit_model;
import model.Categorie_model;
import model.PromotionalVideo_model;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProduitDB produitDB;
    private CategorieDB categorieDB;
    private PromotionalVideoDB videoDB;

    @Override
    public void init() throws ServletException {
        produitDB = new ProduitDB();
        categorieDB = new CategorieDB();
        videoDB = new PromotionalVideoDB();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get filter and search parameters
            String categorieId = request.getParameter("categorieId");
            String query = request.getParameter("query");

            // Fetch categories
            List<Categorie_model> categories = categorieDB.getAllCategories();
            request.setAttribute("categories", categories);
            request.setAttribute("selectedCategorieId", categorieId);

            // Fetch products based on filters
            List<Produit_model> produits;
            if (categorieId != null && !categorieId.isEmpty()) {
                produits = produitDB.getProductsByCategory(categorieId);
            } else if (query != null && !query.trim().isEmpty()) {
                produits = produitDB.searchProductsByName(query);
            } else {
                produits = produitDB.getAllProducts();
            }

            // Fetch discounted products for the "Offres Spéciales" section
            List<Produit_model> discountedProduits = produitDB.getDiscountedProducts();
            if (discountedProduits.size() > 4) {
                discountedProduits = discountedProduits.subList(0, 4);
            }

            // Fetch the promotional video URL
            PromotionalVideo_model video = videoDB.getPromotionalVideo();
            String videoUrl = (video != null && video.getVideoUrl() != null) ? video.getVideoUrl() : "https://www.youtube.com/embed/dQw4w9WgXcQ"; // Fallback URL

            request.setAttribute("produits", produits);
            request.setAttribute("discountedProduits", discountedProduits);
            request.setAttribute("videoUrl", videoUrl);
            request.setAttribute("searchQuery", query);

            // Forward to JSP
            request.getRequestDispatcher("/jsp/client/index.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Erreur lors de la récupération des données", e);
        }
    }
}