<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Utilisateur_model" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gérer les utilisateurs - E-Shop Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/gererUtilisateursStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div class="gererUtilisateurs">
    <div class="user-management">
        <h2><i class="fas fa-users"></i> GÉRER LES UTILISATEURS</h2>
        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/gererUtilisateurs" method="get">
                <input type="text" name="query" value="${searchQuery}" placeholder="Rechercher par nom ou email..." class="search-input">
                <button type="submit" class="search-btn"><i class="fas fa-search"></i> Rechercher</button>
            </form>
        </div>
        <div class="action-bar">
            <button class="btn-add" onclick="showAddForm()"><i class="fas fa-plus"></i> Ajouter un utilisateur</button>
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

        <!-- Form to Add/Edit Utilisateur -->
        <form action="${pageContext.request.contextPath}/gererUtilisateurs" method="post" enctype="multipart/form-data" class="user-form" id="userForm">
            <input type="hidden" name="action" id="formAction">
            <input type="hidden" name="id" id="userId">
            <div class="form-group">
                <label for="nom"><i class="fas fa-user"></i> Nom :</label>
                <input type="text" name="nom" id="nom" placeholder="Entrez le nom" required>
            </div>
            <div class="form-group">
                <label for="email"><i class="fas fa-envelope"></i> Email :</label>
                <input type="email" name="email" id="email" placeholder="Entrez l'email" required>
            </div>
            <div class="form-group">
                <label for="motDePasse"><i class="fas fa-lock"></i> Mot de passe :</label>
                <input type="text" name="motDePasse" id="motDePasse" placeholder="Entrez le mot de passe" required>
            </div>
            <div class="form-group">
                <label for="estAdmin"><i class="fas fa-user-shield"></i> Est Admin :</label>
                <input type="checkbox" name="estAdmin" id="estAdmin" value="true">
            </div>
            <div class="form-group">
                <label for="image"><i class="fas fa-image"></i> Image :</label>
                <input type="file" name="image" id="image" accept="image/*">
            </div>
            <button type="submit" class="btn-add" id="submitBtn"><i class="fas fa-plus"></i> Ajouter l'utilisateur</button>
            <button type="button" class="btn-cancel" onclick="hideForm()">Annuler</button>
        </form>

        <!-- Table of Utilisateurs -->
        <div class="user-table">
            <table>
                <thead>
                    <tr>
                        
                        <th>Image</th>
                        <th>Nom</th>
                        <th>Email</th>
                        <th>Est Admin</th>
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
                        
                        <td>
                            <img src="<%= utilisateur.getImage() != null ? request.getContextPath() + "/" + utilisateur.getImage() : request.getContextPath() + "/images/default-user.png" %>" alt="User Image" class="user-image">
                        </td>
                        <td><%= utilisateur.getNom() %></td>
                        <td><%= utilisateur.getEmail() %></td>
                        <td><%= utilisateur.isEstAdmin() ? "Oui" : "Non" %></td>
                        <td>
                            <button class="btn-edit" onclick="editUtilisateur('<%= utilisateur.getId() %>', '<%= utilisateur.getNom() %>', '<%= utilisateur.getEmail() %>', '<%= utilisateur.getMotDePasse() %>', <%= utilisateur.isEstAdmin() %>)"><i class="fas fa-edit"></i> Modifier</button>
                            <form action="${pageContext.request.contextPath}/gererUtilisateurs" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="supprimer">
                                <input type="hidden" name="id" value="<%= utilisateur.getId() %>">
                                <button type="submit" class="btn-delete" onclick="return confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')"><i class="fas fa-trash"></i> Supprimer</button>
                            </form>
                        </td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr>
                        <td colspan="6">Aucun utilisateur trouvé.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    function showAddForm() {
        document.getElementById('userForm').style.display = 'block';
        document.getElementById('formAction').value = 'ajouter';
        document.getElementById('userId').value = '';
        document.getElementById('nom').value = '';
        document.getElementById('email').value = '';
        document.getElementById('motDePasse').value = '';
        document.getElementById('estAdmin').checked = false;
        document.getElementById('image').value = '';
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-plus"></i> Ajouter l\'utilisateur';
    }

    function editUtilisateur(id, nom, email, motDePasse, estAdmin) {
        document.getElementById('userForm').style.display = 'block';
        document.getElementById('formAction').value = 'modifier';
        document.getElementById('userId').value = id;
        document.getElementById('nom').value = nom;
        document.getElementById('email').value = email;
        document.getElementById('motDePasse').value = motDePasse;
        document.getElementById('estAdmin').checked = estAdmin;
        document.getElementById('image').value = '';
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Mettre à jour';
    }

    function hideForm() {
        document.getElementById('userForm').style.display = 'none';
    }

    // Show form if there was an error during submission
    window.onload = function() {
        if ('${error}' !== '' || '${success}' !== '') {
            document.getElementById('userForm').style.display = 'block';
        }
    };
</script>
</body>
</html>