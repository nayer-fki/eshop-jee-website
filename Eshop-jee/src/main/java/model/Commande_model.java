package model;



public class Commande_model {
    private String id;
    private String idUtilisateur;
    private String produits; 
    private double prixTotal;
    private String statut;
    private java.sql.Date dateCommande;

    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getProduits() { return produits; }
    public void setProduits(String produits) { this.produits = produits; }
    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public java.sql.Date getDateCommande() { return dateCommande; }
    public void setDateCommande(java.sql.Date dateCommande) { this.dateCommande = dateCommande; }
}