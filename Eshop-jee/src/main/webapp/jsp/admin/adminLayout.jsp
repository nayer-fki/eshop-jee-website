<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tableau de bord Admin - E-Shop</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/dashboardadmin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha512-Fo3rlrZj/k7ujTnHg4CGR2D7kSs0v4LLanw2qksYuRlEzO+tcaEPQogQ0KaoGN26/zrn20ImR1DfuLWnOo7aBA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <!-- Top Navigation Bar -->
    <nav class="top-nav">
        <div class="nav-brand">E-Shop Admin Panel</div>
        <form class="search-form" action="${pageContext.request.contextPath}/search" method="get" onsubmit="trimSearchInput()">
            <input type="text" name="query" id="searchInput" placeholder="Rechercher..." class="search-input">
            <button type="submit" class="search-btn"><i class="fas fa-search"></i></button>
        </form>
        <div class="nav-actions">
            <a href="javascript:void(0);" onclick="confirmLogout()" class="logout-btn"><i class="fas fa-sign-out-alt"></i> Déconnexion</a>
        </div>
    </nav>

    <!-- Sidebar -->
<div class="sidebar">
    <h3><i class="fas fa-bars"></i> Menu</h3>
    <ul>
        <li><a href="${pageContext.request.contextPath}/adminDashboard" class="<%= request.getRequestURI().endsWith("/adminDashboard") ? "active" : "" %>"><i class="fas fa-tachometer-alt"></i> Tableau de bord</a></li>
        <li><a href="${pageContext.request.contextPath}/gererUtilisateurs" class="<%= request.getRequestURI().endsWith("/gererUtilisateurs") ? "active" : "" %>"><i class="fas fa-users"></i> Gérer les utilisateurs</a></li>
        <li><a href="${pageContext.request.contextPath}/gererProduits" class="<%= request.getRequestURI().endsWith("/gererProduits") ? "active" : "" %>"><i class="fas fa-box"></i> Gérer les produits</a></li>
        <li><a href="${pageContext.request.contextPath}/gererCommandes" class="<%= request.getRequestURI().endsWith("/gererCommandes") ? "active" : "" %>"><i class="fas fa-shopping-cart"></i> Gérer les commandes</a></li>
        <li><a href="${pageContext.request.contextPath}/gererAvis" class="<%= request.getRequestURI().endsWith("/gererAvis") ? "active" : "" %>"><i class="fas fa-comments"></i> Gérer les avis</a></li>
        <li><a href="${pageContext.request.contextPath}/gererCategories" class="<%= request.getRequestURI().endsWith("/gererCategories") ? "active" : "" %>"><i class="fas fa-list"></i> Gérer les catégories</a></li>
        <li><a href="${pageContext.request.contextPath}/gererRemises" class="<%= request.getRequestURI().endsWith("/gererRemises") ? "active" : "" %>"><i class="fas fa-tags"></i> Gérer les remises</a></li>
        <li><a href="${pageContext.request.contextPath}/gererVideo" class="<%= request.getRequestURI().endsWith("/gererVideo") ? "active" : "" %>"><i class="fas fa-video"></i> Gérer la vidéo</a></li>
        <li><a href="${pageContext.request.contextPath}/parametresCompte" class="<%= request.getRequestURI().endsWith("/parametresCompte") ? "active" : "" %>"><i class="fas fa-cog"></i> Paramètres de compte</a></li>
    </ul>
</div>

    <!-- Main Content Area -->
    <div class="main-content">
        <jsp:include page="${page}" />
    </div>

    <script>
        function trimSearchInput() {
            var searchInput = document.getElementById("searchInput");
            searchInput.value = searchInput.value.trim();
        }

        function confirmLogout() {
            if (confirm("Êtes-vous sûr de vouloir vous déconnecter ?")) {
                window.location.href = "${pageContext.request.contextPath}/logout";
            }
        }
    </script>
</body>
</html>