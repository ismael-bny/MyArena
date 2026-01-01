package com.example.myarena.persistance.dao;
import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionPlanDAOPostgres implements SubscriptionPlanDAO{
    @Override
    public SubscriptionPlan create(SubscriptionPlan plan) {
        String sql = "INSERT INTO subscription_plan (name, price, duration_months, active) " +
                "VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plan.getName());
            stmt.setDouble(2, plan.getPrice());
            stmt.setInt(3, plan.getDurationMonths());
            stmt.setBoolean(4, plan.isActive());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                plan.setId(rs.getLong("id"));
            }
            return plan;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du plan d'abonnement", e);
        }
    }

    @Override
    public SubscriptionPlan update(SubscriptionPlan plan) {
        String sql = "UPDATE subscription_plan SET name = ?, price = ?, " +
                "duration_months = ?, active = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plan.getName());
            stmt.setDouble(2, plan.getPrice());
            stmt.setInt(3, plan.getDurationMonths());
            stmt.setBoolean(4, plan.isActive());
            stmt.setLong(5, plan.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Aucun plan trouvé avec l'ID: " + plan.getId());
            }

            return plan;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du plan d'abonnement", e);
        }
    }

    @Override
    public void delete(Long id) {

        String sql = "DELETE FROM subscription_plan WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Aucun plan trouvé avec l'ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du plan d'abonnement", e);
        }
    }

    @Override
    public SubscriptionPlan getById(Long id) {
        String sql = "SELECT * FROM subscription_plan WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSubscriptionPlan(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du plan d'abonnement", e);
        }
    }

    @Override
    public List<SubscriptionPlan> getAll() {

        String sql = "SELECT * FROM subscription_plan ORDER BY id";

        List<SubscriptionPlan> plans = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                plans.add(mapResultSetToSubscriptionPlan(rs));
            }

            return plans;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des plans d'abonnement", e);
        }
    }

    /**
     * Méthode utilitaire pour transformer une ligne SQL (ResultSet)
     * en objet SubscriptionPlan Java.
     */
    private SubscriptionPlan mapResultSetToSubscriptionPlan(ResultSet rs) throws SQLException {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(rs.getLong("id"));
        plan.setName(rs.getString("name"));
        plan.setPrice(rs.getDouble("price"));
        plan.setDurationMonths(rs.getInt("duration_months"));
        plan.setActive(rs.getBoolean("active"));
        return plan;
    }
}
