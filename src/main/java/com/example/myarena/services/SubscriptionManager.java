package com.example.myarena.services;

import com.example.myarena.domain.Subscription;
import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.domain.SubscriptionStatus;
import com.example.myarena.domain.NotificationType;
import com.example.myarena.persistance.dao.SubscriptionDAO;
import com.example.myarena.persistance.dao.SubscriptionPlanDAO;
import com.example.myarena.persistance.factory.AbstractFactory;
import com.example.myarena.persistance.factory.PostgresFactory;

import java.time.LocalDate;
import java.util.List;

public class SubscriptionManager {
    // DAO pour accéder aux données
    private final SubscriptionDAO subscriptionDAO;
    private final SubscriptionPlanDAO subscriptionPlanDAO;
    private final NotificationManager notificationManager;

    /**
     * Constructeur : initialise les DAO via la factory.
     */
    public SubscriptionManager() {
        AbstractFactory factory = new PostgresFactory();
        this.subscriptionDAO = factory.createSubscriptionDAO();
        this.subscriptionPlanDAO = factory.createSubscriptionPlanDAO();

        // ✅ NEW
        this.notificationManager = new NotificationManager();
    }

    public boolean cancelSubscription(Long userId) {
        Subscription subscription = subscriptionDAO.findActiveByUserId(userId);

        if (subscription == null) { return false; }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);

        subscriptionDAO.update(subscription);

        // notif
        try {
            String planName = (subscription.getPlan() != null && subscription.getPlan().getName() != null)
                    ? subscription.getPlan().getName()
                    : "your plan";

            notificationManager.createNotification(
                    userId,
                    NotificationType.SUBSCRIPTION_CHANGE,
                    "Subscription Cancelled",
                    "Your subscription (" + planName + ") has been cancelled."
            );
        } catch (Exception e) {
            System.err.println("WARN: failed to create notification (cancelSubscription): " + e.getMessage());
        }

        return true;
    }

    public Subscription getActiveSubscription(Long userId) {
        return subscriptionDAO.findActiveByUserId(userId);
    }

    public Subscription subscribeToPlan(Long userId, Long planId) {
        Subscription existingSubscription = subscriptionDAO.findActiveByUserId(userId);
        if (existingSubscription != null) {
            throw new IllegalStateException(
                    "L'utilisateur a déjà un abonnement actif. " + "Veuillez d'abord annuler l'abonnement existant."
            );
        }

        SubscriptionPlan plan = subscriptionPlanDAO.getById(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Le plan d'abonnement avec l'ID " + planId + " n'existe pas.");
        }
        if (!plan.isActive()) {
            throw new IllegalArgumentException("Le plan d'abonnement '" + plan.getName() + "' n'est plus disponible.");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(plan.getDurationMonths());

        Subscription newSubscription = new Subscription();
        newSubscription.setUserId(userId);
        newSubscription.setPlan(plan);
        newSubscription.setStartDate(startDate);
        newSubscription.setEndDate(endDate);
        newSubscription.setAutoRenew(true);
        newSubscription.setStatus(SubscriptionStatus.ACTIVE);

        Subscription saved = subscriptionDAO.save(newSubscription);

        // notif
        try {
            notificationManager.createNotification(
                    userId,
                    NotificationType.SUBSCRIPTION_CHANGE,
                    "Subscription Activated",
                    "Your subscription to '" + plan.getName() + "' is active until " + endDate + "."
            );
        } catch (Exception e) {
            System.err.println("WARN: failed to create notification (subscribeToPlan): " + e.getMessage());
        }

        return saved;
    }

    public SubscriptionPlan createPlan(SubscriptionPlan plan) {
        validatePlan(plan);
        return subscriptionPlanDAO.create(plan);
    }

    public SubscriptionPlan updatePlan(SubscriptionPlan plan) {
        validatePlan(plan);
        if (plan.getId() == null) {
            throw new IllegalArgumentException("L'ID du plan est requis pour la mise à jour.");
        }

        SubscriptionPlan existingPlan = subscriptionPlanDAO.getById(plan.getId());
        if (existingPlan == null) {
            throw new IllegalArgumentException("Le plan avec l'ID " + plan.getId() + " n'existe pas.");
        }

        return subscriptionPlanDAO.update(plan);
    }

    public void deletePlan(Long planId) {
        SubscriptionPlan plan = subscriptionPlanDAO.getById(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Le plan avec l'ID " + planId + " n'existe pas.");
        }

        subscriptionPlanDAO.delete(planId);
    }

    public List<SubscriptionPlan> getSubscriptionPlans() {
        return subscriptionPlanDAO.getAll();
    }

    private void validatePlan(SubscriptionPlan plan) {
        if (plan.getName() == null || plan.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du plan est obligatoire.");
        }

        if (plan.getPrice() <= 0) {
            throw new IllegalArgumentException("Le prix doit être supérieur à 0.");
        }

        if (plan.getDurationMonths() <= 0) {
            throw new IllegalArgumentException("La durée doit être supérieure à 0 mois.");
        }
    }
}
