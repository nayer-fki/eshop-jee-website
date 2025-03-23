package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.PanierDB;
import model.Panier_model;
import model.Utilisateur_model;

@WebServlet("/removeFromCart")
public class RemoveFromCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PanierDB panierDB;

    @Override
    public void init() throws ServletException {
        panierDB = new PanierDB();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            Utilisateur_model utilisateur = (Utilisateur_model) session.getAttribute("utilisateur");
            if (utilisateur == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String productId = request.getParameter("productId");
            String userId = utilisateur.getId();

            Panier_model panier = panierDB.trouverPanier(userId);
            if (panier != null) {
                panier.removeItem(productId);
                panierDB.modifierPanier(panier);
            }

            response.sendRedirect(request.getContextPath() + "/cart");
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la suppression du produit du panier", e);
        }
    }
}