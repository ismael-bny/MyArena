package com.example.myarena.services;

import com.example.myarena.persistance.dao.UserDAO;
import com.example.myarena.domain.User;

public class UserManager {
    private final UserDAO userDAO;
    private User currentUser;

    public UserManager(UserDAO userDAO) {
        if (userDAO == null) throw new IllegalArgumentException("userDAO cannot be null");
        this.userDAO = userDAO;
    }

    /**
     * @return l'utilisateur connecté, ou null si credentials invalides
     */
    public User login(String id, String pwd) {
        if (id == null || id.isBlank() || pwd == null || pwd.isBlank()) {
            return null;
        }

        User u = userDAO.getUserByCredentials(id, pwd);
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
}

