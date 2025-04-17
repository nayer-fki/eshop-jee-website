package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger; // Import Logger
import org.mindrot.jbcrypt.BCrypt;

public class UtilisateurDB {
    // Define and initialize the Logger
    private static final Logger LOGGER = Logger.getLogger(UtilisateurDB.class.getName());

    public void ajouterUtilisateur(Utilisateur_model utilisateur) throws SQLException {
        String sql = "INSERT INTO Utilisateur (id, nom, email, motDePasse, estAdmin, image) VALUES (?, ?, ?, ?, ?, ?)";
        String hashedPassword = isPasswordHashed(utilisateur.getMotDePasse()) ? utilisateur.getMotDePasse() : BCrypt.hashpw(utilisateur.getMotDePasse(), BCrypt.gensalt());
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getId());
            pstmt.setString(2, utilisateur.getNom());
            pstmt.setString(3, utilisateur.getEmail());
            pstmt.setString(4, hashedPassword);
            pstmt.setBoolean(5, utilisateur.isEstAdmin());
            pstmt.setString(6, utilisateur.getImage());
            pstmt.executeUpdate();
        }
    }
    
    // Method to verify a password against the hashed password
    public boolean verifierMotDePasse(String email, String motDePasse) {
        try {
            Utilisateur_model user = trouverUtilisateurParEmail(email); // Corrected method name
            if (user != null) {
                return BCrypt.checkpw(motDePasse, user.getMotDePasse()); // Assuming motDePasse is hashed
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du mot de passe pour l'email : " + email, e);
            return false;
        }
    }
    
    public String getUserNameById(String userId) throws SQLException {
        String sql = "SELECT nom FROM utilisateur WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom");
                }
            }
        }
        return "Utilisateur inconnu";
    }
    
    
    // Method to verify a password against the hashed password
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du mot de passe", e);
            return false;
        }
    }

    public Utilisateur_model trouverUtilisateur(String id) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Utilisateur_model utilisateur = new Utilisateur_model();
                    utilisateur.setId(rs.getString("id"));
                    utilisateur.setNom(rs.getString("nom"));
                    utilisateur.setEmail(rs.getString("email"));
                    utilisateur.setMotDePasse(rs.getString("motDePasse"));
                    utilisateur.setEstAdmin(rs.getBoolean("estAdmin"));
                    utilisateur.setImage(rs.getString("image"));
                    return utilisateur;
                }
            }
        }
        return null;
    }

    public Utilisateur_model trouverUtilisateurParEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Utilisateur_model utilisateur = new Utilisateur_model();
                    utilisateur.setId(rs.getString("id"));
                    utilisateur.setNom(rs.getString("nom"));
                    utilisateur.setEmail(rs.getString("email"));
                    utilisateur.setMotDePasse(rs.getString("motDePasse"));
                    utilisateur.setEstAdmin(rs.getBoolean("estAdmin"));
                    utilisateur.setImage(rs.getString("image"));
                    return utilisateur;
                }
            }
        }
        return null;
    }

    public List<Utilisateur_model> listerUtilisateurs() throws SQLException {
        String sql = "SELECT * FROM Utilisateur";
        List<Utilisateur_model> utilisateurs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Utilisateur_model utilisateur = new Utilisateur_model();
                utilisateur.setId(rs.getString("id"));
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setEmail(rs.getString("email"));
                utilisateur.setMotDePasse(rs.getString("motDePasse"));
                utilisateur.setEstAdmin(rs.getBoolean("estAdmin"));
                utilisateur.setImage(rs.getString("image"));
                utilisateurs.add(utilisateur);
            }
        }
        return utilisateurs;
    }
    
    public void modifierUtilisateur(Utilisateur_model utilisateur) throws SQLException {
        String sql = "UPDATE Utilisateur SET nom = ?, email = ?, motDePasse = ?, estAdmin = ?, image = ? WHERE id = ?";
        String hashedPassword = isPasswordHashed(utilisateur.getMotDePasse()) ? utilisateur.getMotDePasse() : BCrypt.hashpw(utilisateur.getMotDePasse(), BCrypt.gensalt());
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getNom());
            pstmt.setString(2, utilisateur.getEmail());
            pstmt.setString(3, hashedPassword);
            pstmt.setBoolean(4, utilisateur.isEstAdmin());
            pstmt.setString(5, utilisateur.getImage());
            pstmt.setString(6, utilisateur.getId());
            pstmt.executeUpdate();
        }
    }

    public void supprimerUtilisateur(String id) throws SQLException {
        String sql = "DELETE FROM Utilisateur WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    public Utilisateur_model authentifierAdmin(String email, String motDePasse) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("motDePasse");
                    if (isPasswordHashed(storedPassword)) {
                        if (BCrypt.checkpw(motDePasse, storedPassword)) {
                            boolean estAdmin = rs.getBoolean("estAdmin");
                            if (estAdmin) {
                                Utilisateur_model utilisateur = new Utilisateur_model();
                                utilisateur.setId(rs.getString("id"));
                                utilisateur.setNom(rs.getString("nom"));
                                utilisateur.setEmail(rs.getString("email"));
                                utilisateur.setMotDePasse(storedPassword);
                                utilisateur.setEstAdmin(estAdmin);
                                utilisateur.setImage(rs.getString("image"));
                                return utilisateur;
                            }
                        }
                    } else if (motDePasse != null && motDePasse.equals(storedPassword)) {
                        boolean estAdmin = rs.getBoolean("estAdmin");
                        if (estAdmin) {
                            String hashedPassword = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
                            updatePassword(email, hashedPassword);
                            Utilisateur_model utilisateur = new Utilisateur_model();
                            utilisateur.setId(rs.getString("id"));
                            utilisateur.setNom(rs.getString("nom"));
                            utilisateur.setEmail(rs.getString("email"));
                            utilisateur.setMotDePasse(hashedPassword);
                            utilisateur.setEstAdmin(estAdmin);
                            utilisateur.setImage(rs.getString("image"));
                            return utilisateur;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Utilisateur_model authentifierClient(String email, String motDePasse) throws SQLException {
        String sql = "SELECT * FROM Utilisateur WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("motDePasse");
                    if (isPasswordHashed(storedPassword)) {
                        if (BCrypt.checkpw(motDePasse, storedPassword)) {
                            boolean estAdmin = rs.getBoolean("estAdmin");
                            if (!estAdmin) {
                                Utilisateur_model utilisateur = new Utilisateur_model();
                                utilisateur.setId(rs.getString("id"));
                                utilisateur.setNom(rs.getString("nom"));
                                utilisateur.setEmail(rs.getString("email"));
                                utilisateur.setMotDePasse(storedPassword);
                                utilisateur.setEstAdmin(estAdmin);
                                utilisateur.setImage(rs.getString("image"));
                                return utilisateur;
                            }
                        }
                    } else if (motDePasse != null && motDePasse.equals(storedPassword)) {
                        boolean estAdmin = rs.getBoolean("estAdmin");
                        if (!estAdmin) {
                            String hashedPassword = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
                            updatePassword(email, hashedPassword);
                            Utilisateur_model utilisateur = new Utilisateur_model();
                            utilisateur.setId(rs.getString("id"));
                            utilisateur.setNom(rs.getString("nom"));
                            utilisateur.setEmail(rs.getString("email"));
                            utilisateur.setMotDePasse(hashedPassword);
                            utilisateur.setEstAdmin(estAdmin);
                            utilisateur.setImage(rs.getString("image"));
                            return utilisateur;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isPasswordHashed(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$"));
    }

    private void updatePassword(String email, String hashedPassword) throws SQLException {
        String sql = "UPDATE Utilisateur SET motDePasse = ? WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }
}