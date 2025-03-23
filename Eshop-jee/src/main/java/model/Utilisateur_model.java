package model;

public class Utilisateur_model {
    private String id;
    private String nom;
    private String email;
    private String motDePasse;
    private boolean estAdmin;
    private String image;

    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public boolean isEstAdmin() { return estAdmin; }
    public void setEstAdmin(boolean estAdmin) { this.estAdmin = estAdmin; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}