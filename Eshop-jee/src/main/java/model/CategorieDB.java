package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategorieDB {
    public void ajouterCategorie(Categorie_model categorie) throws SQLException {
        String sql = "INSERT INTO Categorie (id, nom) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categorie.getId());
            pstmt.setString(2, categorie.getNom());
            pstmt.executeUpdate();
        }
    }
    
    public List<Categorie_model> getAllCategories() {
        List<Categorie_model> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Categorie_model categorie = new Categorie_model();
                categorie.setId(rs.getString("id"));
                categorie.setNom(rs.getString("nom"));
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Categorie_model getCategorieById(String id) {
        Categorie_model categorie = null;
        String sql = "SELECT * FROM categorie WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    categorie = new Categorie_model();
                    categorie.setId(rs.getString("id"));
                    categorie.setNom(rs.getString("nom"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorie;
    }

    public Categorie_model trouverCategorie(String id) throws SQLException {
        String sql = "SELECT * FROM Categorie WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Categorie_model categorie = new Categorie_model();
                categorie.setId(rs.getString("id"));
                categorie.setNom(rs.getString("nom"));
                return categorie;
            }
        }
        return null;
    }

    public List<Categorie_model> listerCategories() throws SQLException {
        String sql = "SELECT * FROM Categorie";
        List<Categorie_model> categories = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Categorie_model categorie = new Categorie_model();
                categorie.setId(rs.getString("id"));
                categorie.setNom(rs.getString("nom"));
                categories.add(categorie);
            }
        }
        return categories;
    }

    public void modifierCategorie(Categorie_model categorie) throws SQLException {
        String sql = "UPDATE Categorie SET nom = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categorie.getNom());
            pstmt.setString(2, categorie.getId());
            pstmt.executeUpdate();
        }
    }

    public void supprimerCategorie(String id) throws SQLException {
        String sql = "DELETE FROM Categorie WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }
}