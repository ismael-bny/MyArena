package com.example.myarena.domain;

/**
 * Représente un code promo ou une réduction
 */
public class Discount {
    private Long id;
    private String code;              
    private double discountValue;     
    private String expirationDate;    // Date d'expiration (format: yyyy-MM-dd)
    private boolean active;    


    public Discount() {
    }


    public Discount(Long id, String code, double discountValue, String expirationDate, boolean active) {
        this.id = id;
        this.code = code;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.active = active;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", discountValue=" + discountValue +
                ", expirationDate='" + expirationDate + '\'' +
                ", active=" + active +
                '}';
    }
}