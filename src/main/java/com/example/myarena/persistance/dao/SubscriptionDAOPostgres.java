package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Subscription;
import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.domain.SubscriptionStatus;
import com.example.myarena.util.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;

public class SubscriptionDAOPostgres implements SubscriptionDAO {
    @Override
    public Subscription findActiveByUserId(Long userId) {
        String sql = "SELECT s.id, s.user_id, s.start_date, s.end_date, s.auto_renew, s.status, " +
                "p.id AS plan_id, p.name, p.price, p.duration_months, p.active " +
                "FROM subscription s " +
                "INNER JOIN subscription_plan p ON s.plan_id = p.id " +
                "WHERE s.user_id = ? AND s.status = 'ACTIVE'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSubscription(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'abonnement actif pour l'utilisateur " + userId, e);
        }
    }

    @Override
    public Subscription update(Subscription subscription) {
        String sql = "UPDATE subscription SET status = ?, auto_renew = ?, end_date = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, subscription.getStatus().name());
            stmt.setBoolean(2, subscription.isAutoRenew());
            stmt.setDate(3, Date.valueOf(subscription.getEndDate()));
            stmt.setLong(4, subscription.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Aucun abonnement trouvé avec l'ID: " + subscription.getId());
            }

            return subscription;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'abonnement", e);
        }
    }

    @Override
    public Subscription save(Subscription subscription) {
        String sql = "INSERT INTO subscription (user_id, plan_id, start_date, end_date, auto_renew, status) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, subscription.getUserId());
            stmt.setLong(2, subscription.getPlan().getId());
            stmt.setDate(3, Date.valueOf(subscription.getStartDate()));
            stmt.setDate(4, Date.valueOf(subscription.getEndDate()));
            stmt.setBoolean(5, subscription.isAutoRenew());
            stmt.setString(6, subscription.getStatus().name());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                subscription.setId(rs.getLong("id"));
            }

            return subscription;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'abonnement", e);
        }
    }

    /**
     * Méthode utilitaire pour transformer une ligne SQL (ResultSet)
     * en objet Subscription Java COMPLET (avec son SubscriptionPlan).
     * Cette méthode attend un ResultSet provenant d'une jointure entre
     * subscription et subscription_plan.
     */
    private Subscription mapResultSetToSubscription(ResultSet rs) throws SQLException {
        // Étape 1 : Créer et remplir le SubscriptionPlan
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(rs.getLong("plan_id"));
        plan.setName(rs.getString("name"));
        plan.setPrice(rs.getDouble("price"));
        plan.setDurationMonths(rs.getInt("duration_months"));
        plan.setActive(rs.getBoolean("active"));

        // Étape 2 : Créer et remplir le Subscription
        Subscription subscription = new Subscription();
        subscription.setId(rs.getLong("id"));
        subscription.setUserId(rs.getLong("user_id"));
        subscription.setStartDate(rs.getDate("start_date").toLocalDate());
        subscription.setEndDate(rs.getDate("end_date").toLocalDate());
        subscription.setAutoRenew(rs.getBoolean("auto_renew"));
        subscription.setStatus(SubscriptionStatus.valueOf(rs.getString("status")));
        subscription.setPlan(plan);

        return subscription;
    }
}
