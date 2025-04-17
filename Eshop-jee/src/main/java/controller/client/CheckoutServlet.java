package controller.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.UUID;
import model.CommandeDB;
import model.Commande_model;
import model.PanierDB;
import model.PanierItem_model;
import model.Panier_model;
import model.Utilisateur_model;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PanierDB panierDB;
    private CommandeDB commandeDB;

    @Override
    public void init() throws ServletException {
        panierDB = new PanierDB();
        commandeDB = new CommandeDB();
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

            String userId = utilisateur.getId();
            Panier_model panier = panierDB.trouverPanier(userId);
            if (panier == null || panier.getItems().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            request.setAttribute("panier", panier);
            request.getRequestDispatcher("/jsp/client/checkout.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'affichage de la page de paiement", e);
        }
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

            String userId = utilisateur.getId();
            Panier_model panier = panierDB.trouverPanier(userId);
            if (panier == null || panier.getItems().isEmpty()) {
                System.out.println("CheckoutServlet: Cart is empty or null for userId " + userId);
                response.sendRedirect(request.getContextPath() + "/cart?error=emptyCart");
                return;
            }

            // Convert cart items to a string format for the produits field
            StringBuilder produitsString = new StringBuilder();
            int validItems = 0;
            for (PanierItem_model item : panier.getItems()) {
                if (item != null && item.getProduit() != null && item.getProduit().getNom() != null) {
                    if (produitsString.length() > 0) {
                        produitsString.append(";");
                    }
                    produitsString.append(item.getProduit().getNom())
                                 .append(":")
                                 .append(item.getQuantite())
                                 .append(":")
                                 .append(item.getPrixUnitaire());
                    validItems++;
                } else {
                    System.out.println("CheckoutServlet: Skipping invalid cart item - item: " + (item == null ? "null" : item) +
                            ", produit: " + (item != null && item.getProduit() == null ? "null" : (item != null ? item.getProduit() : "N/A")) +
                            ", produit.nom: " + (item != null && item.getProduit() != null && item.getProduit().getNom() == null ? "null" : "N/A"));
                }
            }

            // Check if there are any valid items
            if (validItems == 0 || produitsString.length() == 0) {
                System.out.println("CheckoutServlet: No valid items in cart for userId " + userId + ". produitsString: " + produitsString.toString());
                response.sendRedirect(request.getContextPath() + "/cart?error=invalidItems");
                return;
            }

            // Log the produits string for debugging
            System.out.println("CheckoutServlet: produitsString for userId " + userId + ": " + produitsString.toString());

            // Create a new order
            Commande_model commande = new Commande_model();
            commande.setId(UUID.randomUUID().toString());
            commande.setIdUtilisateur(userId);
            commande.setProduits(produitsString.toString());
            commande.setPrixTotal(panier.getTotal());
            commande.setStatut("EN_ATTENTE");
            commande.setDateCommande(new Date(System.currentTimeMillis()));

            // Save the order to the database
            commandeDB.ajouterCommande(commande);

            // Clear the cart
            panierDB.supprimerPanier(panier.getId());

            // Redirect to the homepage with a success message
            response.sendRedirect(request.getContextPath() + "/index?orderSuccess=true");
        } catch (Exception e) {
            System.err.println("CheckoutServlet: Error during checkout for user: " + (request.getSession().getAttribute("utilisateur") != null ?
                    ((Utilisateur_model) request.getSession().getAttribute("utilisateur")).getId() : "unknown"));
            throw new ServletException("Erreur lors de la finalisation de la commande", e);
        }
    }
}