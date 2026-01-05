package com.example.myarena.persistance.dao;

import com.example.myarena.domain.SubscriptionPlan;
import java.util.List;
public interface SubscriptionPlanDAO {

    /**
     * Crée un nouveau plan d'abonnement dans la base de données.
     */
    SubscriptionPlan create(SubscriptionPlan plan);

    /**
     * Met à jour un plan d'abonnement existant.
     */
    SubscriptionPlan update(SubscriptionPlan plan);

    /**
     * Supprime un plan d'abonnement par son ID.
     */
    void delete(Long id);

    /**
     * Récupère un plan d'abonnement par son ID.
     */
    SubscriptionPlan getById(Long id);

    /**
     * Récupère tous les plans d'abonnement disponibles.
     */
    List<SubscriptionPlan> getAll();

}
