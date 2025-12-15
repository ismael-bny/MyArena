package com.example.myarena.persistance.dao;

import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;
import com.example.myarena.domain.UserStatus;

import java.sql.*;

public class UserDAOPostgres implements UserDAO {

    private static final String URL = "jdbc:postgresql://localhost:5430/myarena";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public User getUserByCredentials(String login, String pwd) {
        String sql = "SELECT * FROM users WHERE email = ? AND password_hash = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, pwd);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur", e);
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
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPwdHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        return user;
    }

}
