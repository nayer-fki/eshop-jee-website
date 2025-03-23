package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AvisDB {
    // Add a new evaluation to the database
    public void ajouterEvaluation(Evaluation evaluation) throws SQLException {
        String sql = "INSERT INTO Evaluation (id, idUtilisateur, produitId, note) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, evaluation.getId());
            pstmt.setString(2, evaluation.getIdUtilisateur());
            pstmt.setString(3, evaluation.getProduitId());
            pstmt.setInt(4, evaluation.getNote());
            pstmt.executeUpdate();
            System.out.println("AvisDB: Added evaluation with ID " + evaluation.getId() + " for product " + evaluation.getProduitId());
        }
    }

    // Add a new comment to the database
    public void ajouterCommentaire(Commentaire commentaire) throws SQLException {
        String sql = "INSERT INTO Commentaire (id, idUtilisateur, produitId, commentaire, dateCreation) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, commentaire.getId());
            pstmt.setString(2, commentaire.getIdUtilisateur());
            pstmt.setString(3, commentaire.getProduitId());
            pstmt.setString(4, commentaire.getCommentaire());
            pstmt.setDate(5, commentaire.getDateCreation());
            pstmt.executeUpdate();
            System.out.println("AvisDB: Added comment with ID " + commentaire.getId() + " for product " + commentaire.getProduitId());
        }
    }

    // Fetch all evaluations for a specific product
    public List<Evaluation> getEvaluationsByProduitId(String produitId) throws SQLException {
        String sql = "SELECT * FROM Evaluation WHERE produitId = ?";
        List<Evaluation> evaluations = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Evaluation evaluation = new Evaluation();
                    evaluation.setId(rs.getString("id"));
                    evaluation.setIdUtilisateur(rs.getString("idUtilisateur"));
                    evaluation.setProduitId(rs.getString("produitId"));
                    evaluation.setNote(rs.getInt("note"));
                    evaluations.add(evaluation);
                }
            }
        }
        return evaluations;
    }

    // Fetch all comments for a specific product
    public List<Commentaire> getCommentairesByProduitId(String produitId) throws SQLException {
        String sql = "SELECT * FROM Commentaire WHERE produitId = ? ORDER BY dateCreation DESC";
        List<Commentaire> commentaires = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Commentaire commentaire = new Commentaire();
                    commentaire.setId(rs.getString("id"));
                    commentaire.setIdUtilisateur(rs.getString("idUtilisateur"));
                    commentaire.setProduitId(rs.getString("produitId"));
                    commentaire.setCommentaire(rs.getString("commentaire"));
                    commentaire.setDateCreation(rs.getDate("dateCreation"));
                    commentaires.add(commentaire);
                }
            }
        }
        return commentaires;
    }

    // Calculate the average rating for a specific product
    public double getAverageRatingByProduitId(String produitId) throws SQLException {
        String sql = "SELECT AVG(note) as averageRating FROM Evaluation WHERE produitId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("averageRating");
                }
            }
        }
        return 0.0; // Return 0 if no ratings exist
    }
}