package com.example.myarena.facade;

public class SessionFacade {
    private static SessionFacade instance;
    private UserManager userManager;

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
        return userManager.login(id, pwd);
    }
}
