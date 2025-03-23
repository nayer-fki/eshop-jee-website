<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Commande_model" %>
<%@ page import="model.Panier_model" %>
<%@ page import="model.Utilisateur_model" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Shop - Historique des commandes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/clientStyle.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        .orders-container {
            max-width: 1000px;
            margin: 50px auto;
            padding: 30px;
            background: linear-gradient(135deg, #ffffff, #f1f5f9);
            border-radius: 12px;
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
        }

        .orders-container h2 {
            font-size: 2.2rem;
            color: #2d3748;
            text-align: center;
            margin-bottom: 30px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .order-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .order-table th,
        .order-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
        }

        .order-table th {
            background: #edf2f7;
            font-weight: 600;
        }

        .order-table td {
            font-size: 0.95rem;
            color: #4a5568;
        }

        .no-orders {
            text-align: center;
            color: #e53e3e;
            font-size: 1.1rem;
            margin-top: 20px;
        }
    </style>
</head>
<body>
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

    <main class="main-content">
        <div class="orders-container">
            <h2>Historique des commandes</h2>
            <%
                List<Commande_model> commandes = (List<Commande_model>) request.getAttribute("commandes");
                if (commandes != null && !commandes.isEmpty()) {
            %>
            <table class="order-table">
                <thead>
                    <tr>
                        <th>ID Commande</th>
                        <th>Date</th>
                        <th>Produits</th>
                        <th>Total</th>
                        <th>Statut</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (Commande_model commande : commandes) {
                    %>
                    <tr>
                        <td><%= commande.getId() %></td>
                        <td><%= commande.getDateCommande() %></td>
                        <td><%= commande.getProduits() %></td>
                        <td><%= commande.getPrixTotal() %> €</td>
                        <td><%= commande.getStatut() %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <% } else { %>
            <p class="no-orders">Aucune commande trouvée.</p>
            <% } %>
        </div>
    </main>

    <footer class="footer">
        <div class="container">
            <p>© 2025 E-Shop. Tous droits réservés.</p>
        </div>
    </footer>
</body>
</html>