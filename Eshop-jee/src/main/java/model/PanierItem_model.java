package model;

public class PanierItem_model {
    private Produit_model produit;
    private int quantite;
    private double prixUnitaire; // Price at the time of adding to cart (including discount)

    public PanierItem_model() {
    }

    public PanierItem_model(Produit_model produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getDiscountedPrice();
    }

    public Produit_model getProduit() {
        return produit;
    }

    public void setProduit(Produit_model produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
}