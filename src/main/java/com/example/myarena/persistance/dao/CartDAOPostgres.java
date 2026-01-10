package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Cart;
import com.example.myarena.domain.CartItem;
import com.example.myarena.domain.CartStatus;
import com.example.myarena.domain.ItemType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation PostgreSQL pour la persistance des paniers
 */
public class CartDAOPostgres implements CartDAO {

    // Même config que TerrainDAOPostgres
    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public Cart getActiveCartByUserId(Long userId) {
        String sql = "SELECT * FROM carts WHERE user_id = ? AND status = 'ACTIVE'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cart cart = mapResultSetToCart(rs);
                // Charger les articles du panier
                cart.setItems(getCartItemsByCartId(cart.getId()));
                return cart;
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du panier actif de l'utilisateur id=" + userId, e);
        }
    }

    @Override
    public void saveCart(Cart cart) {
        String sql = """
                INSERT INTO carts (user_id, status)
                VALUES (?, ?)
                RETURNING id
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cart.getUserId());
            stmt.setString(2, cart.getStatus().name());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long generatedId = rs.getLong("id");
                cart.setId(generatedId); // on met à jour l'objet
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du panier", e);
        }
    }

    @Override
    public void updateCart(Cart cart) {
        if (cart.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un panier sans id");
        }

        String sql = """
                UPDATE carts
                SET user_id = ?, status = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cart.getUserId());
            stmt.setString(2, cart.getStatus().name());
            stmt.setLong(3, cart.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du panier id=" + cart.getId(), e);
        }
    }

    @Override
    public void updateCartStatus(Long cartId, CartStatus status) {
        String sql = "UPDATE carts SET status = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setLong(2, cartId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut du panier id=" + cartId, e);
        }
    }

    @Override
    public void addCartItem(CartItem cartItem) {
        String sql = """
                INSERT INTO cart_items (cart_id, product_id, quantity, item_type, rental_start, rental_end, unit_price)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cartItem.getCartId());
            stmt.setLong(2, cartItem.getProductId());
            stmt.setInt(3, cartItem.getQuantity());
            stmt.setString(4, cartItem.getItemType().name());

            // Gestion des dates de location (null si SALE)
            if (cartItem.getRentalStart() != null) {
                stmt.setDate(5, Date.valueOf(cartItem.getRentalStart()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            if (cartItem.getRentalEnd() != null) {
                stmt.setDate(6, Date.valueOf(cartItem.getRentalEnd()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setDouble(7, cartItem.getUnitPrice());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long generatedId = rs.getLong("id");
                cartItem.setId(generatedId); // on met à jour l'objet
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'article au panier", e);
        }
    }

    @Override
    public void updateCartItem(CartItem cartItem) {
        if (cartItem.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un article sans id");
        }

        String sql = """
                UPDATE cart_items
                SET cart_id = ?, product_id = ?, quantity = ?, item_type = ?, 
                    rental_start = ?, rental_end = ?, unit_price = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cartItem.getCartId());
            stmt.setLong(2, cartItem.getProductId());
            stmt.setInt(3, cartItem.getQuantity());
            stmt.setString(4, cartItem.getItemType().name());

            // Gestion des dates de location (null si SALE)
            if (cartItem.getRentalStart() != null) {
                stmt.setDate(5, Date.valueOf(cartItem.getRentalStart()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            if (cartItem.getRentalEnd() != null) {
                stmt.setDate(6, Date.valueOf(cartItem.getRentalEnd()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setDouble(7, cartItem.getUnitPrice());
            stmt.setLong(8, cartItem.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'article id=" + cartItem.getId(), e);
        }
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        String sql = "DELETE FROM cart_items WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cartItemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'article id=" + cartItemId, e);
        }
    }

    @Override
    public List<CartItem> getCartItemsByCartId(Long cartId) {
        String sql = "SELECT * FROM cart_items WHERE cart_id = ? ORDER BY id";
        List<CartItem> items = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cartId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToCartItem(rs));
            }
            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des articles du panier id=" + cartId, e);
        }
    }

    @Override
    public void clearCart(Long cartId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cartId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du vidage du panier id=" + cartId, e);
        }
    }

    private Cart mapResultSetToCart(ResultSet rs) throws SQLException {
        Cart cart = new Cart();
        cart.setId(rs.getLong("id"));
        cart.setUserId(rs.getLong("user_id"));
        cart.setStatus(CartStatus.valueOf(rs.getString("status")));
        return cart;
    }

    private CartItem mapResultSetToCartItem(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setId(rs.getLong("id"));
        item.setCartId(rs.getLong("cart_id"));
        item.setProductId(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setItemType(ItemType.valueOf(rs.getString("item_type")));

        // Gestion des dates (peuvent être null si SALE)
        Date rentalStart = rs.getDate("rental_start");
        if (rentalStart != null) {
            item.setRentalStart(rentalStart.toString());
        }

        Date rentalEnd = rs.getDate("rental_end");
        if (rentalEnd != null) {
            item.setRentalEnd(rentalEnd.toString());
        }

        item.setUnitPrice(rs.getDouble("unit_price"));
        return item;
    }
}