package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Discount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation PostgreSQL pour la persistance des codes promo
 */
public class DiscountDAOPostgres implements DiscountDAO {

    // Même config que TerrainDAOPostgres
    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public Discount getDiscountById(Long id) {
        String sql = "SELECT * FROM discounts WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDiscount(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du code promo avec id=" + id, e);
        }
    }

    @Override
    public Discount getDiscountByCode(String code) {
        String sql = "SELECT * FROM discounts WHERE code = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDiscount(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du code promo avec code=" + code, e);
        }
    }

    @Override
    public List<Discount> getActiveDiscounts() {
        String sql = "SELECT * FROM discounts WHERE active = true ORDER BY id";
        List<Discount> discounts = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                discounts.add(mapResultSetToDiscount(rs));
            }
            return discounts;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des codes promo actifs", e);
        }
    }

    @Override
    public List<Discount> getAllDiscounts() {
        String sql = "SELECT * FROM discounts ORDER BY id";
        List<Discount> discounts = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                discounts.add(mapResultSetToDiscount(rs));
            }
            return discounts;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les codes promo", e);
        }
    }

    @Override
    public void saveDiscount(Discount discount) {
        String sql = """
                INSERT INTO discounts (code, discount_value, expiration_date, active)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, discount.getCode());
            stmt.setDouble(2, discount.getDiscountValue());
            stmt.setDate(3, Date.valueOf(discount.getExpirationDate()));
            stmt.setBoolean(4, discount.isActive());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long generatedId = rs.getLong("id");
                discount.setId(generatedId); // on met à jour l'objet
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du code promo", e);
        }
    }

    @Override
    public void updateDiscount(Discount discount) {
        if (discount.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un code promo sans id");
        }

        String sql = """
                UPDATE discounts
                SET code = ?, discount_value = ?, expiration_date = ?, active = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, discount.getCode());
            stmt.setDouble(2, discount.getDiscountValue());
            stmt.setDate(3, Date.valueOf(discount.getExpirationDate()));
            stmt.setBoolean(4, discount.isActive());
            stmt.setLong(5, discount.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du code promo id=" + discount.getId(), e);
        }
    }

    @Override
    public void deleteDiscount(Long id) {
        String sql = "DELETE FROM discounts WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du code promo id=" + id, e);
        }
    }

    private Discount mapResultSetToDiscount(ResultSet rs) throws SQLException {
        Discount discount = new Discount();
        discount.setId(rs.getLong("id"));
        discount.setCode(rs.getString("code"));
        discount.setDiscountValue(rs.getDouble("discount_value"));

        Date expirationDate = rs.getDate("expiration_date");
        if (expirationDate != null) {
            discount.setExpirationDate(expirationDate.toString());
        }

        discount.setActive(rs.getBoolean("active"));
        return discount;
    }
}