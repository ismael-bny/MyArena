package com.example.myarena.domain;

import com.example.myarena.domain.CartStatus;
import java.util.ArrayList;
import java.util.List;

/** Panier */
public class Cart {
    private Long id;
    private Long userId;
    private CartStatus status;
    private List<CartItem> items;  


    public Cart() {
        this.items = new ArrayList<>();
    }


    public Cart(Long id, Long userId, CartStatus status) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.items = new ArrayList<>();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }


    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }


    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", status=" + status +
                ", items=" + items.size() +
                ", total=" + getTotalAmount() +
                '}';
    }
}