<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Panier_model" %>
<%@ page import="model.PanierItem_model" %>
<%@ page import="model.Utilisateur_model" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Shop - Paiement</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/client/clientStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        .checkout-container {
            max-width: 800px;
            margin: 50px auto;
            padding: 30px;
            background: linear-gradient(135deg, #ffffff, #f1f5f9);
            border-radius: 12px;
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
        }

        .checkout-container h2 {
            font-size: 2.2rem;
            color: #2d3748;
            text-align: center;
            margin-bottom: 30px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .checkout-form {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .form-group label {
            font-size: 1.1rem;
            color: #4a5568;
            font-weight: 500;
        }

        .form-group input,
        .form-group textarea {
            padding: 12px;
            font-size: 1rem;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            outline: none;
            transition: border-color 0.3s ease;
        }

        .form-group input:focus,
        .form-group textarea:focus {
            border-color: #4CAF50;
        }

        .order-summary {
            margin-top: 30px;
            padding: 20px;
            background: #f8fafc;
            border-radius: 8px;
        }

        .order-summary h3 {
            font-size: 1.5rem;
            color: #2d3748;
            margin-bottom: 15px;
        }

        .order-summary table {
            width: 100%;
            border-collapse: collapse;
        }

        .order-summary th,
        .order-summary td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
        }

        .order-summary th {
            background: #edf2f7;
            font-weight: 600;
        }

        .order-total {
            margin-top: 15px;
            font-size: 1.2rem;
            font-weight: 600;
            text-align: right;
        }

        .confirm-btn {
            padding: 14px;
            background: linear-gradient(90deg, #4CAF50, #2ecc71);
            color: #fff;
            border: none;
            border-radius: 6px;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }

        .confirm-btn:hover {
            background: linear-gradient(90deg, #45a049, #27ae60);
            transform: translateY(-2px);
        }
    </style>
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
        <div class="checkout-container">
            <h2>Finaliser votre commande</h2>
            <form action="${pageContext.request.contextPath}/checkout" method="post" class="checkout-form">
                <div class="form-group">
                    <label for="adresse">Adresse de livraison</label>
                    <textarea id="adresse" name="adresse" rows="4" required></textarea>
                </div>
                <div class="form-group">
                    <label for="methodePaiement">Méthode de paiement</label>
                    <input type="text" id="methodePaiement" name="methodePaiement" placeholder="Ex: Carte de crédit, PayPal" required>
                </div>

                <div class="order-summary">
                    <h3>Récapitulatif de la commande</h3>
                    <%
                        Panier_model panier = (Panier_model) request.getAttribute("panier");
                        if (panier != null && !panier.getItems().isEmpty()) {
                    %>
                    <table>
                        <thead>
                            <tr>
                                <th>Produit</th>
                                <th>Quantité</th>
                                <th>Prix unitaire</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (PanierItem_model item : panier.getItems()) {
                                    if (item != null && item.getProduit() != null) {
                            %>
                            <tr>
                                <td><%= item.getProduit().getNom() %></td>
                                <td><%= item.getQuantite() %></td>
                                <td><%= item.getPrixUnitaire() %> €</td>
                                <td><%= item.getPrixUnitaire() * item.getQuantite() %> €</td>
                            </tr>
                            <%      }
                                }
                            %>
                        </tbody>
                    </table>
                    <div class="order-total">
                        Total : <%= panier.getTotal() %> €
                    </div>
                    <% } else { %>
                    <p>Votre panier est vide.</p>
                    <% } %>
                </div>

                <button type="submit" class="confirm-btn">Confirmer la commande</button>
            </form>
        </div>
    </main>

    <footer class="footer">
        <div class="container">
            <p>© 2025 E-Shop. Tous droits réservés.</p>
        </div>
    </footer>
        <script>
        function confirmLogout(event, logoutUrl) {
            event.preventDefault(); // Prevent the default link behavior
            if (confirm("Êtes-vous sûr de vouloir vous déconnecter ?")) {
                window.location.href = logoutUrl; // Proceed with logout
            }
        }
    </script>
</body>
</html>