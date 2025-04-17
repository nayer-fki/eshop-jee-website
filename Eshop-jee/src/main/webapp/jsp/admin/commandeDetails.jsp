<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Commande_model" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Détails de la commande - E-Shop Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/gererCommandesStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div class="gererCommandes">
    <div class="commande-management">
        <h2><i class="fas fa-shopping-cart"></i> DÉTAILS DE LA COMMANDE</h2>
        <div class="message error">
            <% if (request.getAttribute("error") != null) { %>
                <p><i class="fas fa-exclamation-triangle"></i> <%= request.getAttribute("error") %></p>
            <% } %>
        </div>
        <div class="message success">
            <% if (request.getAttribute("success") != null) { %>
                <p><i class="fas fa-check-circle"></i> <%= request.getAttribute("success") %></p>
            <% } %>
        </div>

        <%
            Commande_model commande = (Commande_model) request.getAttribute("commande");
            String clientName = (String) request.getAttribute("clientName");
            if (commande != null) {
        %>
        <div class="commande-details">
            <p><strong>ID Commande :</strong> <%= commande.getId() %></p>
            <p><strong>Nom du Client :</strong> <%= clientName %></p>
            <p><strong>Produits :</strong> <%= commande.getProduits() %></p>
            <p><strong>Prix Total :</strong> <%= String.format("%.2f", commande.getPrixTotal()) %> €</p>
            <p><strong>Statut :</strong> <%= commande.getStatut() %></p>
            <p><strong>Date Commande :</strong> <%= commande.getDateCommande() %></p>
        </div>

        <!-- Form to Change Status -->
        <div class="status-form">
            <h3>Changer le statut</h3>
            <form action="${pageContext.request.contextPath}/gererCommandes" method="post">
                <input type="hidden" name="action" value="modifierStatus">
                <input type="hidden" name="id" value="<%= commande.getId() %>">
                <div class="form-group">
                    <label for="statut"><i class="fas fa-info-circle"></i> Nouveau Statut :</label>
                    <select name="statut" id="statut" required>
                        <option value="EN_ATTENTE" <%= "EN_ATTENTE".equals(commande.getStatut()) ? "selected" : "" %>>En attente</option>
                        <option value="EXPEDIEE" <%= "EXPEDIEE".equals(commande.getStatut()) ? "selected" : "" %>>Expédiée</option>
                        <option value="LIVREE" <%= "LIVREE".equals(commande.getStatut()) ? "selected" : "" %>>Livrée</option>
                        <option value="ANNULEE" <%= "ANNULEE".equals(commande.getStatut()) ? "selected" : "" %>>Annulée</option>
                    </select>
                </div>
                <button type="submit" class="btn-add"><i class="fas fa-save"></i> Mettre à jour le statut</button>
            </form>
        </div>

        <div class="back-link">
            <a href="${pageContext.request.contextPath}/gererCommandes" class="btn-cancel"><i class="fas fa-arrow-left"></i> Retour à la liste</a>
        </div>
        <% } else { %>
        <p>Aucune commande trouvée.</p>
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/gererCommandes" class="btn-cancel"><i class="fas fa-arrow-left"></i> Retour à la liste</a>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>