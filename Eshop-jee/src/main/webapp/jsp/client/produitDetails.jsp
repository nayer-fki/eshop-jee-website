<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Produit_model" %>
<%@ page import="model.Categorie_model" %>
<%@ page import="model.Panier_model" %>
<%@ page import="model.Utilisateur_model" %>
<%@ page import="model.Evaluation" %>
<%@ page import="model.Commentaire" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Shop - Détails du produit</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/productDetailsStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
    <!-- Header -->
    <header class="header">
        <div class="container">
            <div class="logo">
                <h1><a href="${pageContext.request.contextPath}/index">E-Shop</a></h1>
            </div>
            <nav class="nav">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/index">Accueil</a></li>
                    <li><a href="${pageContext.request.contextPath}/cart">Panier <i class="fas fa-shopping-cart"></i>
                        <% Panier_model panierHeader = (Panier_model) request.getAttribute("panier"); %>
                        <% if (panierHeader != null && !panierHeader.getItems().isEmpty()) { %>
                            <span class="cart-count"><%= panierHeader.getItems().size() %></span>
                        <% } %>
                    </a></li>
                    <% Utilisateur_model utilisateur = (Utilisateur_model) session.getAttribute("utilisateur"); %>
                    <% if (utilisateur != null) { %>
                        <li><span>Bonjour, <%= utilisateur.getNom() %></span></li>
                        <li><a href="${pageContext.request.contextPath}/logout">Déconnexion</a></li>
                    <% } else { %>
                        <li><a href="${pageContext.request.contextPath}/login">Connexion</a></li>
                    <% } %>
                </ul>
            </nav>
        </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
        <div class="container">
            <%
                Produit_model produit = (Produit_model) request.getAttribute("produit");
                Categorie_model categorie = (Categorie_model) request.getAttribute("categorie");
                if (produit != null) {
            %>
            <div class="product-details">
                <div class="product-details-image">
                    <% if (produit.getImage() != null && !produit.getImage().isEmpty()) { %>
                        <img src="${pageContext.request.contextPath}/<%= produit.getImage() %>" alt="<%= produit.getNom() %>">
                    <% } else { %>
                        <img src="${pageContext.request.contextPath}/images/default.png" alt="Image par défaut">
                    <% } %>
                </div>
                <div class="product-details-info">
                    <h2 class="product-title"><%= produit.getNom() %></h2>
                    <p class="product-category">Catégorie : <%= categorie != null ? categorie.getNom() : "Inconnue" %></p>
                    <div class="price-container">
                        <% if (produit.getRemise() > 0) { %>
                            <p class="product-price original-price"><%= produit.getPrix() %> €</p>
                            <p class="product-price discounted-price"><%= produit.getDiscountedPrice() %> €</p>
                            <p class="discount-info">Remise : <%= produit.getRemise() %>%</p>
                        <% } else { %>
                            <p class="product-price"><%= produit.getPrix() %> €</p>
                        <% } %>
                    </div>
                    <p class="product-description"><%= produit.getDescription() != null ? produit.getDescription() : "Aucune description disponible." %></p>
                    <p class="product-quantity">Quantité disponible : <%= produit.getQuantite() %></p>
                    <div class="product-actions">
                        <form action="${pageContext.request.contextPath}/addToCart" method="post" style="display: inline;">
                            <input type="hidden" name="productId" value="<%= produit.getId() %>">
                            <input type="hidden" name="quantity" value="1">
                            <button type="submit" class="btn btn-secondary add-to-cart">Ajouter au panier</button>
                        </form>
                        <a href="${pageContext.request.contextPath}/index" class="btn btn-primary">Retour à l'accueil</a>
                    </div>
                </div>
            </div>

            <!-- Reviews Section -->
            <div class="reviews-section">
                <h3>Évaluations et Commentaires</h3>
                <%
                    Double averageRating = (Double) request.getAttribute("averageRating");
                    List<Evaluation> evaluations = (List<Evaluation>) request.getAttribute("evaluations");
                    List<Commentaire> commentaires = (List<Commentaire>) request.getAttribute("commentaires");
                %>
                <!-- Display Average Rating -->
                <div class="average-rating">
                    Note moyenne : 
                    <% if (averageRating != null && averageRating > 0) { %>
                        <%= String.format("%.1f", averageRating) %> / 5
                        <span class="stars">
                            <% 
                                int fullStars = (int) Math.floor(averageRating);
                                boolean hasHalfStar = (averageRating - fullStars) >= 0.5;
                                for (int i = 1; i <= 5; i++) {
                                    if (i <= fullStars) {
                            %>
                                <i class="fas fa-star"></i>
                            <%      } else if (hasHalfStar && i == fullStars + 1) { %>
                                <i class="fas fa-star-half-alt"></i>
                            <%      } else { %>
                                <i class="far fa-star"></i>
                            <%      } %>
                            <% } %>
                        </span>
                        (basé sur <%= evaluations.size() %> évaluation<%= evaluations.size() > 1 ? "s" : "" %>)
                    <% } else { %>
                        Aucune évaluation pour le moment.
                    <% } %>
                </div>

                <!-- Display Comments -->
                <div class="comments-list">
                    <% if (commentaires != null && !commentaires.isEmpty()) { %>
                        <% for (Commentaire commentaire : commentaires) { %>
                            <div class="comment">
                                <p><strong>Utilisateur <%= commentaire.getIdUtilisateur() %></strong> - 
                                   <span class="comment-date"><%= commentaire.getDateCreation() %></span></p>
                                <p><%= commentaire.getCommentaire() %></p>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p>Aucun commentaire pour le moment.</p>
                    <% } %>
                </div>

                <!-- Rating and Comment Forms (for logged-in users) -->
                <% if (utilisateur != null) { %>
                    <!-- Rating Form -->
                    <div class="rating-form">
                        <h4>Noter ce produit</h4>
                        <form action="${pageContext.request.contextPath}/submitAvis" method="post">
                            <input type="hidden" name="productId" value="<%= produit.getId() %>">
                            <select name="rating" required>
                                <option value="">Sélectionner une note</option>
                                <option value="1">1 étoile</option>
                                <option value="2">2 étoiles</option>
                                <option value="3">3 étoiles</option>
                                <option value="4">4 étoiles</option>
                                <option value="5">5 étoiles</option>
                            </select>
                            <button type="submit" class="btn btn-submit">Soumettre la note</button>
                        </form>
                    </div>

                    <!-- Comment Form -->
                    <div class="comment-form">
                        <h4>Laisser un commentaire</h4>
                        <form action="${pageContext.request.contextPath}/submitAvis" method="post">
                            <input type="hidden" name="productId" value="<%= produit.getId() %>">
                            <textarea name="comment" rows="4" placeholder="Écrivez votre commentaire ici..." required></textarea>
                            <button type="submit" class="btn btn-submit">Soumettre le commentaire</button>
                        </form>
                    </div>
                <% } else { %>
                    <p><a href="${pageContext.request.contextPath}/login">Connectez-vous</a> pour laisser une évaluation ou un commentaire.</p>
                <% } %>
            </div>
            <% } else { %>
            <p class="no-product">Produit non trouvé.</p>
            <% } %>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>© 2025 E-Shop. Tous droits réservés.</p>
        </div>
    </footer>
</body>
</html>