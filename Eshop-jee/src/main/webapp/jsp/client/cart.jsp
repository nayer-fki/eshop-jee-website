<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Panier_model" %>
<%@ page import="model.PanierItem_model" %>
<%@ page import="model.Utilisateur_model" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Shop - Panier</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/client/clientStyle.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/client/cart.css">
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
                        <li><a href="${pageContext.request.contextPath}/profile">Mon Profil</a></li>
                        <li><a href="${pageContext.request.contextPath}/orders">Mes commandes</a></li>
                        <li><span>Bonjour, <%= utilisateur.getNom() %></span></li>
                        <li><a href="#" onclick="confirmLogout(event, '${pageContext.request.contextPath}/clientLogout')">Déconnexion</a></li>
                    <% } else { %>
                        <li><a href="${pageContext.request.contextPath}/login">Connexion</a></li>
                    <% } %>
                </ul>
            </nav>
        </div>
    </header>
    <main class="main-content">
        <div class="cart-container">
            <h2>Votre Panier</h2>
            <%
                Panier_model panier = (Panier_model) request.getAttribute("panier");
                if (panier == null) {
                    out.println("<p class='no-products'>Erreur : Le panier est null.</p>");
                } else if (panier.getItems() == null || panier.getItems().isEmpty()) {
                    out.println("<p class='no-products'>Votre panier est vide.</p>");
                } else {
                    out.println("<p>Nombre d'articles dans le panier : " + panier.getItems().size() + "</p>");
            %>
            <table class="cart-table">
                <thead>
                    <tr>
                        <th>Produit</th>
                        <th>Prix Unitaire</th>
                        <th>Quantité</th>
                        <th>Total</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (PanierItem_model item : panier.getItems()) {
                            if (item == null || item.getProduit() == null) {
                                out.println("<tr><td colspan='5'>Erreur : Article ou produit null dans le panier.</td></tr>");
                                continue;
                            }
                    %>
                    <tr>
                        <td data-label="Produit" class="product-name"><%= item.getProduit().getNom() %></td>
                        <td data-label="Prix Unitaire" class="price"><%= item.getPrixUnitaire() %> €</td>
                        <td data-label="Quantité" class="quantity">
                            <form action="${pageContext.request.contextPath}/updateCartQuantity" method="post" class="quantity-form">
                                <input type="hidden" name="productId" value="<%= item.getProduit().getId() %>">
                                <div class="quantity-control">
                                    <button type="button" class="quantity-btn decrease" title="Diminuer la quantité"><i class="fas fa-minus"></i></button>
                                    <input type="number" name="quantity" value="<%= item.getQuantite() %>" min="1" class="quantity-input" readonly>
                                    <button type="button" class="quantity-btn increase" title="Augmenter la quantité"><i class="fas fa-plus"></i></button>
                                </div>
                            </form>
                        </td>
                        <td data-label="Total" class="total"><%= item.getPrixUnitaire() * item.getQuantite() %> €</td>
                        <td data-label="Action" class="action">
                            <a href="${pageContext.request.contextPath}/removeFromCart?productId=<%= item.getProduit().getId() %>">Supprimer</a>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <div class="cart-total">
                <p>Total : <%= panier.getTotal() %> €</p>
                <a href="${pageContext.request.contextPath}/checkout" class="btn-checkout">Passer la commande</a>
            </div>
            <% } %>
        </div>
    </main>

    <footer class="footer">
        <div class="container">
            <p>© 2025 E-Shop. Tous droits réservés.</p>
        </div>
    </footer>

    <script>
        // JavaScript to handle increment, decrement, and auto-submit
        document.querySelectorAll('.quantity-control').forEach(control => {
            const form = control.closest('.quantity-form');
            const input = control.querySelector('.quantity-input');
            const decreaseBtn = control.querySelector('.decrease');
            const increaseBtn = control.querySelector('.increase');

            const updateQuantity = () => {
                // Show loading state
                input.classList.add('loading');
                decreaseBtn.disabled = true;
                increaseBtn.disabled = true;

                // Submit the form
                form.submit();
            };

            decreaseBtn.addEventListener('click', () => {
                let value = parseInt(input.value);
                if (value > 1) {
                    input.value = value - 1;
                    updateQuantity();
                }
            });

            increaseBtn.addEventListener('click', () => {
                let value = parseInt(input.value);
                input.value = value + 1;
                updateQuantity();
            });

            // Prevent manual input since the field is readonly
            input.addEventListener('change', () => {
                if (input.value < 1) {
                    input.value = 1;
                }
            });
        });

        function confirmLogout(event, logoutUrl) {
            event.preventDefault();
            if (confirm("Êtes-vous sûr de vouloir vous déconnecter ?")) {
                window.location.href = logoutUrl;
            }
        }
    </script>
</body>
</html>