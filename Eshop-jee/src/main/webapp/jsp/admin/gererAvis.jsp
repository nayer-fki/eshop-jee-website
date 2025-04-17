<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.Commentaire, model.Evaluation, model.Categorie_model" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gérer les avis - E-Shop Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/categoryAdmin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<div class="gererCategories">
    <div class="category-management">
        <h2><i class="fas fa-comments"></i> GÉRER LES AVIS</h2>
        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/gererAvis" method="get">
                <input type="text" name="query" value="${searchQuery != null ? searchQuery : ''}" placeholder="Rechercher par nom de produit..." class="search-input">
                <select name="categoryId" class="search-input">
                    <option value="">Toutes les catégories</option>
                    <%
                        List<Categorie_model> categories = (List<Categorie_model>) request.getAttribute("categories");
                        String selectedCategoryId = (String) request.getAttribute("categoryId");
                        if (categories != null && !categories.isEmpty()) {
                            for (Categorie_model category : categories) {
                                String selected = (selectedCategoryId != null && category.getId().equals(selectedCategoryId)) ? "selected" : "";
                    %>
                    <option value="<%= category.getId() %>" <%= selected %>><%= category.getNom() %></option>
                    <%      }
                        } else { %>
                    <option value="">Aucune catégorie disponible</option>
                    <% } %>
                </select>
                <button type="submit" class="search-btn"><i class="fas fa-search"></i> Rechercher</button>
            </form>
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

        <!-- Top and Low Rated Products -->
        <div class="rated-products">
            <h3>Produits les mieux notés</h3>
            <table>
                <thead>
                    <tr>
                        <th>Produit</th>
                        <th>Note moyenne</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Object[]> topRatedProducts = (List<Object[]>) request.getAttribute("topRatedProducts");
                        if (topRatedProducts != null && !topRatedProducts.isEmpty()) {
                            for (Object[] product : topRatedProducts) {
                                String productName = product[0] != null ? product[0].toString() : "Inconnu";
                                Double avgRating = (Double) product[1];
                    %>
                    <tr>
                        <td><%= productName %></td>
                        <td><%= avgRating != null ? String.format("%.2f", avgRating) : "N/A" %></td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr>
                        <td colspan="2">Aucun produit noté trouvé.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>

            <h3>Produits les moins bien notés</h3>
            <table>
                <thead>
                    <tr>
                        <th>Produit</th>
                        <th>Note moyenne</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Object[]> lowRatedProducts = (List<Object[]>) request.getAttribute("lowRatedProducts");
                        if (lowRatedProducts != null && !lowRatedProducts.isEmpty()) {
                            for (Object[] product : lowRatedProducts) {
                                String productName = product[0] != null ? product[0].toString() : "Inconnu";
                                Double avgRating = (Double) product[1];
                    %>
                    <tr>
                        <td><%= productName %></td>
                        <td><%= avgRating != null ? String.format("%.2f", avgRating) : "N/A" %></td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr>
                        <td colspan="2">Aucun produit noté trouvé.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <!-- Comments Section -->
        <h3>Commentaires</h3>
        <div class="category-table">
            <form action="${pageContext.request.contextPath}/gererAvis" method="get" class="search-bar">
                <select name="productId" class="search-input">
                    <option value="">Tous les produits</option>
                    <%
                        List<Object[]> products = (List<Object[]>) request.getAttribute("products");
                        String selectedProductId = (String) request.getAttribute("productId");
                        if (products != null && !products.isEmpty()) {
                            for (Object[] product : products) {
                                String productId = (String) product[0];
                                String productName = (String) product[1];
                                String selected = (selectedProductId != null && productId.equals(selectedProductId)) ? "selected" : "";
                    %>
                    <option value="<%= productId %>" <%= selected %>><%= productName != null ? productName : "Inconnu" %></option>
                    <%      }
                        } else { %>
                    <option value="">Aucun produit disponible</option>
                    <% } %>
                </select>
                <button type="submit" class="search-btn"><i class="fas fa-filter"></i> Filtrer</button>
            </form>
            <table>
                <thead>
                    <tr>
                        <th>Utilisateur</th>
                        <th>Produit</th>
                        <th>Commentaire</th>
                        <th>Date de création</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Commentaire> commentaires = (List<Commentaire>) request.getAttribute("commentaires");
                        if (commentaires != null && !commentaires.isEmpty()) {
                            for (Commentaire commentaire : commentaires) {
                                String userName = commentaire.getIdUtilisateur() != null ? commentaire.getIdUtilisateur() : "Inconnu";
                                String productName = commentaire.getProduitNom() != null ? commentaire.getProduitNom() : "Inconnu";
                                String commentText = commentaire.getCommentaire() != null ? commentaire.getCommentaire() : "";
                                String dateCreation = commentaire.getDateCreation() != null ? commentaire.getDateCreation().toString() : "N/A";
                    %>
                    <tr>
                        <td><%= userName %></td>
                        <td><%= productName %></td>
                        <td><%= commentText %></td>
                        <td><%= dateCreation %></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/gererAvis" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="deleteComment">
                                <input type="hidden" name="commentId" value="<%= commentaire.getId() != null ? commentaire.getId() : "" %>">
                                <button type="submit" class="btn-delete" onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce commentaire ?');">
                                    <i class="fas fa-trash"></i> Supprimer
                                </button>
                            </form>
                        </td>
                    </tr>
                    <% 
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="5">Aucun commentaire trouvé.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

        <!-- Evaluations Section -->
        <h3>Évaluations</h3>
        <div class="category-table">
            <table>
                <thead>
                    <tr>
                        <th>Utilisateur</th>
                        <th>Produit</th>
                        <th>Note</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Evaluation> evaluations = (List<Evaluation>) request.getAttribute("evaluations");
                        if (evaluations != null && !evaluations.isEmpty()) {
                            for (Evaluation evaluation : evaluations) {
                                String userName = evaluation.getIdUtilisateur() != null ? evaluation.getIdUtilisateur() : "Inconnu";
                                String productName = evaluation.getProduitNom() != null ? evaluation.getProduitNom() : "Inconnu";
                                String note = String.valueOf(evaluation.getNote());
                    %>
                    <tr>
                        <td><%= userName %></td>
                        <td><%= productName %></td>
                        <td><%= note %></td>
                    </tr>
                    <% 
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="3">Aucune évaluation trouvée.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>