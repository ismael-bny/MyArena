package com.example.myarena.domain;

/**
 * Représente un produit (équipement) disponible à la vente ou à la location
 */
public class Product {
    private Long id;
    private Long ownerId;             
    private String name;
    private String description;
    private double price;             
    private double rentalPricePerDay; 
    private int stock;                 
    private boolean isSellable;      
    private boolean isRentable;       


    public Product() {
    }

    public Product(Long id, Long ownerId, String name, String description, double price, double rentalPricePerDay, int stock, boolean isSellable, boolean isRentable) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.rentalPricePerDay = rentalPricePerDay;
        this.stock = stock;
        this.isSellable = isSellable;
        this.isRentable = isRentable;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public void setRentalPricePerDay(double rentalPricePerDay) {
        this.rentalPricePerDay = rentalPricePerDay;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isSellable() {
        return isSellable;
    }

    public void setSellable(boolean sellable) {
        isSellable = sellable;
    }

    public boolean isRentable() {
        return isRentable;
    }

    public void setRentable(boolean rentable) {
        isRentable = rentable;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", rentalPricePerDay=" + rentalPricePerDay +
                ", stock=" + stock +
                ", isSellable=" + isSellable +
                ", isRentable=" + isRentable +
                '}';
    }
}