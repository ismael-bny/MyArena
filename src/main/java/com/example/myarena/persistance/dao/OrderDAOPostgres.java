package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Order;
import com.example.myarena.domain.OrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation PostgreSQL pour la persistance des commandes
 */
public class OrderDAOPostgres implements OrderDAO {

    // Même config que TerrainDAOPostgres
    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public Order getOrderById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la commande avec id=" + id, e);
        }
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            return orders;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des commandes de l'utilisateur id=" + userId, e);
        }
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY id DESC";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            return orders;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des commandes avec statut=" + status, e);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders ORDER BY id DESC";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            return orders;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les commandes", e);
        }
    }

    @Override
    public void saveOrder(Order order) {
        String sql = """
                INSERT INTO orders (user_id, reference_number, order_date, amount, status)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, order.getUserId());
            stmt.setString(2, order.getReferenceNumber());
            stmt.setString(3, order.getOrderDate());
            stmt.setDouble(4, order.getAmount());
            stmt.setString(5, order.getStatus().name());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long generatedId = rs.getLong("id");
                order.setId(generatedId); // on met à jour l'objet
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la commande", e);
        }
    }

    @Override
    public void updateOrder(Order order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour une commande sans id");
        }

        String sql = """
                UPDATE orders
                SET user_id = ?, reference_number = ?, order_date = ?, amount = ?, status = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, order.getUserId());
            stmt.setString(2, order.getReferenceNumber());
            stmt.setString(3, order.getOrderDate());
            stmt.setDouble(4, order.getAmount());
            stmt.setString(5, order.getStatus().name());

            stmt.setLong(6, order.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la commande id=" + order.getId(), e);
        }
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setLong(2, orderId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la commande id=" + orderId, e);
        }
    }

    @Override
    public void deleteOrder(Long id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la commande id=" + id, e);
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setReferenceNumber(rs.getString("reference_number"));
        order.setOrderDate(rs.getString("order_date"));
        order.setAmount(rs.getDouble("amount"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        return order;
    }
}