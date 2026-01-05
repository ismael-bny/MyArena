package com.example.myarena.services;

import com.example.myarena.persistance.dao.UserDAO;
import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;
import com.example.myarena.domain.UserStatus;
import com.example.myarena.persistance.factory.AbstractFactory;
import com.example.myarena.persistance.factory.PostgresFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class UserManager {
    private final UserDAO userDAO;
    private User currentUser;

    public UserManager() {
        AbstractFactory factory = new PostgresFactory();
        this.userDAO = factory.createUserDAO();
    }

    /**
     * @return l'utilisateur connecté, ou null si credentials invalides
     */
    public User login(String email, String pwd) {
        if (email == null || email.isBlank() || pwd == null || pwd.isBlank()) {
            return null;
        }

        String hashedPassword = hashPassword(pwd);
        User u = userDAO.getUserByCredentials(email, hashedPassword);

        if (u == null) {
            currentUser = null;
            return null;
        }

        if (!u.isActive()) {
            currentUser = null;
            return null;
        }

        currentUser = u;
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User register(String name, String email, String plainPassword, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // Check if email already exists
        if (emailExists(email)) {
            throw new IllegalStateException("Email already exists");
        }

        // Create new user
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPwdHash(hashPassword(plainPassword));
        newUser.setPhone(phone);
        newUser.setRole(UserRole.CLIENT);
        newUser.setStatus(UserStatus.ACTIVE);

        // Save to database
        userDAO.saveUser(newUser);

        return newUser;
    }

    public boolean emailExists(String email) {
        return userDAO.getUserByEmail(email) != null;
    }

    private String hashPassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Update current user profile (name, email, phone)
     * @return updated User object
     */
    public User updateProfile(String name, String email, String phone) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in");
        }

        // Validate inputs
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        // Check if email changed and if new email already exists
        if (!currentUser.getEmail().equals(email)) {
            if (emailExists(email)) {
                throw new IllegalStateException("Email already exists");
            }
        }

        // Update user object
        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);

        // Persist changes
        userDAO.updateUser(currentUser);

        return currentUser;
    }

    /**
     * Change password for current user
     */
    public void changePassword(String currentPassword, String newPassword) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in");
        }

        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // Verify current password
        String currentHash = hashPassword(currentPassword);
        if (!currentHash.equals(currentUser.getPwdHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Hash new password
        String newHash = hashPassword(newPassword);

        // Update in database
        userDAO.changePassword(currentUser.getId(), newHash);

        // Update current user object
        currentUser.setPwdHash(newHash);
    }
}

