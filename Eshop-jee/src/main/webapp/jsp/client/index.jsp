<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Produit_model" %>
<%@ page import="model.Categorie_model" %>
<%@ page import="model.Panier_model" %>
<%@ page import="model.Utilisateur_model" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Shop - Accueil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/clientStyle.css">
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
            <li><a href="${pageContext.request.contextPath}/orders">Mes commandes</a></li>
            <li><span>Bonjour, <%= utilisateur.getNom() %></span></li>
            <li><a href="${pageContext.request.contextPath}/logout">Déconnexion</a></li>
        <% } else { %>
            <li><a href="${pageContext.request.contextPath}/login">Connexion</a></li>
        <% } %>
    </ul>
</nav>
        </div>
    </header>

    <!-- Hero Section -->
    <section class="hero">
        <div class="container">
            <div class="hero-content">
                <h1>Bienvenue chez E-Shop</h1>
                <p>Découvrez nos meilleures offres et produits exclusifs !</p>
                <a href="#products" class="btn btn-primary">Voir les produits</a>
            </div>
        </div>
    </section>

    <!-- Featured Products (Discounted Products) -->
    <section class="featured-products">
        <div class="container">
            <h2>Offres Spéciales</h2>
            <div class="product-grid">
                <%
                    List<Produit_model> discountedProduits = (List<Produit_model>) request.getAttribute("discountedProduits");
                    if (discountedProduits != null && !discountedProduits.isEmpty()) {
                        for (Produit_model produit : discountedProduits) {
                %>
                <div class="product-card">
                    <div class="product-image">
                        <% if (produit.getImage() != null && !produit.getImage().isEmpty()) { %>
                            <img src="${pageContext.request.contextPath}/<%= produit.getImage() %>" alt="<%= produit.getNom() %>">
                        <% } else { %>
                            <img src="${pageContext.request.contextPath}/images/default.png" alt="Image par défaut">
                        <% } %>
                        <% if (produit.getRemise() > 0) { %>
                            <span class="discount-badge">-${produit.getRemise()}%</span>
                        <% } %>
                    </div>
                    <div class="product-info">
                        <h3 class="product-title"><%= produit.getNom() %></h3>
                        <div class="price-container">
                            <% if (produit.getRemise() > 0) { %>
                                <p class="product-price original-price"><%= produit.getPrix() %> €</p>
                                <p class="product-price discounted-price"><%= produit.getDiscountedPrice() %> €</p>
                                <p class="discount-info">Remise : <%= produit.getRemise() %>%</p>
                            <% } else { %>
                                <p class="product-price"><%= produit.getPrix() %> €</p>
                            <% } %>
                        </div>
                        <div class="product-actions">
                            <a href="${pageContext.request.contextPath}/produitDetails?id=<%= produit.getId() %>" class="btn btn-primary">Voir les détails</a>
                            <form action="${pageContext.request.contextPath}/addToCart" method="post" style="display: inline;">
                                <input type="hidden" name="productId" value="<%= produit.getId() %>">
                                <input type="hidden" name="quantity" value="1">
                                <button type="submit" class="btn btn-secondary add-to-cart">Ajouter au panier</button>
                            </form>
                        </div>
                    </div>
                </div>
                <%      }
                    } else { %>
                <p class="no-products">Aucune offre spéciale disponible pour le moment.</p>
                <% } %>
            </div>
        </div>
    </section>

    <!-- Video Section -->
    <section class="video-section">
        <div class="container">
            <h2>Découvrez Nos Produits</h2>
            <div class="video-container">
                <iframe width="100%" height="400" src="https://www.youtube.com/embed/dQw4w9WgXcQ" title="Promotional Video" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
            </div>
        </div>
    </section>

    <!-- Main Content (All Products) -->
    <section class="products" id="products">
        <div class="container">
            <h2>Tous les Produits</h2>
            <!-- Filter and Search Bar -->
            <div class="filter-search-bar">
                <form action="${pageContext.request.contextPath}/index" method="get" class="filter-form">
                    <div class="filter-group">
                        <label for="categorieId">Catégorie :</label>
                        <select name="categorieId" id="categorieId" onchange="this.form.submit()">
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
                    </div>
                    <div class="search-group">
                        <input type="text" name="query" value="${searchQuery}" placeholder="Rechercher un produit..." class="search-input">
                        <button type="submit" class="search-btn"><i class="fas fa-search"></i></button>
                    </div>
                </form>
            </div>

            <!-- Product Grid -->
            <div class="product-grid">
                <%
                    List<Produit_model> produits = (List<Produit_model>) request.getAttribute("produits");
                    if (produits != null && !produits.isEmpty()) {
                        for (Produit_model produit : produits) {
                %>
                <div class="product-card">
                    <div class="product-image">
                        <% if (produit.getImage() != null && !produit.getImage().isEmpty()) { %>
                            <img src="${pageContext.request.contextPath}/<%= produit.getImage() %>" alt="<%= produit.getNom() %>">
                        <% } else { %>
                            <img src="${pageContext.request.contextPath}/images/default.png" alt="Image par défaut">
                        <% } %>
                        <% if (produit.getRemise() > 0) { %>
                            <span class="discount-badge">-${produit.getRemise()}%</span>
                        <% } %>
                    </div>
                    <div class="product-info">
                        <h3 class="product-title"><%= produit.getNom() %></h3>
                        <div class="price-container">
                            <% if (produit.getRemise() > 0) { %>
                                <p class="product-price original-price"><%= produit.getPrix() %> €</p>
                                <p class="product-price discounted-price"><%= produit.getDiscountedPrice() %> €</p>
                                <p class="discount-info">Remise : <%= produit.getRemise() %>%</p>
                            <% } else { %>
                                <p class="product-price"><%= produit.getPrix() %> €</p>
                            <% } %>
                        </div>
                        <div class="product-actions">
                            <a href="${pageContext.request.contextPath}/produitDetails?id=<%= produit.getId() %>" class="btn btn-primary">Voir les détails</a>
                            <form action="${pageContext.request.contextPath}/addToCart" method="post" style="display: inline;">
                                <input type="hidden" name="productId" value="<%= produit.getId() %>">
                                <input type="hidden" name="quantity" value="1">
                                <button type="submit" class="btn btn-secondary add-to-cart">Ajouter au panier</button>
                            </form>
                        </div>
                    </div>
                </div>
                <%      }
                    } else { %>
                <p class="no-products">Aucun produit trouvé.</p>
                <% } %>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>© 2025 E-Shop. Tous droits réservés.</p>
        </div>
    </footer>
</body>
</html>