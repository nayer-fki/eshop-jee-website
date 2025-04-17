package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AvisDB;
import model.CategorieDB;
import model.Categorie_model;
import model.Commentaire;
import model.Evaluation;
import model.ProduitDB;
import model.UtilisateurDB;
import model.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet("/gererAvis")
public class GererAvisServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GererAvisServlet.class.getName());
    private AvisDB avisDB;
    private UtilisateurDB utilisateurDB;
    private ProduitDB produitDB;
    private CategorieDB categorieDB;

    @Override
    public void init() throws ServletException {
        avisDB = new AvisDB();
        utilisateurDB = new UtilisateurDB();
        produitDB = new ProduitDB();
        categorieDB = new CategorieDB();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null || !(boolean) session.getAttribute("admin")) {
            response.sendRedirect(request.getContextPath() + "/LoginAdmin");
            return;
        }

        try {
            // Fetch categories for the filter
            List<Categorie_model> categories = categorieDB.listerCategories();
            request.setAttribute("categories", categories != null ? categories : new ArrayList<>());

            // Fetch all products for the product filter
            List<Object[]> products = getAllProducts();
            request.setAttribute("products", products != null ? products : new ArrayList<>());

            // Get search and filter parameters
            String query = request.getParameter("query");
            String categoryId = request.getParameter("categoryId");
            String productId = request.getParameter("productId");
            request.setAttribute("searchQuery", query);
            request.setAttribute("categoryId", categoryId);
            request.setAttribute("productId", productId);

            // Fetch comments and evaluations with filters
            List<Commentaire> commentaires = avisDB.getCommentairesByProduitId(productId, 1, Integer.MAX_VALUE);
            List<Evaluation> evaluations = avisDB.getEvaluationsByProduitId(null);

            // Filter by product name (query) and category
            if (query != null && !query.trim().isEmpty()) {
                query = java.net.URLDecoder.decode(query, "UTF-8").trim();
                final String searchQuery = query.toLowerCase();
                commentaires = commentaires.stream()
                    .filter(c -> c.getProduitNom() != null && c.getProduitNom().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
                evaluations = evaluations.stream()
                    .filter(e -> e.getProduitNom() != null && e.getProduitNom().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
            }
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                List<String> productIdsInCategory = getProductIdsByCategory(categoryId);
                commentaires = commentaires.stream()
                    .filter(c -> productIdsInCategory.contains(c.getProduitId()))
                    .collect(Collectors.toList());
                evaluations = evaluations.stream()
                    .filter(e -> productIdsInCategory.contains(e.getProduitId()))
                    .collect(Collectors.toList());
            }

            // Add user names to comments and evaluations
            for (Commentaire commentaire : commentaires) {
                String userName = utilisateurDB.getUserNameById(commentaire.getIdUtilisateur());
                commentaire.setIdUtilisateur(userName != null ? userName : "Inconnu");
            }
            for (Evaluation evaluation : evaluations) {
                String userName = utilisateurDB.getUserNameById(evaluation.getIdUtilisateur());
                evaluation.setIdUtilisateur(userName != null ? userName : "Inconnu");
            }

            // Fetch top-rated and lowest-rated products
            List<Object[]> topRatedProducts = getTopRatedProducts(3); // Top 3
            List<Object[]> lowRatedProducts = getLowRatedProducts(3); // Bottom 3
            request.setAttribute("topRatedProducts", topRatedProducts != null ? topRatedProducts : new ArrayList<>());
            request.setAttribute("lowRatedProducts", lowRatedProducts != null ? lowRatedProducts : new ArrayList<>());

            request.setAttribute("commentaires", commentaires != null ? commentaires : new ArrayList<>());
            request.setAttribute("evaluations", evaluations != null ? evaluations : new ArrayList<>());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des avis", e);
            request.setAttribute("error", "Erreur lors de la récupération des avis : " + e.getMessage());
        }

        request.setAttribute("page", "/jsp/admin/gererAvis.jsp");
        request.getRequestDispatcher("/jsp/admin/adminLayout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null || !(boolean) session.getAttribute("admin")) {
            response.sendRedirect(request.getContextPath() + "/LoginAdmin");
            return;
        }

        String action = request.getParameter("action");
        if ("deleteComment".equals(action)) {
            String commentId = request.getParameter("commentId");
            try {
                avisDB.deleteComment(commentId);
                request.setAttribute("success", "Commentaire supprimé avec succès.");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du commentaire", e);
                request.setAttribute("error", "Erreur lors de la suppression du commentaire : " + e.getMessage());
            }
        }

        // Refresh the page by re-fetching data
        doGet(request, response);
    }

    private List<Object[]> getAllProducts() throws SQLException {
        List<Object[]> products = new ArrayList<>();
        String sql = "SELECT id, nom FROM Produit ORDER BY nom";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                products.add(new Object[]{rs.getString("id"), rs.getString("nom")});
            }
        }
        return products;
    }

    private List<String> getProductIdsByCategory(String categoryId) throws SQLException {
        List<String> productIds = new ArrayList<>();
        String sql = "SELECT id FROM Produit WHERE categorieId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productIds.add(rs.getString("id"));
                }
            }
        }
        return productIds;
    }

    private List<Object[]> getTopRatedProducts(int limit) throws SQLException {
        List<Object[]> topRated = new ArrayList<>();
        String sql = "SELECT e.produitId, p.nom, AVG(e.note) as avg_rating " +
                     "FROM Evaluation e JOIN Produit p ON e.produitId = p.id " +
                     "GROUP BY e.produitId, p.nom " +
                     "ORDER BY avg_rating DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    topRated.add(new Object[]{rs.getString("nom"), rs.getDouble("avg_rating")});
                }
            }
        }
        return topRated;
    }

    private List<Object[]> getLowRatedProducts(int limit) throws SQLException {
        List<Object[]> lowRated = new ArrayList<>();
        String sql = "SELECT e.produitId, p.nom, AVG(e.note) as avg_rating " +
                     "FROM Evaluation e JOIN Produit p ON e.produitId = p.id " +
                     "GROUP BY e.produitId, p.nom " +
                     "ORDER BY avg_rating ASC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lowRated.add(new Object[]{rs.getString("nom"), rs.getDouble("avg_rating")});
                }
            }
        }
        return lowRated;
    }
}