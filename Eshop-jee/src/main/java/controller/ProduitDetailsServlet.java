package controller;

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
            if (productId != null && !productId.isEmpty()) {
                // Fetch the product
                Produit_model produit = produitDB.getProductById(productId);
                if (produit != null) {
                    // Fetch the category
                    Categorie_model categorie = categorieDB.getCategorieById(produit.getIdCategorie());
                    // Fetch evaluations and comments
                    List<Evaluation> evaluations = avisDB.getEvaluationsByProduitId(productId);
                    List<Commentaire> commentaires = avisDB.getCommentairesByProduitId(productId);
                    double averageRating = avisDB.getAverageRatingByProduitId(productId);

                    // Set attributes for the JSP
                    request.setAttribute("produit", produit);
                    request.setAttribute("categorie", categorie);
                    request.setAttribute("evaluations", evaluations);
                    request.setAttribute("commentaires", commentaires);
                    request.setAttribute("averageRating", averageRating);
                }
            }
            request.getRequestDispatcher("/jsp/client/produitDetails.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Erreur lors de la récupération des données", e);
        }
    }
}