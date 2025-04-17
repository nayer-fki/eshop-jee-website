package model;

public class CommandeWithClientName {
    private Commande_model commande;
    private String clientName;

    public CommandeWithClientName(Commande_model commande, String clientName) {
        this.commande = commande;
        this.clientName = clientName;
    }

    public Commande_model getCommande() {
        return commande;
    }

    public String getClientName() {
        return clientName;
    }
}