package com.example.myarena.facade;


import com.example.myarena.domain.User;

public class UserSession {
    private static UserSession instance;
    private User user;

    private UserSession(User user){
        this.user = user;
    }

    public static void setSession(User user) {
        instance = new UserSession(user);
    }

    public static UserSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("User not logged in!");
        }
        return instance;
    }

    public static void cleanSession() {
        instance = null;
    }

    public User getUser() {
        return user;
    }
}
