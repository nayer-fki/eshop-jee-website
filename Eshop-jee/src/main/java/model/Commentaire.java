package model;

public class Commentaire {
    private String id;
    private String idUtilisateur;
    private String produitId;
    private String commentaire;
    private java.sql.Date dateCreation;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getProduitId() { return produitId; }
    public void setProduitId(String produitId) { this.produitId = produitId; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public java.sql.Date getDateCreation() { return dateCreation; }
    public void setDateCreation(java.sql.Date dateCreation) { this.dateCreation = dateCreation; }
}