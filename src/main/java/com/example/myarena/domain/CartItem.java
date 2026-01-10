package com.example.myarena.domain;

import com.example.myarena.domain.ItemType;

/** Article du panier */
public class CartItem {
    private Long id;
    private Long cartId;
    private Long productId;
    private int quantity;
    private ItemType itemType;
    private String rentalStart;
    private String rentalEnd;
    private double unitPrice;

    public CartItem() {
    }

    public CartItem(Long id, Long cartId, Long productId, int quantity, ItemType itemType, String rentalStart, String rentalEnd, double unitPrice) {
        this.id = id;
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.itemType = itemType;
        this.rentalStart = rentalStart;
        this.rentalEnd = rentalEnd;
        this.unitPrice = unitPrice;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getRentalStart() {
        return rentalStart;
    }

    public void setRentalStart(String rentalStart) {
        this.rentalStart = rentalStart;
    }

    public String getRentalEnd() {
        return rentalEnd;
    }

    public void setRentalEnd(String rentalEnd) {
        this.rentalEnd = rentalEnd;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /** Calcule le sous-total */
    public double getSubtotal() {
        return quantity * unitPrice;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", cartId=" + cartId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", itemType=" + itemType +
                ", rentalStart='" + rentalStart + '\'' +
                ", rentalEnd='" + rentalEnd + '\'' +
                ", unitPrice=" + unitPrice +
                '}';
    }
}