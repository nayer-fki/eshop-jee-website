<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Produit_model" %>
<%@ page import="model.Categorie_model" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gérer les produits - E-Shop Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/produitAdmin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div class="gererProduits">
    <div class="product-management">
        <h2><i class="fas fa-box"></i> GÉRER LES PRODUITS</h2>
        <div class="filter-bar">
            <form action="${pageContext.request.contextPath}/gererProduits" method="get">
                <select name="categorieId" onchange="this.form.submit()" class="filter-select">
                    <option value="">Toutes les catégories</option>
                    <%
                        List<Categorie_model> categories = (List<Categorie_model>) request.getAttribute("categories");
                        if (categories != null) {
                            for (Categorie_model categorie : categories) {
                    %>
                    <option value="<%= categorie.getId() %>" <%= categorie.getId().equals(request.getAttribute("selectedCategorieId")) ? "selected" : "" %>>
                        <%= categorie.getNom() %>
                    </option>
                    <%      }
                        }
                    %>
                </select>
                <input type="text" name="query" value="${searchQuery}" placeholder="Rechercher par nom..." class="search-input">
                <button type="submit" class="search-btn"><i class="fas fa-search"></i> Rechercher</button>
            </form>
        </div>
        <div class="action-bar">
            <button class="btn-add" onclick="showAddForm()"><i class="fas fa-plus"></i> Ajouter un produit</button>
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

        <!-- Form to Add/Edit Product -->
        <form action="${pageContext.request.contextPath}/gererProduits" method="post" enctype="multipart/form-data" class="product-form" id="productForm">
            <input type="hidden" name="action" id="formAction">
            <input type="hidden" name="id" id="productId">
            <div class="form-group">
                <label for="nom"><i class="fas fa-tag"></i> Nom :</label>
                <input type="text" name="nom" id="nom" placeholder="Entrez le nom du produit" required>
            </div>
            <div class="form-group">
                <label for="description"><i class="fas fa-align-left"></i> Description :</label>
                <textarea name="description" id="description" placeholder="Entrez la description du produit"></textarea>
            </div>
            <div class="form-group">
                <label for="prix"><i class="fas fa-dollar-sign"></i> Prix :</label>
                <input type="number" name="prix" id="prix" step="0.01" placeholder="Entrez le prix" required>
            </div>
            <div class="form-group">
                <label for="quantite"><i class="fas fa-cubes"></i> Quantité :</label>
                <input type="number" name="quantite" id="quantite" placeholder="Entrez la quantité" required>
            </div>
            <div class="form-group">
                <label for="idCategorie"><i class="fas fa-list"></i> Catégorie :</label>
                <select name="idCategorie" id="idCategorie" required>
                    <option value="">Sélectionner une catégorie</option>
                    <%
                        if (categories != null) {
                            for (Categorie_model categorie : categories) {
                    %>
                    <option value="<%= categorie.getId() %>"><%= categorie.getNom() %></option>
                    <%      }
                        }
                    %>
                </select>
            </div>
            <div class="form-group">
                <label for="remise"><i class="fas fa-percentage"></i> Remise (%) :</label>
                <input type="number" name="remise" id="remise" step="0.01" placeholder="Entrez la remise (ex: 20 pour 20%)" min="0" max="100">
            </div>
            <div class="form-group">
                <label for="image"><i class="fas fa-image"></i> Image :</label>
                <input type="file" name="image" id="image" accept="image/*">
                <img id="previewImage" src="" alt="Aperçu de l'image" style="display: none; max-width: 100px; margin-top: 10px;">
            </div>
            <button type="submit" class="btn-add" id="submitBtn"><i class="fas fa-plus"></i> Ajouter le produit</button>
            <button type="button" class="btn-cancel" onclick="hideForm()">Annuler</button>
        </form>

        <!-- Modal for Product Details -->
        <div id="productDetailsModal" class="modal">
            <div class="modal-content">
                <span class="close" onclick="closeModal()">×</span>
                <h2>Détails du produit</h2>
                <div class="modal-body">
                    <p><strong>Nom :</strong> <span id="modalNom"></span></p>
                    <p><strong>Description :</strong> <span id="modalDescription"></span></p>
                    <p><strong>Prix :</strong> <span id="modalPrix"></span></p>
                    <p><strong>Remise :</strong> <span id="modalRemise"></span></p>
                    <p><strong>Prix après remise :</strong> <span id="modalDiscountedPrice"></span></p>
                    <p><strong>Quantité :</strong> <span id="modalQuantite"></span></p>
                    <p><strong>Catégorie :</strong> <span id="modalCategorie"></span></p>
                    <p><strong>Image :</strong></p>
                    <img id="modalImage" src="" alt="Image produit" style="max-width: 200px;">
                </div>
            </div>
        </div>

        <!-- Table of Products -->
        <div class="product-table">
            <table>
                <thead>
                    <tr>
                        <th>Nom</th>
                        <th>Description</th>
                        <th>Prix</th>
                        <th>Remise (%)</th>
                        <th>Quantité</th>
                        <th>Catégorie</th>
                        <th>Image</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Produit_model> produits = (List<Produit_model>) request.getAttribute("produits");
                        if (produits != null && !produits.isEmpty()) {
                            for (Produit_model produit : produits) {
                                String categorieNom = "Inconnue";
                                if (categories != null) {
                                    for (Categorie_model categorie : categories) {
                                        if (categorie.getId().equals(produit.getIdCategorie())) {
                                            categorieNom = categorie.getNom();
                                            break;
                                        }
                                    }
                                }
                    %>
                    <tr>
                        <td><%= produit.getNom() %></td>
                        <td><%= produit.getDescription() != null ? produit.getDescription() : "N/A" %></td>
                        <td><%= produit.getPrix() %></td>
                        <td><%= produit.getRemise() %></td>
                        <td><%= produit.getQuantite() %></td>
                        <td><%= categorieNom %></td>
                        <td>
                            <% if (produit.getImage() != null && !produit.getImage().isEmpty()) { %>
                                <img src="${pageContext.request.contextPath}/<%= produit.getImage() %>" alt="Image produit" style="max-width: 50px;">
                            <% } else { %>
                                <img src="${pageContext.request.contextPath}/images/default.png" alt="Image par défaut" style="max-width: 50px;">
                            <% } %>
                        </td>
                        <td class="actions">
                            <button class="btn-details" onclick="showProductDetails('<%= produit.getId() %>', '<%= produit.getNom() %>', '<%= produit.getDescription() != null ? produit.getDescription() : "" %>', '<%= produit.getPrix() %>', '<%= produit.getRemise() %>', '<%= produit.getDiscountedPrice() %>', '<%= produit.getQuantite() %>', '<%= categorieNom %>', '<%= produit.getImage() %>')">
                                <i class="fas fa-info-circle"></i>
                            </button>
                            <button class="btn-edit" onclick="editProduct('<%= produit.getId() %>', '<%= produit.getNom() %>', '<%= produit.getDescription() != null ? produit.getDescription() : "" %>', '<%= produit.getPrix() %>', '<%= produit.getRemise() %>', '<%= produit.getQuantite() %>', '<%= produit.getIdCategorie() %>', '<%= produit.getImage() %>')">
                                <i class="fas fa-edit"></i>
                            </button>
                            <form action="${pageContext.request.contextPath}/gererProduits" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="supprimer">
                                <input type="hidden" name="id" value="<%= produit.getId() %>">
                                <button type="submit" class="btn-delete" onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </form>
                        </td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr>
                        <td colspan="8">Aucun produit trouvé.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    function showAddForm() {
        document.getElementById('productForm').style.display = 'block';
        document.getElementById('formAction').value = 'ajouter';
        document.getElementById('productId').value = '';
        document.getElementById('nom').value = '';
        document.getElementById('description').value = '';
        document.getElementById('prix').value = '';
        document.getElementById('quantite').value = '';
        document.getElementById('idCategorie').value = '';
        document.getElementById('remise').value = '0';
        document.getElementById('image').value = '';
        document.getElementById('previewImage').style.display = 'none';
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-plus"></i> Ajouter le produit';
    }

    function editProduct(id, nom, description, prix, remise, quantite, idCategorie, image) {
        document.getElementById('productForm').style.display = 'block';
        document.getElementById('formAction').value = 'modifier';
        document.getElementById('productId').value = id;
        document.getElementById('nom').value = nom;
        document.getElementById('description').value = description;
        document.getElementById('prix').value = prix;
        document.getElementById('remise').value = remise;
        document.getElementById('quantite').value = quantite;
        document.getElementById('idCategorie').value = idCategorie;
        if (image) {
            document.getElementById('previewImage').src = '${pageContext.request.contextPath}/' + image;
            document.getElementById('previewImage').style.display = 'block';
        } else {
            document.getElementById('previewImage').style.display = 'none';
        }
        document.getElementById('submitBtn').innerHTML = '<i class="fas fa-save"></i> Mettre à jour';
    }

    function hideForm() {
        document.getElementById('productForm').style.display = 'none';
    }

    function showProductDetails(id, nom, description, prix, remise, discountedPrice, quantite, categorie, image) {
        document.getElementById('modalNom').textContent = nom;
        document.getElementById('modalDescription').textContent = description || 'N/A';
        document.getElementById('modalPrix').textContent = prix + ' €';
        document.getElementById('modalRemise').textContent = remise + '%';
        document.getElementById('modalDiscountedPrice').textContent = discountedPrice + ' €';
        document.getElementById('modalQuantite').textContent = quantite;
        document.getElementById('modalCategorie').textContent = categorie;
        if (image) {
            document.getElementById('modalImage').src = '${pageContext.request.contextPath}/' + image;
            document.getElementById('modalImage').style.display = 'block';
        } else {
            document.getElementById('modalImage').src = '${pageContext.request.contextPath}/images/default.png';
            document.getElementById('modalImage').style.display = 'block';
        }
        document.getElementById('productDetailsModal').style.display = 'block';
    }

    function closeModal() {
        document.getElementById('productDetailsModal').style.display = 'none';
    }

    window.onclick = function(event) {
        const modal = document.getElementById('productDetailsModal');
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    }

    window.onload = function() {
        if ('${error}' !== '' || '${success}' !== '') {
            document.getElementById('productForm').style.display = 'block';
        }

        document.getElementById('image').addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    document.getElementById('previewImage').src = e.target.result;
                    document.getElementById('previewImage').style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        });
    };
</script>
</body>
</html>