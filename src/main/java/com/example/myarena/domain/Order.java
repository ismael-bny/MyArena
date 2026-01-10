package com.example.myarena.domain;

import com.example.myarena.domain.OrderStatus;

/** Commande */
public class Order {
    private Long id;
    private Long userId;
    private String referenceNumber;   
    private String orderDate;        
    private double amount;           
    private OrderStatus status;


    public Order() {
    }


    public Order(Long id, Long userId, String referenceNumber, String orderDate, double amount, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.referenceNumber = referenceNumber;
        this.orderDate = orderDate;
        this.amount = amount;
        this.status = status;

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

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                '}';
    }
}