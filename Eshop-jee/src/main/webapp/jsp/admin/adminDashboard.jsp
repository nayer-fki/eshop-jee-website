<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.Connection, java.sql.PreparedStatement, java.sql.ResultSet, java.sql.SQLException, java.util.ArrayList, java.util.List, model.DBConnection, model.Commentaire, model.Evaluation, model.UtilisateurDB" %>
<div class="gererCategories">
    <div class="category-management">
        <div class="header-row">
            <h2 class="dashboard-title"><i class="fas fa-tachometer-alt"></i> Tableau de Bord Admin</h2>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/adminDashboardStyles.css">
            <div class="search-bar">
                <form action="${pageContext.request.contextPath}/adminDashboard" method="get">
                    <input type="text" name="query" value="${searchQuery}" placeholder="Rechercher dans les commentaires..." class="search-input">
                    <button type="submit" class="search-btn"><i class="fas fa-search"></i> Rechercher</button>
                </form>
            </div>
        </div>

        <p class="dashboard-description">Cette zone affiche des statistiques et des informations clés pour la gestion de votre boutique.</p>

        <%
            // Initialize variables for statistics
            int totalProducts = 0;
            int totalCategories = 0;
            int totalUsers = 0;
            int totalComments = 0;
            int totalEvaluations = 0;
            int totalOrders = 0;
            List<Commentaire> recentComments = new ArrayList<>();
            List<Evaluation> recentEvaluations = new ArrayList<>();
            List<Object[]> topRatedProducts = new ArrayList<>();
            String error = null;
            UtilisateurDB utilisateurDB = new UtilisateurDB();

            try (Connection conn = DBConnection.getConnection()) {
                // Fetch total products
                String sqlProducts = "SELECT COUNT(*) FROM Produit";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlProducts);
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalProducts = rs.getInt(1);
                    }
                }

                // Fetch total categories
                String sqlCategories = "SELECT COUNT(*) FROM Categorie";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlCategories);
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalCategories = rs.getInt(1);
                    }
                }

                // Fetch total users
                String sqlUsers = "SELECT COUNT(*) FROM Utilisateur";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlUsers);
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalUsers = rs.getInt(1);
                    }
                }

                // Fetch total comments
                String sqlComments = "SELECT COUNT(*) FROM Commentaire";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlComments);
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalComments = rs.getInt(1);
                    }
                }

                // Fetch total evaluations
                String sqlEvaluations = "SELECT COUNT(*) FROM Evaluation";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlEvaluations);
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalEvaluations = rs.getInt(1);
                    }
                }

                // Fetch total orders
                String sqlOrders = "SELECT COUNT(*) FROM Commande";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlOrders);
                     ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalOrders = rs.getInt(1);
                    }
                }

                // Fetch recent comments (top 5)
                String sqlRecentComments = "SELECT c.*, p.nom AS produitNom FROM Commentaire c LEFT JOIN Produit p ON c.produitId = p.id ORDER BY c.dateCreation DESC LIMIT 5";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlRecentComments);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Commentaire commentaire = new Commentaire();
                        commentaire.setId(rs.getString("id"));
                        commentaire.setIdUtilisateur(rs.getString("idUtilisateur"));
                        commentaire.setProduitId(rs.getString("produitId"));
                        commentaire.setProduitNom(rs.getString("produitNom"));
                        commentaire.setCommentaire(rs.getString("commentaire"));
                        commentaire.setDateCreation(rs.getDate("dateCreation"));
                        String userName = utilisateurDB.getUserNameById(commentaire.getIdUtilisateur());
                        commentaire.setIdUtilisateur(userName != null ? userName : "Inconnu");
                        recentComments.add(commentaire);
                    }
                }

                // Fetch recent evaluations (top 5)
                String sqlRecentEvaluations = "SELECT e.*, p.nom AS produitNom FROM Evaluation e LEFT JOIN Produit p ON e.produitId = p.id ORDER BY e.id DESC LIMIT 5";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlRecentEvaluations);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Evaluation evaluation = new Evaluation();
                        evaluation.setId(rs.getString("id"));
                        evaluation.setIdUtilisateur(rs.getString("idUtilisateur"));
                        evaluation.setProduitId(rs.getString("produitId"));
                        evaluation.setProduitNom(rs.getString("produitNom"));
                        evaluation.setNote(rs.getInt("note"));
                        String userName = utilisateurDB.getUserNameById(evaluation.getIdUtilisateur());
                        evaluation.setIdUtilisateur(userName != null ? userName : "Inconnu");
                        recentEvaluations.add(evaluation);
                    }
                }

                // Fetch top-rated products (top 5)
                String sqlTopRated = "SELECT e.produitId, p.nom, AVG(e.note) as avg_rating " +
                                    "FROM Evaluation e JOIN Produit p ON e.produitId = p.id " +
                                    "GROUP BY e.produitId, p.nom " +
                                    "ORDER BY avg_rating DESC LIMIT 5";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlTopRated);
                     ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        topRatedProducts.add(new Object[]{rs.getString("nom"), rs.getDouble("avg_rating")});
                    }
                }

            } catch (SQLException e) {
                error = "Erreur lors de la récupération des données : " + e.getMessage();
            }
        %>

        <!-- Error Message -->
        <div class="message error">
            <% if (error != null) { %>
                <p><i class="fas fa-exclamation-triangle"></i> <%= error %></p>
            <% } %>
        </div>

        <!-- Statistics Section -->
        <div class="dashboard-stats">
            <div class="stat-box stat-box-products">
                <div class="stat-icon"><i class="fas fa-box"></i></div>
                <div class="stat-content">
                    <h3>Produits</h3>
                    <p><%= totalProducts %></p>
                </div>
            </div>
            <div class="stat-box stat-box-categories">
                <div class="stat-icon"><i class="fas fa-list"></i></div>
                <div class="stat-content">
                    <h3>Catégories</h3>
                    <p><%= totalCategories %></p>
                </div>
            </div>
            <div class="stat-box stat-box-users">
                <div class="stat-icon"><i class="fas fa-users"></i></div>
                <div class="stat-content">
                    <h3>Utilisateurs</h3>
                    <p><%= totalUsers %></p>
                </div>
            </div>
            <div class="stat-box stat-box-comments">
                <div class="stat-icon"><i class="fas fa-comments"></i></div>
                <div class="stat-content">
                    <h3>Commentaires</h3>
                    <p><%= totalComments %></p>
                </div>
            </div>
            <div class="stat-box stat-box-evaluations">
                <div class="stat-icon"><i class="fas fa-star"></i></div>
                <div class="stat-content">
                    <h3>Évaluations</h3>
                    <p><%= totalEvaluations %></p>
                </div>
            </div>
            <div class="stat-box stat-box-orders">
                <div class="stat-icon"><i class="fas fa-shopping-cart"></i></div>
                <div class="stat-content">
                    <h3>Commandes</h3>
                    <p><%= totalOrders %></p>
                </div>
            </div>
        </div>

        <!-- Recent Comments Section -->
        <div class="section-wrapper">
            <h3 class="section-title"><i class="fas fa-comments"></i> Commentaires Récents</h3>
            <div class="category-table">
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Utilisateur</th>
                                <th>Produit</th>
                                <th>Commentaire</th>
                                <th>Date de création</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (recentComments != null && !recentComments.isEmpty()) {
                                    for (Commentaire commentaire : recentComments) {
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
                            </tr>
                            <% 
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="4" class="empty-state">
                                    <i class="fas fa-comments"></i>
                                    <p>Aucun commentaire pour le moment. Encouragez vos clients à laisser des avis !</p>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Recent Evaluations Section -->
        <div class="section-wrapper">
            <h3 class="section-title"><i class="fas fa-star"></i> Évaluations Récentes</h3>
            <div class="category-table">
                <div class="table-responsive">
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
                                if (recentEvaluations != null && !recentEvaluations.isEmpty()) {
                                    for (Evaluation evaluation : recentEvaluations) {
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
                                <td colspan="3" class="empty-state">
                                    <i class="fas fa-star"></i>
                                    <p>Aucune évaluation pour le moment. Invitez vos clients à noter vos produits !</p>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- Top-Rated Products Section -->
        <div class="section-wrapper">
            <h3 class="section-title"><i class="fas fa-trophy"></i> Produits les Mieux Notés</h3>
            <div class="category-table">
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Produit</th>
                                <th>Note Moyenne</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
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
                                <td colspan="2" class="empty-state">
                                    <i class="fas fa-trophy"></i>
                                    <p>Aucun produit noté pour le moment. Encouragez vos clients à laisser des évaluations !</p>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>