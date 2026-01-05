package com.example.myarena.persistance.dao;

import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;
import com.example.myarena.domain.UserStatus;

import java.sql.*;

public class UserDAOPostgres implements UserDAO {

    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public User getUserByCredentials(String login, String pwd) {
        // ✅ Changed SELECT * to explicit columns to ensure 'id' is found
        String sql = "SELECT id, name, email, password_hash, phone, role, status FROM users WHERE email = ? AND password_hash = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, pwd);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User u = mapResultSetToUser(rs);
                // ✅ DEBUG: Print the ID to console so you can verify it
                System.out.println("DEBUG: Found User ID = " + u.getId());
                return u;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace(); // Print full error
            throw new RuntimeException("Error fetching user", e);
        }
    }

    @Override
    public User getUserByID(Long id) {
        throw new UnsupportedOperationException("Pas encore implémenté");
    }

    @Override
    public void saveUser(User u) {
        throw new UnsupportedOperationException("Pas encore implémenté");
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        long dbId = rs.getLong("id");
        user.setId(dbId);
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPwdHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        return user;
    }

}
