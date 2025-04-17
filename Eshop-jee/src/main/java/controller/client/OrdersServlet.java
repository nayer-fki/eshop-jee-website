package controller.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.CommandeDB;
import model.Commande_model;
import model.PanierDB;
import model.Panier_model;
import model.Utilisateur_model;

@WebServlet("/orders")
public class OrdersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CommandeDB commandeDB;
    private PanierDB panierDB;

    @Override
    public void init() throws ServletException {
        commandeDB = new CommandeDB();
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

            // Fetch the user's orders
            List<Commande_model> commandes = new ArrayList<>();
            for (Commande_model commande : commandeDB.listerCommandes()) {
                if (commande.getIdUtilisateur().equals(utilisateur.getId())) {
                    commandes.add(commande);
                }
            }

            // Fetch the cart for the header
            Panier_model panier = panierDB.trouverPanier(utilisateur.getId());
            if (panier == null) {
                panier = new Panier_model();
                panier.setUserId(utilisateur.getId());
            }

            request.setAttribute("commandes", commandes);
            request.setAttribute("panier", panier);
            request.getRequestDispatcher("/jsp/client/orders.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'affichage de l'historique des commandes", e);
        }
    }
}