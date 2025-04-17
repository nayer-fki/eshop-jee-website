package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.PanierDB;
import model.Panier_model;
import model.PanierItem_model;
import model.Utilisateur_model;

@WebServlet("/updateCartQuantity")
public class UpdateCartQuantityServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PanierDB panierDB;

    @Override
    public void init() throws ServletException {
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
            String userId = utilisateur.getId();

            Panier_model panier = panierDB.trouverPanier(userId);
            if (panier == null) {
                System.out.println("UpdateCartQuantityServlet: No cart found for userId " + userId);
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            System.out.println("UpdateCartQuantityServlet: Cart found with ID " + panier.getId());
            if (panier.getItems() == null) {
                System.out.println("UpdateCartQuantityServlet: Cart items list is null for cart ID " + panier.getId());
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            System.out.println("UpdateCartQuantityServlet: Cart has " + panier.getItems().size() + " items");
            for (PanierItem_model item : panier.getItems()) {
                if (item == null) {
                    System.out.println("UpdateCartQuantityServlet: Found a null item in the cart");
                    continue;
                }
                if (item.getProduit() == null) {
                    System.out.println("UpdateCartQuantityServlet: Found an item with a null product in the cart");
                    continue;
                }
                if (item.getProduit().getId().equals(productId)) {
                    if (quantity <= 0) {
                        panier.removeItem(productId);
                        System.out.println("UpdateCartQuantityServlet: Removed item with productId " + productId);
                    } else {
                        item.setQuantite(quantity);
                        panier.recalculateTotal();
                        System.out.println("UpdateCartQuantityServlet: Updated quantity to " + quantity + " for productId " + productId);
                    }
                    break;
                }
            }
            panierDB.modifierPanier(panier);
            System.out.println("UpdateCartQuantityServlet: Cart updated successfully");

            response.sendRedirect(request.getContextPath() + "/cart");
        } catch (Exception e) {
            System.out.println("UpdateCartQuantityServlet: Error - " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Erreur lors de la mise à jour de la quantité dans le panier", e);
        }
    }
}