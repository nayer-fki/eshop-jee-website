<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.Utilisateur_model, java.net.URLEncoder" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-style.css">

<% 
    String searchQuery = request.getAttribute("searchQuery") != null ? 
        URLEncoder.encode((String) request.getAttribute("searchQuery"), "UTF-8").replace("+", "%20") : "";
%>

<div class="user-management">
    <!-- Header Section -->
    <div class="admin-header">
        <h1>Gérer les Utilisateurs</h1>
        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/gererUtilisateurs" method="get" class="search-form">
                <input type="text" name="query" placeholder="Rechercher par nom ou email..." value="<%= request.getAttribute("searchQuery") != null ? request.getAttribute("searchQuery") : "" %>">
                <button type="submit" class="btn btn-search">Rechercher</button>
            </form>
        </div>
    </div>

    <div class="error">
        <% if (request.getAttribute("error") != null) { %>
            <p><%= request.getAttribute("error") %></p>
        <% } %>
    </div>

    <% if (request.getAttribute("searchQuery") != null) { %>
        <p class="search-results">Résultats de la recherche pour : <strong><%= request.getAttribute("searchQuery") %></strong></p>
    <% } %>

    <button class="btn btn-add" onclick="openAddUserModal()">Ajouter un utilisateur</button>

    <div id="addUserModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeAddUserModal()">×</span>
            <h3>Ajouter un nouvel utilisateur</h3>
            <form action="${pageContext.request.contextPath}/gererUtilisateurs" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="ajouter">
                <div class="form-group">
                    <label for="nom">Nom :</label>
                    <input type="text" name="nom" required>
                </div>
                <div class="form-group">
                    <label for="email">Email :</label>
                    <input type="email" name="email" required>
                </div>
                <div class="form-group">
                    <label for="motDePasse">Mot de passe :</label>
                    <input type="text" name="motDePasse" required>
                </div>
                <div class="form-group">
                    <label for="estAdmin">Admin :</label>
                    <input type="checkbox" name="estAdmin" value="true">
                </div>
                <div class="form-group">
                    <label for="image">Image :</label>
                    <input type="file" name="image" accept="image/*" required>
                </div>
                <button type="submit" class="btn btn-save">Ajouter</button>
            </form>
        </div>
    </div>

    <table class="user-table">
        <thead>
            <tr>
                <th>Nom</th>
                <th>Email</th>
                <th>Admin</th>
                <th>Image</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Utilisateur_model> utilisateurs = (List<Utilisateur_model>) request.getAttribute("utilisateurs");
                if (utilisateurs != null && !utilisateurs.isEmpty()) {
                    for (Utilisateur_model utilisateur : utilisateurs) {
            %>
                <tr>
                    <td><%= utilisateur.getNom() != null ? utilisateur.getNom() : "" %></td>
                    <td><%= utilisateur.getEmail() != null ? utilisateur.getEmail() : "" %></td>
                    <td><%= utilisateur.isEstAdmin() ? "Oui" : "Non" %></td>
                    <td>
                        <img src="${pageContext.request.contextPath}/<%= utilisateur.getImage() != null ? utilisateur.getImage() : "images/user-def.jpg" %>" 
                             alt="User Image" style="max-width: 50px; max-height: 50px;" 
                             onerror="this.src='${pageContext.request.contextPath}/images/user-def.jpg';">
                    </td>
                    <td class="action-buttons">
                        <button class="btn btn-edit" onclick="openEditModal('<%= utilisateur.getId() %>', '<%= utilisateur.getNom() != null ? utilisateur.getNom() : "" %>', '<%= utilisateur.getEmail() != null ? utilisateur.getEmail() : "" %>', '<%= utilisateur.getMotDePasse() != null ? utilisateur.getMotDePasse() : "" %>', <%= utilisateur.isEstAdmin() %>, '<%= utilisateur.getImage() != null ? utilisateur.getImage() : "images/user-def.jpg" %>')">Modifier</button>
                        <form action="${pageContext.request.contextPath}/gererUtilisateurs" method="post" style="display:inline-block;" onsubmit="return confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?');">
                            <input type="hidden" name="action" value="supprimer">
                            <input type="hidden" name="id" value="<%= utilisateur.getId() %>">
                            <input type="hidden" name="query" value="<%= searchQuery %>">
                            <button type="submit" class="btn btn-delete">Supprimer</button>
                        </form>
                    </td>
                </tr>
            <% 
                    }
                } else {
            %>
                <tr>
                    <td colspan="5">Aucun utilisateur trouvé.</td>
                </tr>
            <% 
                }
            %>
        </tbody>
    </table>

    <div id="editUserModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEditModal()">×</span>
            <h3>Modifier l'utilisateur</h3>
            <form id="editUserForm" action="${pageContext.request.contextPath}/gererUtilisateurs" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="modifier">
                <input type="hidden" name="id" id="editId">
                <input type="hidden" name="query" value="<%= searchQuery %>">
                <div class="form-group">
                    <label for="editNom">Nom :</label>
                    <input type="text" name="nom" id="editNom" required>
                </div>
                <div class="form-group">
                    <label for="editEmail">Email :</label>
                    <input type="email" name="email" id="editEmail" required>
                </div>
                <div class="form-group">
                    <label for="editMotDePasse">Mot de passe :</label>
                    <input type="text" name="motDePasse" id="editMotDePasse" required>
                </div>
                <div class="form-group">
                    <label for="editEstAdmin">Admin :</label>
                    <input type="checkbox" name="estAdmin" id="editEstAdmin" value="true">
                </div>
                <div class="form-group">
                    <label for="editImage">Image :</label>
                    <input type="file" name="image" id="editImage" accept="image/*" >
                </div>
                <button type="submit" class="btn btn-save">Enregistrer</button>
            </form>
        </div>
    </div>
</div>

<script>
    function openAddUserModal() {
        document.getElementById("addUserModal").style.display = "block";
    }

    function closeAddUserModal() {
        document.getElementById("addUserModal").style.display = "none";
    }

    function openEditModal(id, nom, email, motDePasse, estAdmin, image) {
        document.getElementById("editId").value = id;
        document.getElementById("editNom").value = nom;
        document.getElementById("editEmail").value = email;
        document.getElementById("editMotDePasse").value = motDePasse;
        document.getElementById("editEstAdmin").checked = estAdmin;
        document.getElementById("editImage").value = ""; // Clear file input
        document.getElementById("editUserModal").style.display = "block";
    }

    function closeEditModal() {
        document.getElementById("editUserModal").style.display = "none";
    }

    window.onclick = function(event) {
        if (event.target.className === "modal") {
            closeAddUserModal();
            closeEditModal();
        }
    }
</script>