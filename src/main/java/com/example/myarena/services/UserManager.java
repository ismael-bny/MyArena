package com.example.myarena.services;

import com.example.myarena.persistance.dao.UserDAO;
import com.example.myarena.domain.User;
import com.example.myarena.persistance.factory.AbstractFactory;
import com.example.myarena.persistance.factory.PostgresFactory;

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

        User u = userDAO.getUserByCredentials(email, pwd);
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

