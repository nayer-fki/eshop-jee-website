<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Categorie_model" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gérer les catégories - E-Shop Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/categoryAdmin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div class="gererCategories">
    <div class="category-management">
        <h2><i class="fas fa-list"></i> GÉRER LES CATÉGORIES</h2>
        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/gererCategories" method="get">
                <input type="text" name="query" value="${searchQuery}" placeholder="Rechercher par nom..." class="search-input">
                <button type="submit" class="search-btn"><i class="fas fa-search"></i> Rechercher</button>
            </form>
        </div>
        <div class="action-bar">
            <button class="btn-add" onclick="showAddForm()"><i class="fas fa-plus"></i> Ajouter une catégorie</button>
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

        <!-- Form to Add/Edit Category -->
        <form action="${pageContext.request.contextPath}/gererCategories" method="post" class="category-form" id="categoryForm">
            <input type="hidden" name="action" id="formAction">
            <input type="hidden" name="id" id="categoryId">
            <div class="form-group">
                <label for="nomCategorie"><i class="fas fa-tag"></i> Nom de la catégorie :</label>
                <input type="text" name="nomCategorie" id="nomCategorie" placeholder="Entrez le nom de la catégorie" required>
            </div>
            <button type="submit" class="btn-add" id="submitBtn"><i class="fas fa-plus"></i> Ajouter la catégorie</button>
            <button type="button" class="btn-cancel" onclick="hideForm()">Annuler</button>
        </form>

        <!-- Table of Categories -->
        <div class="category-table">
            <table>
                <thead>
                    <tr>
                        
                        <th>Nom</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Categorie_model> categories = (List<Categorie_model>) request.getAttribute("categories");
                        if (categories != null && !categories.isEmpty()) {
                            for (Categorie_model categorie : categories) {
                    %>
                    <tr>
                        
                        <td><%= categorie.getNom() %></td>
                        <td>
                            <button class="btn-edit" onclick="editCategory('<%= categorie.getId() %>', '<%= categorie.getNom() %>')"><i class="fas fa-edit"></i> Modifier</button>
                            <form action="${pageContext.request.contextPath}/gererCategories" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="supprimer">
                                <input type="hidden" name="id" value="<%= categorie.getId() %>">
                                <button type="submit" class="btn-delete" onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette catégorie ?')"><i class="fas fa-trash"></i> Supprimer</button>
                            </form>
                        </td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr>
                        <td colspan="3">Aucune catégorie trouvée.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    function showAddForm() {
        document.getElementById('categoryForm').style.display = 'block';
        document.getElementById('formAction').value = 'ajouter';
        document.getElementById('categoryId').value = '';
        document.getElementById('nomCategorie').value = '';
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-plus"></i> Ajouter la catégorie';
    }

    function editCategory(id, nom) {
        document.getElementById('categoryForm').style.display = 'block';
        document.getElementById('formAction').value = 'modifier';
        document.getElementById('categoryId').value = id;
        document.getElementById('nomCategorie').value = nom;
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Mettre à jour';
    }

    function hideForm() {
        document.getElementById('categoryForm').style.display = 'none';
    }

    // Show form if there was an error during submission
    window.onload = function() {
        if ('${error}' !== '' || '${success}' !== '') {
            document.getElementById('categoryForm').style.display = 'block';
        }
    };
</script>
</body>
</html>