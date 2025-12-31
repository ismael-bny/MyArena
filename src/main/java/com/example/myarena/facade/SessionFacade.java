package com.example.myarena.facade;
import com.example.myarena.domain.User;
import com.example.myarena.services.UserManager;

public class SessionFacade {
    private static SessionFacade instance;
    private final UserManager userManager;

    private SessionFacade() {
        this.userManager = new UserManager();
    }
    
    // Méthode pour obtenir l'instance unique
    public static SessionFacade getInstance() {
        if (instance == null) {
            instance = new SessionFacade();
        }
        return instance;
    }

    public boolean login(String id, String pwd) {
        User currentUser = userManager.login(id, pwd);  // ← Stocke le user
        if (currentUser != null) {
            UserSession.setSession(currentUser);  // Set the user session
            return true;
        }
        return false;
    }

    public User getUser() {
        // Delegates to your existing UserSession class
        return UserSession.getInstance().getUser();
    }
}
