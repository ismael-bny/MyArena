package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Notification;
import com.example.myarena.domain.NotificationStatus;
import com.example.myarena.domain.NotificationType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOPostgres implements NotificationDAO {

    private static final String URL =
            "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena" +
                    "?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public Notification save(Notification n) {
        String sql = """
            INSERT INTO notifications (user_id, type, title, message, status)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id, created_at, status
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, n.getUserId());
            stmt.setString(2, n.getType().name());
            stmt.setString(3, n.getTitle());
            stmt.setString(4, n.getMessage());
            stmt.setString(5, n.getStatus().name());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                n.setId(rs.getLong("id"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) n.setCreatedAt(ts.toLocalDateTime());
                n.setStatus(NotificationStatus.valueOf(rs.getString("status")));
            }
            return n;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving notification", e);
        }
    }

    @Override
    public List<Notification> findByUserId(Long userId) {
        String sql = """
            SELECT id, user_id, type, title, message, status, created_at
            FROM notifications
            WHERE user_id = ?
            ORDER BY created_at DESC
            """;

        List<Notification> res = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    res.add(map(rs));
                }
            }
            return res;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching notifications for user_id=" + userId, e);
        }
    }

    @Override
    public Notification getById(Long id) {
        String sql = """
            SELECT id, user_id, type, title, message, status, created_at
            FROM notifications
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching notification id=" + id, e);
        }
    }

    @Override
    public Notification updateStatus(Long id, NotificationStatus status) {
        String sql = """
            UPDATE notifications
            SET status = ?
            WHERE id = ?
            RETURNING id, user_id, type, title, message, status, created_at
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setLong(2, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating notification status id=" + id, e);
        }
    }

    private Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();

        n.setId(rs.getLong("id"));
        n.setUserId(rs.getLong("user_id"));

        String typeStr = rs.getString("type");
        if (typeStr != null) n.setType(NotificationType.valueOf(typeStr));

        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));

        String statusStr = rs.getString("status");
        if (statusStr != null) n.setStatus(NotificationStatus.valueOf(statusStr));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            LocalDateTime createdAt = ts.toLocalDateTime();
            n.setCreatedAt(createdAt);
        }

        return n;
    }
}
