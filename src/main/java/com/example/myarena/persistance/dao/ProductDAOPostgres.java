package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation PostgreSQL pour la persistance des produits
 */
public class ProductDAOPostgres implements ProductDAO {

    // Même config que TerrainDAOPostgres
    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public Product getProductById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du produit avec id=" + id, e);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        String sql = "SELECT * FROM products ORDER BY id";
        List<Product> products = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            return products;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les produits", e);
        }
    }

    @Override
    public List<Product> getProductsByOwnerId(Long ownerId) {
        String sql = "SELECT * FROM products WHERE owner_id = ? ORDER BY id";
        List<Product> products = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, ownerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            return products;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des produits du propriétaire id=" + ownerId, e);
        }
    }

    @Override
    public void saveProduct(Product product) {
        String sql = """
                INSERT INTO products (owner_id, name, description, price, rental_price_day, stock, is_sellable, is_rentable)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, product.getOwnerId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setDouble(4, product.getPrice());
            stmt.setDouble(5, product.getRentalPricePerDay());
            stmt.setInt(6, product.getStock());
            stmt.setBoolean(7, product.isSellable());
            stmt.setBoolean(8, product.isRentable());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long generatedId = rs.getLong("id");
                product.setId(generatedId); // on met à jour l'objet
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du produit", e);
        }
    }

    @Override
    public void updateProduct(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un produit sans id");
        }

        String sql = """
                UPDATE products
                SET owner_id = ?, name = ?, description = ?, price = ?, rental_price_day = ?, 
                    stock = ?, is_sellable = ?, is_rentable = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, product.getOwnerId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setDouble(4, product.getPrice());
            stmt.setDouble(5, product.getRentalPricePerDay());
            stmt.setInt(6, product.getStock());
            stmt.setBoolean(7, product.isSellable());
            stmt.setBoolean(8, product.isRentable());
            stmt.setLong(9, product.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du produit id=" + product.getId(), e);
        }
    }

    @Override
    public void deleteProduct(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du produit id=" + id, e);
        }
    }

    @Override
    public void updateStock(Long productId, int newStock) {
        String sql = "UPDATE products SET stock = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newStock);
            stmt.setLong(2, productId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du stock du produit id=" + productId, e);
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setOwnerId(rs.getLong("owner_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getDouble("price"));
        product.setRentalPricePerDay(rs.getDouble("rental_price_day"));
        product.setStock(rs.getInt("stock"));
        product.setSellable(rs.getBoolean("is_sellable"));
        product.setRentable(rs.getBoolean("is_rentable"));
        return product;
    }
}