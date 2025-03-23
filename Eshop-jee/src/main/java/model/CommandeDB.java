package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandeDB {
    public void ajouterCommande(Commande_model commande) throws SQLException {
        String sql = "INSERT INTO Commande (id, idUtilisateur, produits, prixTotal, statut, dateCommande) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, commande.getId());
            pstmt.setString(2, commande.getIdUtilisateur());
            pstmt.setString(3, commande.getProduits());
            pstmt.setDouble(4, commande.getPrixTotal());
            pstmt.setString(5, commande.getStatut());
            pstmt.setDate(6, commande.getDateCommande());
            pstmt.executeUpdate();
            System.out.println("CommandeDB: Added new order with ID " + commande.getId() + " for userId " + commande.getIdUtilisateur());
        }
    }

    public Commande_model trouverCommande(String id) throws SQLException {
        String sql = "SELECT * FROM Commande WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Commande_model commande = new Commande_model();
                    commande.setId(rs.getString("id"));
                    commande.setIdUtilisateur(rs.getString("idUtilisateur"));
                    commande.setProduits(rs.getString("produits"));
                    commande.setPrixTotal(rs.getDouble("prixTotal"));
                    commande.setStatut(rs.getString("statut"));
                    commande.setDateCommande(rs.getDate("dateCommande"));
                    System.out.println("CommandeDB: Found order with ID " + commande.getId());
                    return commande;
                }
            }
        }
        System.out.println("CommandeDB: No order found with ID " + id);
        return null;
    }

    public List<Commande_model> listerCommandes() throws SQLException {
        String sql = "SELECT * FROM Commande";
        List<Commande_model> commandes = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Commande_model commande = new Commande_model();
                commande.setId(rs.getString("id"));
                commande.setIdUtilisateur(rs.getString("idUtilisateur"));
                commande.setProduits(rs.getString("produits"));
                commande.setPrixTotal(rs.getDouble("prixTotal"));
                commande.setStatut(rs.getString("statut"));
                commande.setDateCommande(rs.getDate("dateCommande"));
                commandes.add(commande);
            }
            System.out.println("CommandeDB: Listed " + commandes.size() + " orders");
        }
        return commandes;
    }

    public void modifierCommande(Commande_model commande) throws SQLException {
        String sql = "UPDATE Commande SET idUtilisateur = ?, produits = ?, prixTotal = ?, statut = ?, dateCommande = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, commande.getIdUtilisateur());
            pstmt.setString(2, commande.getProduits());
            pstmt.setDouble(3, commande.getPrixTotal());
            pstmt.setString(4, commande.getStatut());
            pstmt.setDate(5, commande.getDateCommande());
            pstmt.setString(6, commande.getId());
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("CommandeDB: Updated order with ID " + commande.getId() + " (" + rowsAffected + " rows affected)");
        }
    }

    public void supprimerCommande(String id) throws SQLException {
        String sql = "DELETE FROM Commande WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("CommandeDB: Deleted order with ID " + id + " (" + rowsAffected + " rows affected)");
        }
    }
}