package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Subscription;

public interface SubscriptionDAO {
    /**
     * Trouve l'abonnement actif d'un utilisateur.
     * Un utilisateur ne peut avoir qu'un seul abonnement actif à la fois.
     */
    Subscription findActiveByUserId(Long userId);

    /**
     * Met à jour un abonnement existant (par exemple pour changer le statut ou auto-renew).
     */
    Subscription update(Subscription subscription);

    /**
     * Enregistre un nouvel abonnement dans la base de données.
     */
    Subscription save(Subscription subscription);
}
