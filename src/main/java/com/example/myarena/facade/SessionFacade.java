package com.example.myarena.facade;

import com.example.myarena.domain.User;
import com.example.myarena.services.UserManager;
import java.util.regex.Pattern;

public class SessionFacade {
    private static SessionFacade instance;
    private final UserManager userManager;

    private SessionFacade() {
        this.userManager = new UserManager();
    }

    public static SessionFacade getInstance() {
        if (instance == null) {
            instance = new SessionFacade();
        }
        return instance;
    }

    public boolean login(String email, String pwd) {
        User user = userManager.login(email, pwd);
        if (user != null) {
            UserSession.getInstance().setUser(user);
            return true;
        }
        return false;
    }

    public String register(String name, String email, String password, String confirmPassword, String phone) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            return "Name is required";
        }
        if (name.length() < 2) {
            return "Name must be at least 2 characters";
        }

        // Validate email
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }

        // Validate password
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        // Check if email already exists
        if (userManager.emailExists(email)) {
            return "Email already exists. Please login instead.";
        }

        // Register user
        try {
            User newUser = userManager.register(name.trim(), email.trim(), password, phone != null ? phone.trim() : null);
            if (newUser != null) {
                return null; // Success
            } else {
                return "Registration failed. Please try again.";
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "An unexpected error occurred. Please try again.";
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public void logout() {
        UserSession.getInstance().cleanSession();
    }

    public User getCurrentUser() {
        return UserSession.getInstance().getUser();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Update profile with validation
     * @return null on success, error message on failure
     */
    public String updateProfile(String name, String email, String phone) {
        // Validate name
        if (name == null || name.trim().length() < 2) {
            return "Name must be at least 2 characters";
        }

        // Validate email format
        if (email == null || !isValidEmail(email)) {
            return "Invalid email format";
        }

        try {
            userManager.updateProfile(name.trim(), email.trim(), phone != null ? phone.trim() : null);
            return null; // Success
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }

    /**
     * Change password with validation
     * @return null on success, error message on failure
     */
    public String changePassword(String currentPassword, String newPassword, String confirmPassword) {
        // Validate inputs
        if (currentPassword == null || currentPassword.isBlank()) {
            return "Current password is required";
        }
        if (newPassword == null || newPassword.length() < 6) {
            return "New password must be at least 6 characters";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        try {
            userManager.changePassword(currentPassword, newPassword);
            return null; // Success
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }

}