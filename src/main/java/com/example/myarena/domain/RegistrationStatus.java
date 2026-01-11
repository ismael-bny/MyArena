package com.example.myarena.domain;

public enum RegistrationStatus {
    PendingValidation,    // En attente de validation par l'organisateur
    Validated,            // Validée et confirmée
    Rejected,             // Rejetée par l'organisateur
    Cancelled             // Annulée par le client
}