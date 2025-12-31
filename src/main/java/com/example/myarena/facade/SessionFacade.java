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

    public boolean login(String id, String pwd) {
        User currentUser = userManager.login(id, pwd);
        if (currentUser != null) {
            // ✅ CRITICAL FIX: Save the user directly to UserSession
            UserSession.getInstance().setUser(currentUser);
            System.out.println("SessionFacade: User stored in session with ID=" + currentUser.getId());
            return true;
        }
        return false;
    }

    public void logout() {
        UserSession.getInstance().cleanSession();
    }
}