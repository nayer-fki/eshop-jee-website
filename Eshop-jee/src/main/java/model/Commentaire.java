package model;

import java.sql.Date;

public class Commentaire {
    private String id;
    private String idUtilisateur;
    private String produitId;
    private String produitNom; // New field for display name
    private String commentaire;
    private Date dateCreation;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getProduitId() { return produitId; }
    public void setProduitId(String produitId) { this.produitId = produitId; }

    public String getProduitNom() { return produitNom; }
    public void setProduitNom(String produitNom) { this.produitNom = produitNom; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
}