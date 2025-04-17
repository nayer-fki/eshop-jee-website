package controller.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.CommandeDB;
import model.Commande_model;
import model.CommandeWithClientName;
import model.ProduitDB;
import model.Produit_model;
import model.UtilisateurDB;
import model.Utilisateur_model;

@WebServlet("/gererCommandes")
public class GererCommandesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GererCommandesServlet.class.getName());
    
    private CommandeDB commandeDB;
    private UtilisateurDB utilisateurDB;
    private ProduitDB produitDB;

    @Override
    public void init() throws ServletException {
        commandeDB = new CommandeDB();
        utilisateurDB = new UtilisateurDB();
        produitDB = new ProduitDB();
        System.out.println("GererCommandesServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null || !(boolean) session.getAttribute("admin")) {
            response.sendRedirect(request.getContextPath() + "/LoginAdmin");
            return;
        }

        String action = request.getParameter("action");
        if ("details".equals(action)) {
            String commandeId = request.getParameter("id");
            try {
                Commande_model commande = commandeDB.trouverCommande(commandeId);
                if (commande != null) {
                    String clientName = utilisateurDB.getUserNameById(commande.getIdUtilisateur());
                    request.setAttribute("commande", commande);
                    request.setAttribute("clientName", clientName);
                    request.setAttribute("page", "/jsp/admin/commandeDetails.jsp");
                } else {
                    request.setAttribute("error", "Commande non trouvée.");
                    request.setAttribute("page", "/jsp/admin/gererCommandes.jsp");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des détails de la commande", e);
                request.setAttribute("error", "Erreur lors de la récupération des détails : " + e.getMessage());
                request.setAttribute("page", "/jsp/admin/gererCommandes.jsp");
            }
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
            return;
        }

        try {
            List<Commande_model> commandes = commandeDB.listerCommandes();
            List<Utilisateur_model> utilisateurs = utilisateurDB.listerUtilisateurs();
            request.setAttribute("utilisateurs", utilisateurs);

            List<CommandeWithClientName> commandesWithClientName = new ArrayList<>();
            for (Commande_model commande : commandes) {
                String clientName = utilisateurDB.getUserNameById(commande.getIdUtilisateur());
                commandesWithClientName.add(new CommandeWithClientName(commande, clientName));
            }

            String query = request.getParameter("query");
            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLDecoder.decode(query, "UTF-8").trim();
                final String searchQuery = query.toLowerCase();
                commandesWithClientName = commandesWithClientName.stream()
                    .filter(c -> c.getClientName() != null && c.getClientName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
                request.setAttribute("searchQuery", query);
            }
            LOGGER.log(Level.INFO, "Nombre de commandes récupérées : {0}", commandesWithClientName.size());
            request.setAttribute("commandesWithClientName", commandesWithClientName);
            request.setAttribute("page", "/jsp/admin/gererCommandes.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des commandes", e);
            request.setAttribute("error", "Erreur lors de la récupération des commandes : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererCommandes.jsp");
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
        String id = request.getParameter("id");

        try {
            if ("ajouter".equals(action)) {
                String clientName = request.getParameter("clientName");
                String produits = request.getParameter("produits");
                String prixTotalStr = request.getParameter("prixTotal");
                String statut = request.getParameter("statut");
                String dateCommandeStr = request.getParameter("dateCommande");

                if (clientName != null && !clientName.trim().isEmpty() &&
                    produits != null && !produits.trim().isEmpty() &&
                    prixTotalStr != null && !prixTotalStr.trim().isEmpty() &&
                    statut != null && !statut.trim().isEmpty() &&
                    dateCommandeStr != null && !dateCommandeStr.trim().isEmpty()) {
                    try {
                        double prixTotal = Double.parseDouble(prixTotalStr.trim());
                        List<Utilisateur_model> utilisateurs = utilisateurDB.listerUtilisateurs();
                        String idUtilisateur = null;
                        for (Utilisateur_model utilisateur : utilisateurs) {
                            if (utilisateur.getNom().equalsIgnoreCase(clientName)) {
                                idUtilisateur = utilisateur.getId();
                                break;
                            }
                        }
                        if (idUtilisateur == null) {
                            throw new ServletException("Utilisateur non trouvé pour le nom : " + clientName);
                        }

                        Commande_model commande = new Commande_model();
                        commande.setId("CMD" + System.currentTimeMillis());
                        commande.setIdUtilisateur(idUtilisateur);
                        commande.setProduits(produits);
                        commande.setPrixTotal(prixTotal);
                        commande.setStatut(statut);
                        commande.setDateCommande(java.sql.Date.valueOf(dateCommandeStr.trim()));
                        commandeDB.ajouterCommande(commande);
                        request.setAttribute("success", "Commande ajoutée avec succès.");
                        LOGGER.log(Level.INFO, "Commande ajoutée avec succès : ID={0}", commande.getId());
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.SEVERE, "Invalid price format: {0}", prixTotalStr);
                        throw new ServletException("Format de prix invalide : " + prixTotalStr);
                    } catch (IllegalArgumentException e) {
                        LOGGER.log(Level.SEVERE, "Invalid date format: {0}", dateCommandeStr);
                        throw new ServletException("Format de date invalide : " + dateCommandeStr);
                    }
                } else {
                    throw new ServletException("Tous les champs sont requis pour ajouter une commande.");
                }
            } else if ("modifierStatus".equals(action)) {
                String newStatus = request.getParameter("statut");
                Commande_model commande = commandeDB.trouverCommande(id);
                if (commande != null) {
                    String oldStatus = commande.getStatut();
                    if (!newStatus.equals(oldStatus)) { // Only update if status has changed
                        commande.setStatut(newStatus);
                        if ("LIVREE".equals(newStatus)) {
                            // Parse products and update stock
                            Map<String, Integer> orderedQuantities = parseProducts(commande.getProduits());
                            for (Map.Entry<String, Integer> entry : orderedQuantities.entrySet()) {
                                String productId = entry.getKey(); // Use product ID
                                String paramName = "delivered_" + productId.replaceAll("[^a-zA-Z0-9]", "");
                                String deliveredStr = request.getParameter(paramName);
                                int deliveredQty = 0;
                                if (deliveredStr != null && !deliveredStr.trim().isEmpty()) {
                                    try {
                                        deliveredQty = Integer.parseInt(deliveredStr.trim());
                                    } catch (NumberFormatException e) {
                                        LOGGER.log(Level.WARNING, "Invalid quantity format for product ID {0}: {1}", 
                                                   new Object[]{productId, deliveredStr});
                                        request.setAttribute("error", "Quantité invalide pour le produit ID : " + productId + " (valeur : " + deliveredStr + ")");
                                        request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
                                        return;
                                    }
                                }
                                if (deliveredQty > 0 && deliveredQty <= entry.getValue()) {
                                    Produit_model product = produitDB.getProductById(productId);
                                    if (product != null) {
                                        int newStock = product.getQuantite() - deliveredQty;
                                        if (newStock >= 0) {
                                            product.setQuantite(newStock);
                                            produitDB.modifierProduit(product);
                                            LOGGER.log(Level.INFO, "Stock mis à jour pour produit ID {0}: {1} -> {2}", 
                                                       new Object[]{productId, product.getQuantite() + deliveredQty, newStock});
                                        } else {
                                            request.setAttribute("error", "Stock insuffisant pour le produit ID : " + productId);
                                            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
                                            return;
                                        }
                                    } else {
                                        request.setAttribute("error", "Produit non trouvé avec ID : " + productId);
                                        request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
                                        return;
                                    }
                                } else if (deliveredQty < 0) {
                                    request.setAttribute("error", "Quantité livrée invalide pour le produit ID : " + productId);
                                    request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
                                    return;
                                }
                            }
                            request.setAttribute("success", "Statut mis à jour et stock ajusté pour les quantités livrées.");
                        } else {
                            request.setAttribute("success", "Statut de la commande mis à jour avec succès.");
                        }
                        commandeDB.modifierCommande(commande);
                        LOGGER.log(Level.INFO, "Statut de la commande mis à jour : ID={0}, Statut={1}", new Object[]{id, newStatus});
                    } else {
                        request.setAttribute("success", "Aucun changement de statut détecté.");
                    }
                    String clientName = utilisateurDB.getUserNameById(commande.getIdUtilisateur());
                    request.setAttribute("commande", commande);
                    request.setAttribute("clientName", clientName);
                    request.setAttribute("page", "/jsp/admin/commandeDetails.jsp");
                } else {
                    throw new ServletException("Commande non trouvée.");
                }
            } else if ("supprimer".equals(action)) {
                if (id != null) {
                    commandeDB.supprimerCommande(id);
                    request.setAttribute("success", "Commande supprimée avec succès.");
                    LOGGER.log(Level.INFO, "Commande supprimée avec succès : ID={0}", id);
                } else {
                    throw new ServletException("L'ID de la commande est requis.");
                }
            }

            List<Commande_model> commandes = commandeDB.listerCommandes();
            List<Utilisateur_model> utilisateurs = utilisateurDB.listerUtilisateurs();
            request.setAttribute("utilisateurs", utilisateurs);

            List<CommandeWithClientName> commandesWithClientName = new ArrayList<>();
            for (Commande_model cmd : commandes) {
                String clientName = utilisateurDB.getUserNameById(cmd.getIdUtilisateur());
                commandesWithClientName.add(new CommandeWithClientName(cmd, clientName));
            }

            String query = request.getParameter("query");
            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLDecoder.decode(query, "UTF-8").trim();
                final String searchQuery = query.toLowerCase();
                commandesWithClientName = commandesWithClientName.stream()
                    .filter(c -> c.getClientName() != null && c.getClientName().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
                request.setAttribute("searchQuery", query);
            }
            request.setAttribute("commandesWithClientName", commandesWithClientName);
            request.setAttribute("page", "/jsp/admin/gererCommandes.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        } catch (Exception e) { // Broad catch to handle any unexpected exceptions
            LOGGER.log(Level.SEVERE, "Une erreur inattendue s'est produite : " + e.getMessage(), e);
            request.setAttribute("error", "Une erreur inattendue s'est produite : " + e.getMessage());
            request.setAttribute("page", "/jsp/admin/gererCommandes.jsp");
            request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
        }
    }

    private Map<String, Integer> parseProducts(String produits) {
        Map<String, Integer> quantities = new HashMap<>();
        if (produits != null && !produits.isEmpty()) {
            String[] items = produits.split(";");
            for (int i = 0; i < items.length; i += 3) {
                if (i + 1 < items.length) {
                    String productId = items[i].split(":")[0]; // Extract ID from format like "P001:2"
                    int quantity = Integer.parseInt(items[i + 1].split(":")[1]);
                    quantities.put(productId, quantity);
                }
            }
        }
        return quantities;
    }
}