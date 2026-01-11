package com.example.myarena.services;

import com.example.myarena.domain.Cart;
import com.example.myarena.domain.CartItem;
import com.example.myarena.domain.Order;
import com.example.myarena.domain.Product;
import com.example.myarena.domain.ItemType;
import com.example.myarena.domain.OrderStatus;
import com.example.myarena.persistance.dao.OrderDAO;
import com.example.myarena.persistance.dao.OrderDAOPostgres;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/** Logique métier des commandes */
public class OrderManager {

    private static OrderManager instance;
    private final OrderDAO orderDAO;
    private final ProductManager productManager;
    //private final NotificationManager notificationManager;

    private OrderManager() {
        this.orderDAO = new OrderDAOPostgres();
        this.productManager = ProductManager.getInstance();
        //this.notificationManager = NotificationManager.getInstance();
    }

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    /** Crée une commande à partir d'un panier */
    public Order createOrder(Cart cart, Long userId) {
        if (cart.isEmpty()) {
            throw new RuntimeException("Le panier est vide");
        }

        String referenceNumber = generateReferenceNumber();
        String orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        double amount = cart.getTotalAmount();

        Order order = new Order();
        order.setUserId(userId);
        order.setReferenceNumber(referenceNumber);
        order.setOrderDate(orderDate);
        order.setAmount(amount);
        order.setStatus(OrderStatus.PENDING);

        orderDAO.saveOrder(order);

        for (CartItem item : cart.getItems()) {
            productManager.decrementStock(item.getProductId(), item.getQuantity());
        }

        return order;
    }

    /** Génère un numéro de référence unique */
    public String generateReferenceNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + year + "-" + uniqueId;
    }

    /** Récupère une commande par ID */
    public Order getOrderById(Long id) {
        return orderDAO.getOrderById(id);
    }

    /** Récupère les commandes en attente */
    public List<Order> getPendingOrders() {
        return orderDAO.getOrdersByStatus(OrderStatus.PENDING);
    }

    /** Récupère les commandes d'un utilisateur */
    public List<Order> getOrdersByUserId(Long userId) {
        return orderDAO.getOrdersByUserId(userId);
    }

    /** Récupère toutes les commandes */
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

//    /**
//     * Valide une commande (change le statut à PAID)
//     * USE CASE 2 : Validate Payment (étape 6)
//     */
//    public void validateOrder(Long orderId) {
//        Order order = orderDAO.getOrderById(orderId);
//
//        if (order == null) {
//            throw new RuntimeException("Commande introuvable avec id=" + orderId);
//        }
//
//        if (order.getStatus() != OrderStatus.PENDING) {
//            throw new RuntimeException("Seules les commandes PENDING peuvent être validées");
//        }
//
//        // Changer le statut à PAID
//        order.setStatus(OrderStatus.PAID);
//        orderDAO.updateOrder(order);
//
//        // Envoyer une notification au client
//        notificationManager.sendNotification(
//                order.getUserId(),
//                "Commande validée",
//                "Votre commande " + order.getReferenceNumber() + " a été validée et payée avec succès."
//        )
// }


    /** Met à jour une commande */
    public void updateOrder(Order order) {
        orderDAO.updateOrder(order);
    }

    /** Supprime une commande */
    public void deleteOrder(Long id) {
        orderDAO.deleteOrder(id);
    }
}