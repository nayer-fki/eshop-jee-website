package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PanierDB {
    private ProduitDB produitDB;

    public PanierDB() {
        this.produitDB = new ProduitDB();
    }

    public void ajouterAuPanier(Panier_model panier) throws SQLException {
        String sqlPanier = "INSERT INTO panier (id, idUtilisateur, total) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlPanier)) {
            String panierId = UUID.randomUUID().toString();
            panier.setId(panierId);
            pstmt.setString(1, panierId);
            pstmt.setString(2, panier.getUserId());
            pstmt.setDouble(3, panier.getTotal());
            pstmt.executeUpdate();
            System.out.println("PanierDB: Added new cart with ID " + panierId + " for userId " + panier.getUserId());
        }

        String sqlItems = "INSERT INTO panier_items (panier_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlItems)) {
            for (PanierItem_model item : panier.getItems()) {
                pstmt.setString(1, panier.getId());
                pstmt.setString(2, item.getProduit().getId());
                pstmt.setInt(3, item.getQuantite());
                pstmt.setDouble(4, item.getPrixUnitaire());
                pstmt.executeUpdate();
            }
            System.out.println("PanierDB: Added " + panier.getItems().size() + " items to cart ID " + panier.getId());
        }
    }

    public Panier_model trouverPanier(String userId) throws SQLException {
        String sqlPanier = "SELECT * FROM panier WHERE idUtilisateur = ?";
        Panier_model panier = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlPanier)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    panier = new Panier_model();
                    panier.setId(rs.getString("id"));
                    panier.setUserId(rs.getString("idUtilisateur"));
                    System.out.println("PanierDB: Found cart with ID " + panier.getId() + " for userId " + userId);
                }
            }
        }

        if (panier != null) {
            String sqlItems = "SELECT * FROM panier_items WHERE panier_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sqlItems)) {
                pstmt.setString(1, panier.getId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<PanierItem_model> items = new ArrayList<>();
                    while (rs.next()) {
                        String produitId = rs.getString("produit_id");
                        Produit_model produit = produitDB.getProductById(produitId);
                        if (produit != null && produit.getNom() != null) { // Only include valid products
                            PanierItem_model item = new PanierItem_model();
                            item.setProduit(produit);
                            item.setQuantite(rs.getInt("quantite"));
                            item.setPrixUnitaire(produit.getDiscountedPrice());
                            items.add(item);
                            System.out.println("PanierDB: Loaded item with productId " + produitId + " for cart ID " + panier.getId());
                        } else {
                            System.out.println("PanierDB: Skipping invalid item with productId " + produitId + " for cart ID " + panier.getId());
                        }
                    }
                    panier.setItems(items);
                    System.out.println("PanierDB: Loaded " + items.size() + " items for cart ID " + panier.getId());
                }
            }
        }
        return panier;
    }

    public List<Panier_model> listerPaniers() throws SQLException {
        List<Panier_model> paniers = new ArrayList<>();
        String sqlPanier = "SELECT * FROM panier";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlPanier);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Panier_model panier = new Panier_model();
                panier.setId(rs.getString("id"));
                panier.setUserId(rs.getString("idUtilisateur"));
                panier.setTotal(rs.getDouble("total"));
                panier.setCreatedAt(rs.getTimestamp("created_at"));
                panier.setUpdatedAt(rs.getTimestamp("updated_at"));

                String sqlItems = "SELECT * FROM panier_items WHERE panier_id = ?";
                try (PreparedStatement pstmtItems = conn.prepareStatement(sqlItems)) {
                    pstmtItems.setString(1, panier.getId());
                    try (ResultSet rsItems = pstmtItems.executeQuery()) {
                        List<PanierItem_model> items = new ArrayList<>();
                        while (rsItems.next()) {
                            String produitId = rsItems.getString("produit_id");
                            Produit_model produit = produitDB.getProductById(produitId);
                            if (produit != null) {
                                PanierItem_model item = new PanierItem_model(produit, rsItems.getInt("quantite"));
                                item.setPrixUnitaire(rsItems.getDouble("prix_unitaire"));
                                items.add(item);
                            }
                        }
                        panier.setItems(items);
                    }
                }
                paniers.add(panier);
            }
        }
        return paniers;
    }

    public void modifierPanier(Panier_model panier) throws SQLException {
        String sqlPanier = "UPDATE panier SET idUtilisateur = ?, total = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlPanier)) {
            pstmt.setString(1, panier.getUserId());
            pstmt.setDouble(2, panier.getTotal());
            pstmt.setString(3, panier.getId());
            pstmt.executeUpdate();
            System.out.println("PanierDB: Updated cart with ID " + panier.getId());
        }

        String sqlDeleteItems = "DELETE FROM panier_items WHERE panier_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlDeleteItems)) {
            pstmt.setString(1, panier.getId());
            pstmt.executeUpdate();
            System.out.println("PanierDB: Deleted existing items for cart ID " + panier.getId());
        }

        String sqlItems = "INSERT INTO panier_items (panier_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlItems)) {
            for (PanierItem_model item : panier.getItems()) {
                pstmt.setString(1, panier.getId());
                pstmt.setString(2, item.getProduit().getId());
                pstmt.setInt(3, item.getQuantite());
                pstmt.setDouble(4, item.getPrixUnitaire());
                pstmt.executeUpdate();
            }
            System.out.println("PanierDB: Inserted " + panier.getItems().size() + " items for cart ID " + panier.getId());
        }
    }

    public void supprimerPanier(String id) throws SQLException {
        String sqlDeleteItems = "DELETE FROM panier_items WHERE panier_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlDeleteItems)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }

        String sqlDeletePanier = "DELETE FROM panier WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlDeletePanier)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }
}