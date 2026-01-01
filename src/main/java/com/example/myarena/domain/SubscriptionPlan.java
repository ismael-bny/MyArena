package com.example.myarena.domain;

public class SubscriptionPlan {

    private Long id;
    private String name;
    private double price;
    private int durationMonths;
    private boolean active;

    /**
     * Constructeur par défaut (nécessaire pour JDBC et JavaFX)
     */
    public SubscriptionPlan() {
    }

    /**
     * Constructeur avec tous les paramètres
     */
    public SubscriptionPlan(Long id, String name, double price, int durationMonths, boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationMonths = durationMonths;
        this.active = active;
    }

    // === GETTERS ET SETTERS ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "SubscriptionPlan{" + "id=" + id + ", name='" + name + '\'' + ", price=" + price + ", durationMonths=" + durationMonths + ", active=" + active + '}';
    }
}
