package com.example.myarena.domain;

public enum SubscriptionStatus {

     // Abonnement actif - l'utilisateur bénéficie des avantages de l'abonnement
    ACTIVE,

    // Abonnement annulé - l'utilisateur a annulé son abonnement (auto-renew désactivé)
    CANCELLED,

    //Abonnement expiré - la date de fin est dépassée
    EXPIRED
}
