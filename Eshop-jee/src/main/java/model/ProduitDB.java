package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing products in the database.
 */
public class ProduitDB {

    /**
     * Adds a new product to the database.
     *
     * @param produit The product to add.
     * @throws SQLException If a database error occurs.
     */
    public void ajouterProduit(Produit_model produit) throws SQLException {
        String sql = "INSERT INTO produit (id, nom, description, prix, quantite, idCategorie, image, remise) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produit.getId());
            pstmt.setString(2, produit.getNom());
            pstmt.setString(3, produit.getDescription());
            pstmt.setDouble(4, produit.getPrix());
            pstmt.setInt(5, produit.getQuantite());
            pstmt.setString(6, produit.getIdCategorie());
            pstmt.setString(7, produit.getImage());
            pstmt.setDouble(8, produit.getRemise());
            pstmt.executeUpdate();
        }
    }
    
    
    public List<Produit_model> getDiscountedProducts() throws SQLException {
        List<Produit_model> discountedProducts = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE remise > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Produit_model produit = new Produit_model();
                produit.setId(rs.getString("id"));
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setPrix(rs.getDouble("prix"));
                produit.setQuantite(rs.getInt("quantite"));
                produit.setIdCategorie(rs.getString("idCategorie"));
                produit.setImage(rs.getString("image"));
                produit.setRemise(rs.getDouble("remise"));
                discountedProducts.add(produit);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits en remise : " + e.getMessage());
            throw e;
        }
        return discountedProducts;
    }
    
    public String getProductNameById(String produitId) throws SQLException {
        String sql = "SELECT nom FROM Produit WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom");
                }
            }
        }
        return "Produit inconnu";
    }
    
    public List<Produit_model> getRelatedProducts(String categorieId, String excludeProduitId) throws SQLException {
        List<Produit_model> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE idCategorie = ? AND id != ? LIMIT 4";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categorieId);
            pstmt.setString(2, excludeProduitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produit_model produit = new Produit_model();
                    produit.setId(rs.getString("id"));
                    produit.setNom(rs.getString("nom"));
                    produit.setPrix(rs.getDouble("prix"));
                    produit.setDescription(rs.getString("description"));
                    produit.setImage(rs.getString("image"));
                    produit.setQuantite(rs.getInt("quantite"));
                    produit.setIdCategorie(rs.getString("idCategorie"));
                    produit.setRemise(rs.getInt("remise"));
                    produits.add(produit);
                }
            }
        }
        return produits;
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product to retrieve.
     * @return The product if found, or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public Produit_model getProductById(String id) throws SQLException {
        String sql = "SELECT * FROM produit WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Produit_model produit = new Produit_model();
                    produit.setId(rs.getString("id"));
                    produit.setNom(rs.getString("nom"));
                    produit.setDescription(rs.getString("description"));
                    produit.setPrix(rs.getDouble("prix"));
                    produit.setQuantite(rs.getInt("quantite"));
                    produit.setIdCategorie(rs.getString("idCategorie"));
                    produit.setImage(rs.getString("image"));
                    produit.setRemise(rs.getDouble("remise"));
                    return produit;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all products from the database.
     *
     * @return A list of all products.
     * @throws SQLException If a database error occurs.
     */
    public List<Produit_model> getAllProducts() throws SQLException {
        List<Produit_model> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Produit_model produit = new Produit_model();
                produit.setId(rs.getString("id"));
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setPrix(rs.getDouble("prix"));
                produit.setQuantite(rs.getInt("quantite"));
                produit.setIdCategorie(rs.getString("idCategorie"));
                produit.setImage(rs.getString("image"));
                produit.setRemise(rs.getDouble("remise"));
                produits.add(produit);
            }
        }
        return produits;
    }

    /**
     * Retrieves products by category ID.
     *
     * @param categorieId The ID of the category.
     * @return A list of products in the specified category.
     * @throws SQLException If a database error occurs.
     */
    public List<Produit_model> getProductsByCategory(String categorieId) throws SQLException {
        List<Produit_model> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE idCategorie = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categorieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produit_model produit = new Produit_model();
                    produit.setId(rs.getString("id"));
                    produit.setNom(rs.getString("nom"));
                    produit.setDescription(rs.getString("description"));
                    produit.setPrix(rs.getDouble("prix"));
                    produit.setQuantite(rs.getInt("quantite"));
                    produit.setIdCategorie(rs.getString("idCategorie"));
                    produit.setImage(rs.getString("image"));
                    produit.setRemise(rs.getDouble("remise"));
                    produits.add(produit);
                }
            }
        }
        return produits;
    }

    /**
     * Searches for products by name.
     *
     * @param query The search query to match against product names.
     * @return A list of products whose names match the query.
     * @throws SQLException If a database error occurs.
     */
    public List<Produit_model> searchProductsByName(String query) throws SQLException {
        List<Produit_model> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE nom LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Produit_model produit = new Produit_model();
                    produit.setId(rs.getString("id"));
                    produit.setNom(rs.getString("nom"));
                    produit.setDescription(rs.getString("description"));
                    produit.setPrix(rs.getDouble("prix"));
                    produit.setQuantite(rs.getInt("quantite"));
                    produit.setIdCategorie(rs.getString("idCategorie"));
                    produit.setImage(rs.getString("image"));
                    produit.setRemise(rs.getDouble("remise"));
                    produits.add(produit);
                }
            }
        }
        return produits;
    }

    /**
     * Updates an existing product in the database.
     *
     * @param produit The product to update.
     * @throws SQLException If a database error occurs.
     */
    public void modifierProduit(Produit_model produit) throws SQLException {
        String sql = "UPDATE produit SET nom = ?, description = ?, prix = ?, quantite = ?, idCategorie = ?, image = ?, remise = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setString(2, produit.getDescription());
            pstmt.setDouble(3, produit.getPrix());
            pstmt.setInt(4, produit.getQuantite());
            pstmt.setString(5, produit.getIdCategorie());
            pstmt.setString(6, produit.getImage());
            pstmt.setDouble(7, produit.getRemise());
            pstmt.setString(8, produit.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a product from the database by its ID.
     *
     * @param id The ID of the product to delete.
     * @throws SQLException If a database error occurs.
     */
    public void supprimerProduit(String id) throws SQLException {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }
}