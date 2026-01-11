package com.example.myarena.facade;

import com.example.myarena.domain.Order;
import com.example.myarena.domain.OrderStatus;
import com.example.myarena.services.OrderManager;

import java.util.List;

/** Gestion des commandes */
public class OrderFacade {

    private static OrderFacade instance;
    private final OrderManager orderManager;

    private OrderFacade() {
        this.orderManager = OrderManager.getInstance();
    }

    public static OrderFacade getInstance() {
        if (instance == null) {
            instance = new OrderFacade();
        }
        return instance;
    }

    /** Récupère les commandes en attente */
    public List<Order> getPendingOrders() {
        return orderManager.getPendingOrders();
    }

    /** Récupère une commande par ID */
    public Order getOrderById(Long orderId) {
        return orderManager.getOrderById(orderId);
    }


    /** Récupère les commandes d'un utilisateur */
    public List<Order> getOrdersByUserId(Long userId) {
        return orderManager.getOrdersByUserId(userId);
    }

    /** Récupère toutes les commandes */
    public List<Order> getAllOrders() {
        return orderManager.getAllOrders();
    }

    /** Met à jour le statut d'une commande */
    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        if (order == null) {
            throw new IllegalArgumentException("L'objet Order ne peut pas être null");
        }

        if (order.getId() == null) {
            throw new IllegalArgumentException("L'ID de la commande ne peut pas être null");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Seules les commandes PENDING peuvent être modifiées. Statut actuel : " + order.getStatus());
        }

        order.setStatus(newStatus);
        orderManager.updateOrder(order);
    }

    /** Valide une commande */
    public void validateOrder(Order order) {
        updateOrderStatus(order, OrderStatus.PAID);
    }

    /** Rejette une commande */
    public void rejectOrder(Order order) {
        updateOrderStatus(order, OrderStatus.CANCELLED);
    }

    /** Vérifie si une commande peut être modifiée */
    public boolean canModifyOrder(Order order) {
        return order != null && order.getStatus() == OrderStatus.PENDING;
    }
}