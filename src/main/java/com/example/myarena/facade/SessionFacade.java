package com.example.myarena.facade;
import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;
import com.example.myarena.services.UserManager;
import com.example.myarena.services.SubscriptionManager;
import com.example.myarena.domain.Subscription;

import java.util.List;


public class SessionFacade {
    private static SessionFacade instance;
    private final UserManager userManager;
    private final SubscriptionManager subscriptionManager;
    private User currentUser;

    private SessionFacade() {

        this.userManager = new UserManager();
        this.subscriptionManager = new SubscriptionManager();
    }
    
    // Méthode pour obtenir l'instance unique
    public static SessionFacade getInstance() {
        if (instance == null) {
            instance = new SessionFacade();
        }
        return instance;
    }

    public boolean login(String id, String pwd) {
        this.currentUser = userManager.login(id, pwd);  // ← Stocke le user
        return currentUser != null;
    }

    public boolean subscribeToPlan(Long planId) {
        // Vérifier qu'un utilisateur est connecté
        if (this.currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté.");
        }

        try {
            // Appeler le service avec l'ID de l'utilisateur connecté
            subscriptionManager.subscribeToPlan(currentUser.getId(), planId);
            return true;
        } catch (IllegalStateException | IllegalArgumentException e) {
            // En cas d'erreur métier (déjà abonné, plan invalide, etc.)
            System.err.println("Erreur lors de la souscription : " + e.getMessage());
            return false;
        }
    }
    //Annule l'abonnement de l'utilisateur connecté.
    public boolean cancelSubscription(Long userId) {
        return subscriptionManager.cancelSubscription(userId);
    }

    public Subscription getActiveSubscription(Long userId) {
        return subscriptionManager.getActiveSubscription(userId);
    }

    //Récupère la liste de tous les plans d'abonnement disponibles.
    public List<SubscriptionPlan> getSubscriptionPlans() {
        return subscriptionManager.getSubscriptionPlans();
    }

    public SubscriptionPlan createPlan(SubscriptionPlan plan) {
        if (currentUser == null || (currentUser.getRole() != UserRole.OWNER && currentUser.getRole() != UserRole.ADMIN )) {
            throw new IllegalStateException("Permission refusée : accès réservé aux administrateurs et propriétaires.");
        }
        return subscriptionManager.createPlan(plan);
    }

    public SubscriptionPlan updatePlan(SubscriptionPlan plan) {
        if (currentUser == null || (currentUser.getRole() != UserRole.OWNER && currentUser.getRole() != UserRole.ADMIN )) {
            throw new IllegalStateException("Permission refusée : accès réservé aux administrateurs et propriétaires.");
        }
        return subscriptionManager.updatePlan(plan);
    }

    public void deletePlan(Long planId) {
        if (currentUser == null || (currentUser.getRole() != UserRole.OWNER && currentUser.getRole() != UserRole.ADMIN )) {
            throw new IllegalStateException("Permission refusée : accès réservé aux administrateurs et propriétaires.");
        }
        subscriptionManager.deletePlan(planId);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isUserLoggedIn() {
        if (currentUser == null) {
            return false;
        }
        return true;
    }
}
