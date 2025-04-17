package model;

public class Evaluation {
    private String id;
    private String idUtilisateur;
    private String produitId;
    private String produitNom; // New field for display name
    private int note;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getProduitId() { return produitId; }
    public void setProduitId(String produitId) { this.produitId = produitId; }

    public String getProduitNom() { return produitNom; }
    public void setProduitNom(String produitNom) { this.produitNom = produitNom; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }
}