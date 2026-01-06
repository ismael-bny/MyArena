package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Order;
import com.example.myarena.domain.OrderStatus;
import java.util.List;

/** Persistance des commandes */
public interface OrderDAO {

    Order getOrderById(Long id);

    List<Order> getOrdersByUserId(Long userId);

    List<Order> getOrdersByStatus(OrderStatus status);

    List<Order> getAllOrders();

    void saveOrder(Order order);

    void updateOrder(Order order);

    void updateOrderStatus(Long orderId, OrderStatus status);

    void deleteOrder(Long id);
}