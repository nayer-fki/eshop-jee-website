package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import model.ProduitDB;
import model.CategorieDB;
import model.Produit_model;
import model.Categorie_model;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProduitDB produitDB;
    private CategorieDB categorieDB;

    @Override
    public void init() throws ServletException {
        produitDB = new ProduitDB();
        categorieDB = new CategorieDB();
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
            List<Produit_model> discountedProduits = produitDB.getAllProducts().stream()
                .filter(p -> p.getRemise() > 0)
                .limit(4) // Limit to 4 featured products
                .collect(Collectors.toList());

            request.setAttribute("produits", produits);
            request.setAttribute("discountedProduits", discountedProduits);
            request.setAttribute("searchQuery", query);

            // Forward to JSP
            request.getRequestDispatcher("/jsp/client/index.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Erreur lors de la récupération des données", e);
        }
    }
}