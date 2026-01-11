package com.example.myarena.domain;

public enum TournamentStatus {
    AwaitingApproval,    // En attente de validation par l'admin
    Approved,             // Approuvé et visible aux clients
    Rejected,             // Rejeté par l'admin
    Open,                 // Ouvert aux inscriptions
    Closed,               // Fermé aux inscriptions
    InProgress,           // En cours
    Completed,            // Terminé
    Cancelled             // Annulé
}