package controller.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.PanierDB;
import model.ProduitDB;
import model.Panier_model;
import model.Produit_model;
import model.Utilisateur_model;

@WebServlet("/addToCart")
public class AddToCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProduitDB produitDB;
    private PanierDB panierDB;

    @Override
    public void init() throws ServletException {
        produitDB = new ProduitDB();
        panierDB = new PanierDB();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            Utilisateur_model utilisateur = (Utilisateur_model) session.getAttribute("utilisateur");
            if (utilisateur == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String productId = request.getParameter("productId");
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            Produit_model produit = produitDB.getProductById(productId);
            if (produit == null) {
                response.sendRedirect(request.getContextPath() + "/index?error=productNotFound");
                return;
            }

            String userId = utilisateur.getId();
            Panier_model panier = panierDB.trouverPanier(userId);
            if (panier == null) {
                panier = new Panier_model();
                panier.setUserId(userId);
            }
            panier.addItem(produit, quantity);
            if (panier.getId() == null) {
                panierDB.ajouterAuPanier(panier);
            } else {
                panierDB.modifierPanier(panier);
            }

            response.sendRedirect(request.getContextPath() + "/cart");
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'ajout au panier", e);
        }
    }
}