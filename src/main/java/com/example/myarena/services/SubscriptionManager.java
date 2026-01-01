package com.example.myarena.services;
import com.example.myarena.domain.Subscription;
import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.domain.SubscriptionStatus;
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

    /**
     * Constructeur : initialise les DAO via la factory.
     */
    public SubscriptionManager() {
        AbstractFactory factory = new PostgresFactory();
        this.subscriptionDAO = factory.createSubscriptionDAO();
        this.subscriptionPlanDAO = factory.createSubscriptionPlanDAO();
    }

    /**
     * Annule l'abonnement actif d'un utilisateur.
     * LOGIQUE MÉTIER :
     * 1. Vérifie qu'un abonnement actif existe
     * 2. Change le statut à CANCELLED
     * 3. Désactive le renouvellement automatique
     */
    public boolean cancelSubscription(Long userId) {
        // Étape 1 : Récupérer l'abonnement actif
        Subscription subscription = subscriptionDAO.findActiveByUserId(userId);

        // Étape 2 : Vérifier qu'il existe
        if (subscription == null) {
            return false; // Aucun abonnement actif à annuler
        }

        // Étape 3 : Modifier le statut et désactiver le renouvellement
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);

        // Étape 4 : Sauvegarder les changements
        subscriptionDAO.update(subscription);

        return true;
    }

    public Subscription getActiveSubscription(Long userId) {
        return subscriptionDAO.findActiveByUserId(userId);
    }

    /**
     * Souscrit un utilisateur à un plan d'abonnement.
     * LOGIQUE MÉTIER :
     * 1. Vérifie que l'utilisateur n'a pas déjà un abonnement actif
     * 2. Vérifie que le plan existe et est actif
     * 3. Calcule les dates (start = aujourd'hui, end = start + durée du plan)
     * 4. Crée l'abonnement avec auto-renew activé par défaut
     */
    public Subscription subscribeToPlan(Long userId, Long planId) {
        // Étape 1 : Vérifier qu'il n'y a pas déjà un abonnement actif
        Subscription existingSubscription = subscriptionDAO.findActiveByUserId(userId);
        if (existingSubscription != null) {
            throw new IllegalStateException(
                    "L'utilisateur a déjà un abonnement actif. " + "Veuillez d'abord annuler l'abonnement existant."
            );
        }

        // Étape 2 : Récupérer le plan et vérifier qu'il existe et est actif
        SubscriptionPlan plan = subscriptionPlanDAO.getById(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Le plan d'abonnement avec l'ID " + planId + " n'existe pas.");
        }
        if (!plan.isActive()) {
            throw new IllegalArgumentException("Le plan d'abonnement '" + plan.getName() + "' n'est plus disponible.");
        }

        // Étape 3 : Calculer les dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(plan.getDurationMonths());

        // Étape 4 : Créer le nouvel abonnement
        Subscription newSubscription = new Subscription();
        newSubscription.setUserId(userId);
        newSubscription.setPlan(plan);
        newSubscription.setStartDate(startDate);
        newSubscription.setEndDate(endDate);
        newSubscription.setAutoRenew(true);
        newSubscription.setStatus(SubscriptionStatus.ACTIVE);

        // Étape 5 : Enregistrer en base de données
        return subscriptionDAO.save(newSubscription);
    }
    /**
     * Crée un nouveau plan d'abonnement.
     * VALIDATION :
     * - Le nom ne doit pas être vide
     * - Le prix doit être > 0
     * - La durée doit être > 0
     */
    public SubscriptionPlan createPlan(SubscriptionPlan plan) {
        // Validation des données
        validatePlan(plan);

        // Création du plan
        return subscriptionPlanDAO.create(plan);
    }

    /**
     * Met à jour un plan d'abonnement existant.
     */
    public SubscriptionPlan updatePlan(SubscriptionPlan plan) {

        validatePlan(plan);
        if (plan.getId() == null) {
            throw new IllegalArgumentException("L'ID du plan est requis pour la mise à jour.");
        }

        SubscriptionPlan existingPlan = subscriptionPlanDAO.getById(plan.getId());
        if (existingPlan == null) {
            throw new IllegalArgumentException("Le plan avec l'ID " + plan.getId() + " n'existe pas.");
        }

        // Mise à jour
        return subscriptionPlanDAO.update(plan);
    }

    /**
     * Supprime un plan d'abonnement.
     */
    public void deletePlan(Long planId) {
        // Vérifier que le plan existe
        SubscriptionPlan plan = subscriptionPlanDAO.getById(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Le plan avec l'ID " + planId + " n'existe pas.");
        }

        // Suppression (peut échouer si des abonnements actifs l'utilisent)
        subscriptionPlanDAO.delete(planId);
    }

    /**
     * Récupère tous les plans d'abonnement disponibles.
     */
    public List<SubscriptionPlan> getSubscriptionPlans() {
        return subscriptionPlanDAO.getAll();
    }


     //Valide les données d'un plan d'abonnement.
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
