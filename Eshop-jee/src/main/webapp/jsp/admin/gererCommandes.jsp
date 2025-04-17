<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Utilisateur_model" %>
<%@ page import="model.Commande_model" %>
<%@ page import="model.CommandeWithClientName" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gérer les commandes - E-Shop Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/gererCommandesStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div class="gererCommandes">
    <div class="commande-management">
        <h2><i class="fas fa-shopping-cart"></i> GÉRER LES COMMANDES</h2>
        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/gererCommandes" method="get">
                <input type="text" name="query" value="${searchQuery}" placeholder="Rechercher par nom du client..." class="search-input">
                <button type="submit" class="search-btn"><i class="fas fa-search"></i> Rechercher</button>
            </form>
        </div>
        <div class="action-bar">
            <button class="btn-add" onclick="showAddForm()"><i class="fas fa-plus"></i> Ajouter une commande</button>
        </div>
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

        <!-- Form to Add Commande -->
        <form action="${pageContext.request.contextPath}/gererCommandes" method="post" class="commande-form" id="commandeForm">
            <input type="hidden" name="action" value="ajouter">
            <div class="form-group">
                <label for="clientName"><i class="fas fa-user"></i> Nom du Client :</label>
                <select name="clientName" id="clientName" required>
                    <option value="">Sélectionnez un client</option>
                    <%
                        List<Utilisateur_model> utilisateurs = (List<Utilisateur_model>) request.getAttribute("utilisateurs");
                        if (utilisateurs != null) {
                            for (Utilisateur_model utilisateur : utilisateurs) {
                                if (!utilisateur.isEstAdmin()) { // Exclude admins
                    %>
                    <option value="<%= utilisateur.getNom() %>"><%= utilisateur.getNom() %></option>
                    <%          }
                            }
                        }
                    %>
                </select>
            </div>
            <div class="form-group">
                <label for="produits"><i class="fas fa-box"></i> Produits :</label>
                <input type="text" name="produits" id="produits" placeholder="Ex: Téléphone:1,299.99;hello:1,359.1" required>
            </div>
            <div class="form-group">
                <label for="prixTotal"><i class="fas fa-euro-sign"></i> Prix Total :</label>
                <input type="number" step="0.01" name="prixTotal" id="prixTotal" placeholder="Entrez le prix total" required>
            </div>
            <div class="form-group">
                <label for="statut"><i class="fas fa-info-circle"></i> Statut :</label>
                <select name="statut" id="statut" required>
                    <option value="EN_ATTENTE">En attente</option>
                    <option value="EXPEDIEE">Expédiée</option>
                    <option value="LIVREE">Livrée</option>
                    <option value="ANNULEE">Annulée</option>
                </select>
            </div>
            <div class="form-group">
                <label for="dateCommande"><i class="fas fa-calendar-alt"></i> Date Commande :</label>
                <input type="date" name="dateCommande" id="dateCommande" required>
            </div>
            <button type="submit" class="btn-add"><i class="fas fa-plus"></i> Ajouter la commande</button>
            <button type="button" class="btn-cancel" onclick="hideForm()">Annuler</button>
        </form>

        <!-- Table of Commandes -->
        <div class="commande-table">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nom du Client</th>
                        <th>Date Commande</th>
                        <th>Statut</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<CommandeWithClientName> commandesWithClientName = (List<CommandeWithClientName>) request.getAttribute("commandesWithClientName");
                        if (commandesWithClientName != null && !commandesWithClientName.isEmpty()) {
                            for (CommandeWithClientName commandeWithClient : commandesWithClientName) {
                                Commande_model commande = commandeWithClient.getCommande();
                    %>
                    <tr>
                        <td><%= commande.getId() %></td>
                        <td><%= commandeWithClient.getClientName() %></td>
                        <td><%= commande.getDateCommande() %></td>
                        <td><%= commande.getStatut() %></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/gererCommandes?action=details&id=<%= commande.getId() %>" class="btn-details"><i class="fas fa-eye"></i> Détails</a>
                            <form action="${pageContext.request.contextPath}/gererCommandes" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="supprimer">
                                <input type="hidden" name="id" value="<%= commande.getId() %>">
                                <button type="submit" class="btn-delete" onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette commande ?')"><i class="fas fa-trash"></i> Supprimer</button>
                            </form>
                        </td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr>
                        <td colspan="5">Aucune commande trouvée.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    function showAddForm() {
        document.getElementById('commandeForm').style.display = 'block';
        document.getElementById('clientName').value = '';
        document.getElementById('produits').value = '';
        document.getElementById('prixTotal').value = '';
        document.getElementById('statut').value = 'EN_ATTENTE';
        document.getElementById('dateCommande').value = '';
    }

    function hideForm() {
        document.getElementById('commandeForm').style.display = 'none';
    }

    // Show form if there was an error during submission
    window.onload = function() {
        if ('${error}' !== '' || '${success}' !== '') {
            document.getElementById('commandeForm').style.display = 'block';
        }
    };
</script>
</body>
</html>