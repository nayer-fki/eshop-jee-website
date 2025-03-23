package model;

public class Evaluation {
    private String id;
    private String idUtilisateur;
    private String produitId;
    private int note;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(String idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getProduitId() { return produitId; }
    public void setProduitId(String produitId) { this.produitId = produitId; }
    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }
}