package com.example.myarena.services;

import com.example.myarena.domain.*;
import com.example.myarena.persistance.dao.*;
import com.example.myarena.persistance.factory.PostgresFactory;

import java.util.List;

/**
 * Gère la logique métier pour les inscriptions aux tournois
 * Implémente le use case: Register to Tournament
 */
public class TournamentRegistrationManager {
    private final TournamentRegistrationDAO registrationDAO;
    private final TournamentDAO tournamentDAO;
    private final NotificationManager notificationManager;

    public TournamentRegistrationManager() {
        PostgresFactory factory = new PostgresFactory();
        this.registrationDAO = factory.createTournamentRegistrationDAO();
        this.tournamentDAO = factory.createTournamentDAO();
        this.notificationManager = new NotificationManager();
    }

    /**
     * USE CASE: Register to Tournament
     * Un client s'inscrit à un tournoi disponible
     */
    public TournamentRegistration registerToTournament(Long tournamentId, Long userId) {
        // Vérifier que le tournoi existe
        Tournament tournament = tournamentDAO.getTournamentById(tournamentId);
        if (tournament == null) {
            System.err.println("ERROR: Tournament not found.");
            return null;
        }

        // Vérifier que les inscriptions sont ouvertes
        if (tournament.getStatus() != TournamentStatus.Open) {
            System.err.println("ERROR: Tournament is not open for registrations. Status: " + tournament.getStatus());
            return null;
        }

        // Vérifier qu'il reste des places
        if (tournament.isFull()) {
            System.err.println("ERROR: Tournament is full. No slots available.");
            return null;
        }

        // Vérifier que l'utilisateur n'est pas déjà inscrit
        if (registrationDAO.isUserRegistered(tournamentId, userId)) {
            System.err.println("ERROR: User is already registered to this tournament.");
            return null;
        }

        // Créer l'inscription avec statut "PendingValidation"
        TournamentRegistration registration = new TournamentRegistration(tournamentId, userId);
        registration.setStatus(RegistrationStatus.PendingValidation);

        // Sauvegarder en DB
        registrationDAO.saveRegistration(registration);

        // Incrémenter le compteur de participants
        tournamentDAO.incrementParticipantCount(tournamentId);

        // Notifier l'organisateur
        try {
            notificationManager.createNotification(
                    tournament.getOrganiserId(),
                    NotificationType.TOURNAMENT_UPDATE,
                    "New Tournament Registration",
                    "A new participant (User #" + userId + ") has registered for your tournament '" + tournament.getName() + "'."
            );
        } catch (Exception e) {
            System.err.println("WARN: Failed to notify organizer: " + e.getMessage());
        }

        // Notifier le client
        try {
            notificationManager.createNotification(
                    userId,
                    NotificationType.TOURNAMENT_UPDATE,
                    "Registration Pending",
                    "Your registration for tournament '" + tournament.getName() + "' is pending organizer validation."
            );
        } catch (Exception e) {
            System.err.println("WARN: Failed to notify user: " + e.getMessage());
        }

        return registration;
    }

    /**
     * Valider une inscription (Organisateur uniquement)
     */
    public boolean validateRegistration(Long registrationId, Long organiserId) {
        TournamentRegistration registration = registrationDAO.getRegistrationById(registrationId);
        if (registration == null) {
            return false;
        }

        // Vérifier que le tournoi appartient à l'organisateur
        Tournament tournament = tournamentDAO.getTournamentById(registration.getTournamentId());
        if (tournament == null || !tournament.getOrganiserId().equals(organiserId)) {
            System.err.println("ERROR: Not authorized to validate this registration.");
            return false;
        }

        registration.setStatus(RegistrationStatus.Validated);
        registrationDAO.updateRegistration(registration);

        // Notifier le participant
        try {
            notificationManager.createNotification(
                    registration.getUserId(),
                    NotificationType.TOURNAMENT_UPDATE,
                    "Registration Validated",
                    "Your registration for tournament '" + tournament.getName() + "' has been validated!"
            );
        } catch (Exception e) {
            System.err.println("WARN: Failed to notify participant: " + e.getMessage());
        }

        return true;
    }

    /**
     * Rejeter une inscription (Organisateur uniquement)
     */
    public boolean rejectRegistration(Long registrationId, Long organiserId, String reason) {
        TournamentRegistration registration = registrationDAO.getRegistrationById(registrationId);
        if (registration == null) {
            return false;
        }

        // Vérifier que le tournoi appartient à l'organisateur
        Tournament tournament = tournamentDAO.getTournamentById(registration.getTournamentId());
        if (tournament == null || !tournament.getOrganiserId().equals(organiserId)) {
            System.err.println("ERROR: Not authorized to reject this registration.");
            return false;
        }

        registration.setStatus(RegistrationStatus.Rejected);
        registration.setNotes(reason);
        registrationDAO.updateRegistration(registration);

        // Décrémenter le compteur de participants
        tournamentDAO.decrementParticipantCount(registration.getTournamentId());

        // Notifier le participant
        try {
            notificationManager.createNotification(
                    registration.getUserId(),
                    NotificationType.TOURNAMENT_UPDATE,
                    "Registration Rejected",
                    "Your registration for tournament '" + tournament.getName() + "' was rejected. Reason: " + reason
            );
        } catch (Exception e) {
            System.err.println("WARN: Failed to notify participant: " + e.getMessage());
        }

        return true;
    }

    /**
     * Annuler sa propre inscription (Client)
     */
    public boolean cancelRegistration(Long tournamentId, Long userId) {
        TournamentRegistration registration = registrationDAO.getRegistrationByTournamentAndUser(tournamentId, userId);

        if (registration == null) {
            System.err.println("ERROR: Registration not found.");
            return false;
        }

        registration.setStatus(RegistrationStatus.Cancelled);
        registrationDAO.updateRegistration(registration);

        // Décrémenter le compteur de participants
        tournamentDAO.decrementParticipantCount(tournamentId);

        // Notifier l'organisateur
        try {
            Tournament tournament = tournamentDAO.getTournamentById(tournamentId);
            if (tournament != null) {
                notificationManager.createNotification(
                        tournament.getOrganiserId(),
                        NotificationType.TOURNAMENT_UPDATE,
                        "Registration Cancelled",
                        "User #" + userId + " cancelled their registration for tournament '" + tournament.getName() + "'."
                );
            }
        } catch (Exception e) {
            System.err.println("WARN: Failed to notify organizer: " + e.getMessage());
        }

        return true;
    }

    // === Méthodes de récupération ===

    public TournamentRegistration getRegistrationById(Long id) {
        return registrationDAO.getRegistrationById(id);
    }

    public List<TournamentRegistration> getRegistrationsByTournament(Long tournamentId) {
        return registrationDAO.getRegistrationsByTournamentId(tournamentId);
    }

    public List<TournamentRegistration> getRegistrationsByUser(Long userId) {
        return registrationDAO.getRegistrationsByUserId(userId);
    }

    public List<TournamentRegistration> getPendingRegistrations(Long tournamentId) {
        return registrationDAO.getRegistrationsByStatus(tournamentId, RegistrationStatus.PendingValidation);
    }

    public boolean isUserRegistered(Long tournamentId, Long userId) {
        return registrationDAO.isUserRegistered(tournamentId, userId);
    }

    public int getRegistrationCount(Long tournamentId) {
        return registrationDAO.countRegistrationsByTournament(tournamentId);
    }
}