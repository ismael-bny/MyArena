package com.example.myarena.facade;

import com.example.myarena.domain.*;
import com.example.myarena.services.SubscriptionManager;
import java.util.List;

public class SubscriptionFacade {
    private static SubscriptionFacade instance;
    private final SubscriptionManager subscriptionManager;

    private SubscriptionFacade() {
        this.subscriptionManager = new SubscriptionManager();
    }

    public static SubscriptionFacade getInstance() {
        if (instance == null) {
            instance = new SubscriptionFacade();
        }
        return instance;
    }

    // --- Use Case: View/Subscribe ---
    public List<SubscriptionPlan> getSubscriptionPlans() {
        return subscriptionManager.getSubscriptionPlans();
    }

    public boolean subscribeToPlan(Long planId) {
        User user = UserSession.getInstance().getUser();
        if (user == null) return false;
        try {
            subscriptionManager.subscribeToPlan(user.getId(), planId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- Use Case: Cancel Subscription (Fixed for your Controller) ---
    public boolean cancelSubscription() {
        User user = UserSession.getInstance().getUser();
        if (user == null) return false;
        return subscriptionManager.cancelSubscription(user.getId());
    }

    public Subscription getActiveSubscription() {
        User user = UserSession.getInstance().getUser();
        if (user == null) return null;
        return subscriptionManager.getActiveSubscription(user.getId());
    }

    // --- Use Case: Manage Plans (Admin/Owner only) ---
    public SubscriptionPlan createPlan(SubscriptionPlan plan) {
        validateAdminAccess();
        return subscriptionManager.createPlan(plan);
    }

    public SubscriptionPlan updatePlan(SubscriptionPlan plan) {
        validateAdminAccess();
        return subscriptionManager.updatePlan(plan);
    }

    public void deletePlan(Long planId) {
        validateAdminAccess();
        subscriptionManager.deletePlan(planId);
    }

    private void validateAdminAccess() {
        User user = UserSession.getInstance().getUser();
        if (user == null || (user.getRole() != UserRole.OWNER && user.getRole() != UserRole.ADMIN)) {
            throw new IllegalStateException("Permission refusée : accès réservé aux administrateurs.");
        }
    }
}