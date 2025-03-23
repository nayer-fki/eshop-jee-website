package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Panier_model {
    private String id;
    private String userId;
    private List<PanierItem_model> items;
    private double total;
    private Date createdAt;
    private Date updatedAt;

    public Panier_model() {
        this.items = new ArrayList<>(); // Ensure items is never null
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<PanierItem_model> getItems() {
        return items;
    }

    public void setItems(List<PanierItem_model> items) {
        this.items = (items != null) ? items : new ArrayList<>(); // Ensure items is never null
        recalculateTotal();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Add an item to the cart
    public void addItem(Produit_model produit, int quantite) {
        for (PanierItem_model item : items) {
            if (item.getProduit().getId().equals(produit.getId())) {
                item.setQuantite(item.getQuantite() + quantite);
                recalculateTotal();
                return;
            }
        }
        items.add(new PanierItem_model(produit, quantite));
        recalculateTotal();
    }

    // Remove an item from the cart
    public void removeItem(String produitId) {
        items.removeIf(item -> item.getProduit().getId().equals(produitId));
        recalculateTotal();
    }

    // Recalculate the total price (considering discounts)
    public void recalculateTotal() {
        this.total = items.stream()
            .filter(item -> item != null && item.getProduit() != null) // Skip null items or items with null products
            .mapToDouble(item -> item.getProduit().getDiscountedPrice() * item.getQuantite())
            .sum();
    }
}