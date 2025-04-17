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
import model.AvisDB;
import model.Produit_model;
import model.Categorie_model;
import model.Evaluation;
import model.Commentaire;

@WebServlet("/produitDetails")
public class ProduitDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProduitDB produitDB;
    private CategorieDB categorieDB;
    private AvisDB avisDB;

    @Override
    public void init() throws ServletException {
        produitDB = new ProduitDB();
        categorieDB = new CategorieDB();
        avisDB = new AvisDB();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String productId = request.getParameter("id");
            int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
            int pageSize = 5; // Number of comments per page

            if (productId != null && !productId.isEmpty()) {
                // Fetch the product
                Produit_model produit = produitDB.getProductById(productId);
                if (produit != null) {
                    // Fetch the category
                    Categorie_model categorie = categorieDB.getCategorieById(produit.getIdCategorie());
                    // Fetch evaluations and comments
                    List<Evaluation> evaluations = avisDB.getEvaluationsByProduitId(productId);
                    List<Commentaire> commentaires = avisDB.getCommentairesByProduitId(productId, page, pageSize);
                    double averageRating = avisDB.getAverageRatingByProduitId(productId);
                    int totalComments = avisDB.getCommentCount(productId);
                    int totalPages = (int) Math.ceil((double) totalComments / pageSize);
                    // Fetch related products
                    List<Produit_model> relatedProducts = produitDB.getRelatedProducts(produit.getIdCategorie(), produit.getId());

                    // Set attributes for the JSP
                    request.setAttribute("produit", produit);
                    request.setAttribute("categorie", categorie);
                    request.setAttribute("evaluations", evaluations);
                    request.setAttribute("commentaires", commentaires);
                    request.setAttribute("averageRating", averageRating);
                    request.setAttribute("currentPage", page);
                    request.setAttribute("totalPages", totalPages);
                    request.setAttribute("relatedProducts", relatedProducts);
                }
            }
            request.getRequestDispatcher("/jsp/client/produitDetails.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Erreur lors de la récupération des données", e);
        }
    }
}