package com.example.myarena.facade;

import com.example.myarena.domain.User;
import com.example.myarena.services.UserManager;

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

    public void logout() {
        UserSession.getInstance().cleanSession();
    }

    public User getCurrentUser() {
        return UserSession.getInstance().getUser();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }
}