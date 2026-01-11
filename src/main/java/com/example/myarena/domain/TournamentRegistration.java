package com.example.myarena.domain;

import java.util.Date;

/**
 * Représente l'inscription d'un client à un tournoi
 */
public class TournamentRegistration {
    private Long id;
    private Long tournamentId;
    private Long userId;
    private RegistrationStatus status;
    private Date registeredAt;
    private String notes;  // Notes ou commentaires de l'organisateur

    // Constructeur par défaut
    public TournamentRegistration() {
        this.status = RegistrationStatus.PendingValidation;
        this.registeredAt = new Date();
    }

    // Constructeur avec paramètres essentiels
    public TournamentRegistration(Long tournamentId, Long userId) {
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.status = RegistrationStatus.PendingValidation;
        this.registeredAt = new Date();
    }

    public TournamentRegistration(Long tournamentId, Long userId, RegistrationStatus status) {
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.status = status;
        this.registeredAt = new Date();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "TournamentRegistration{" +
                "id=" + id +
                ", tournamentId=" + tournamentId +
                ", userId=" + userId +
                ", status=" + status +
                ", registeredAt=" + registeredAt +
                '}';
    }
}