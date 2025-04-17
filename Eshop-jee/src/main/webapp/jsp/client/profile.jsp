<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Utilisateur_model" %>
<%@ page import="model.Panier_model" %>
<%@ page import="java.util.UUID" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Profil utilisateur sur E-Shop">
    <meta name="keywords" content="E-Shop, Profil, Utilisateur">
    <title>E-Shop - Mon Profil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/client/profileStyle.css">
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

    <!-- Main Content -->
    <main class="main-content">
        <div class="container">
            <h2>Mon Profil</h2>
            <% if (utilisateur != null) { %>
                <!-- Display Success or Error Messages -->
                <% String successMessage = (String) request.getAttribute("successMessage"); %>
                <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
                <% if (successMessage != null) { %>
                    <div class="alert alert-success"><%= successMessage %></div>
                <% } %>
                <% if (errorMessage != null) { %>
                    <div class="alert alert-error"><%= errorMessage %></div>
                <% } %>

                <!-- Profile Information -->
                <div class="profile-info">
                    <div class="profile-image">
                        <% if (utilisateur.getImage() != null && !utilisateur.getImage().isEmpty()) { %>
                            <img src="${pageContext.request.contextPath}/<%= utilisateur.getImage() %>" alt="Image de profil">
                        <% } else { %>
                            <img src="${pageContext.request.contextPath}/images/default-user.png" alt="Image par défaut">
                        <% } %>
                    </div>
                    <div class="profile-details">
                        <p><strong>Nom :</strong> <%= utilisateur.getNom() %></p>
                        <p><strong>Email :</strong> <%= utilisateur.getEmail() %></p>
                        <p><strong>Administrateur :</strong> <%= utilisateur.isEstAdmin() ? "Oui" : "Non" %></p>
                    </div>
                </div>

                <!-- Profile Update Form -->
                <div class="profile-form">
                    <h3>Mettre à jour le profil</h3>
                    <form action="${pageContext.request.contextPath}/profile" method="post" enctype="multipart/form-data" id="profileForm">
                        <% 
                            String csrfToken = (String) session.getAttribute("csrfToken");
                            if (csrfToken == null) {
                                csrfToken = UUID.randomUUID().toString();
                                session.setAttribute("csrfToken", csrfToken);
                            }
                        %>
                        <input type="hidden" name="csrfToken" value="<%= csrfToken %>">
                        <div class="form-group">
                            <label for="nom">Nom :</label>
                            <input type="text" id="nom" name="nom" value="<%= utilisateur.getNom() %>" required>
                        </div>
                        <div class="form-group">
                            <label for="email">Email :</label>
                            <input type="email" id="email" name="email" value="<%= utilisateur.getEmail() %>" required>
                        </div>
                        <div class="form-group">
                            <label for="motDePasse">Nouveau mot de passe (laisser vide pour ne pas changer) :</label>
                            <input type="password" id="motDePasse" name="motDePasse">
                        </div>
                        <div class="form-group">
                            <label for="currentPassword">Mot de passe actuel (requis pour confirmer les modifications) :</label>
                            <input type="password" id="currentPassword" name="currentPassword" required>
                        </div>
                        <div class="form-group">
                            <label for="image">Image de profil :</label>
                            <input type="file" id="image" name="image" accept="image/*">
                        </div>
                        <button type="submit" class="btn btn-submit">Mettre à jour</button>
                    </form>
                </div>
            <% } else { %>
                <p class="no-access">Veuillez vous <a href="${pageContext.request.contextPath}/login">connecter</a> pour accéder à votre profil.</p>
            <% } %>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>© 2025 E-Shop. Tous droits réservés.</p>
        </div>
    </footer>
    
    <script>
        function confirmLogout(event, logoutUrl) {
            event.preventDefault();
            if (confirm("Êtes-vous sûr de vouloir vous déconnecter ?")) {
                window.location.href = logoutUrl;
            }
        }
    </script>
</body>
</html>