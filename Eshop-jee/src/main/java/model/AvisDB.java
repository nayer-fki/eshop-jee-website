package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AvisDB {
    public void ajouterEvaluation(Evaluation evaluation) throws SQLException {
        String sql = "INSERT INTO Evaluation (id, idUtilisateur, produitId, note) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, evaluation.getId());
            pstmt.setString(2, evaluation.getIdUtilisateur());
            pstmt.setString(3, evaluation.getProduitId());
            pstmt.setInt(4, evaluation.getNote());
            pstmt.executeUpdate();
        }
    }

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
        }
    }

    public List<Evaluation> getEvaluationsByProduitId(String produitId) throws SQLException {
        String sql = produitId == null 
            ? "SELECT e.*, p.nom AS produitNom FROM Evaluation e LEFT JOIN Produit p ON e.produitId = p.id"
            : "SELECT e.*, p.nom AS produitNom FROM Evaluation e LEFT JOIN Produit p ON e.produitId = p.id WHERE e.produitId = ?";
        List<Evaluation> evaluations = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (produitId != null) {
                pstmt.setString(1, produitId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Evaluation evaluation = new Evaluation();
                    evaluation.setId(rs.getString("id"));
                    evaluation.setIdUtilisateur(rs.getString("idUtilisateur"));
                    evaluation.setProduitId(rs.getString("produitId"));
                    evaluation.setProduitNom(rs.getString("produitNom"));
                    evaluation.setNote(rs.getInt("note"));
                    evaluations.add(evaluation);
                }
            }
        }
        return evaluations;
    }

    public List<Commentaire> getCommentairesByProduitId(String produitId, int page, int pageSize) throws SQLException {
        String sql = produitId == null 
            ? "SELECT c.*, p.nom AS produitNom FROM Commentaire c LEFT JOIN Produit p ON c.produitId = p.id ORDER BY c.dateCreation DESC LIMIT ? OFFSET ?"
            : "SELECT c.*, p.nom AS produitNom FROM Commentaire c LEFT JOIN Produit p ON c.produitId = p.id WHERE c.produitId = ? ORDER BY c.dateCreation DESC LIMIT ? OFFSET ?";
        List<Commentaire> commentaires = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (produitId != null) {
                pstmt.setString(1, produitId);
                pstmt.setInt(2, pageSize);
                pstmt.setInt(3, (page - 1) * pageSize);
            } else {
                pstmt.setInt(1, pageSize);
                pstmt.setInt(2, (page - 1) * pageSize);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Commentaire commentaire = new Commentaire();
                    commentaire.setId(rs.getString("id"));
                    commentaire.setIdUtilisateur(rs.getString("idUtilisateur"));
                    commentaire.setProduitId(rs.getString("produitId"));
                    commentaire.setProduitNom(rs.getString("produitNom"));
                    commentaire.setCommentaire(rs.getString("commentaire"));
                    commentaire.setDateCreation(rs.getDate("dateCreation"));
                    commentaires.add(commentaire);
                }
            }
        }
        return commentaires;
    }

    public int getCommentCount(String produitId) throws SQLException {
        String sql = produitId == null ? "SELECT COUNT(*) FROM Commentaire" : "SELECT COUNT(*) FROM Commentaire WHERE produitId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (produitId != null) {
                pstmt.setString(1, produitId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public double getAverageRatingByProduitId(String produitId) throws SQLException {
        String sql = produitId == null ? "SELECT AVG(note) FROM Evaluation" : "SELECT AVG(note) FROM Evaluation WHERE produitId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (produitId != null) {
                pstmt.setString(1, produitId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }

    public boolean hasUserEvaluated(String idUtilisateur, String produitId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Evaluation WHERE idUtilisateur = ? AND produitId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idUtilisateur);
            pstmt.setString(2, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void deleteComment(String commentId) throws SQLException {
        String sql = "DELETE FROM Commentaire WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, commentId);
            pstmt.executeUpdate();
        }
    }
}