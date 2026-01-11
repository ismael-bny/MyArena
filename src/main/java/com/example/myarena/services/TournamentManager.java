package com.example.myarena.services;

import com.example.myarena.domain.*;
import com.example.myarena.persistance.dao.*;
import com.example.myarena.persistance.factory.PostgresFactory;

import java.util.Date;
import java.util.List;

/**
 * Gère la logique métier pour les tournois
 * Implémente les use cases: Create, Modify, Delete Tournament
 */
public class TournamentManager {
    private final TournamentDAO tournamentDAO;
    private final TournamentRegistrationDAO registrationDAO;
    private final NotificationManager notificationManager;

    public TournamentManager() {
        PostgresFactory factory = new PostgresFactory();
        this.tournamentDAO = factory.createTournamentDAO();
        this.registrationDAO = factory.createTournamentRegistrationDAO();
        this.notificationManager = new NotificationManager();
    }

    /**
     * USE CASE: Create Tournament
     * L'organisateur crée un tournoi qui sera soumis à l'admin pour validation
     */
    public Tournament createTournament(Tournament tournament) {
        if (tournament == null || tournament.getOrganiserId() == null) {
            System.err.println("ERROR: Cannot create tournament. Invalid data.");
            return null;
        }

        // Validation des dates
        if (tournament.getStartDate() == null || tournament.getEndDate() == null) {
            System.err.println("ERROR: Start date and end date are required.");
            return null;
        }

        if (tournament.getStartDate().after(tournament.getEndDate())) {
            System.err.println("ERROR: Start date must be before end date.");
            return null;
        }

        // Validation des participants
        if (tournament.getMaxParticipants() == null || tournament.getMaxParticipants() <= 0) {
            System.err.println("ERROR: Max participants must be greater than 0.");
            return null;
        }

        // Statut initial: en attente d'approbation
        tournament.setStatus(TournamentStatus.AwaitingApproval);
        tournament.setCreatedAt(new Date());
        tournament.setCurrentParticipants(0);

        // Sauvegarder en DB
        tournamentDAO.saveTournament(tournament);

        // Notifier l'admin (ID admin typiquement = 1 ou récupérer via UserDAO)
        try {
            notificationManager.createNotification(
                    1L, // Admin user ID (à adapter selon votre système)
                    NotificationType.TOURNAMENT_UPDATE,
                    "New Tournament Awaiting Approval",
                    "Tournament '" + tournament.getName() + "' created by organizer #" + tournament.getOrganiserId() + " is awaiting your approval."
            );
        } catch (Exception e) {
            System.err.println("WARN: Failed to notify admin: " + e.getMessage());
        }

        return tournament;
    }

    /**
     * USE CASE: Modify Tournament
     * L'organisateur modifie son tournoi
     */
    public boolean updateTournament(Tournament tournament, Long organiserId) {
        if (tournament == null || tournament.getId() == null) {
            return false;
        }

        // Vérifier que le tournoi appartient à l'organisateur
        Tournament existing = tournamentDAO.getTournamentById(tournament.getId());
        if (existing == null) {
            System.err.println("ERROR: Tournament not found.");
            return false;
        }

        if (!existing.getOrganiserId().equals(organiserId)) {
            System.err.println("ERROR: Not authorized. Tournament does not belong to this organizer.");
            return false;
        }

        // Validation: capacité >= participants actuels
        if (tournament.getMaxParticipants() < existing.getCurrentParticipants()) {
            System.err.println("ERROR: Cannot reduce max participants below current registrations.");
            return false;
        }

        // Validation des dates
        if (tournament.getStartDate().after(tournament.getEndDate())) {
            System.err.println("ERROR: Start date must be before end date.");
            return false;
        }

        // Update en DB
        tournamentDAO.updateTournament(tournament);

        // Notifier tous les participants inscrits
        try {
            List<TournamentRegistration> registrations = registrationDAO.getRegistrationsByTournamentId(tournament.getId());
            for (TournamentRegistration reg : registrations) {
                notificationManager.createNotification(
                        reg.getUserId(),
                        NotificationType.TOURNAMENT_UPDATE,
                        "Tournament Updated",
                        "The tournament '" + tournament.getName() + "' has been updated. Please check the new details."
                );
            }
        } catch (Exception e) {
            System.err.println("WARN: Failed to notify participants: " + e.getMessage());
        }

        return true;
    }

    /**
     * USE CASE: Delete Tournament
     * L'organisateur supprime/annule son tournoi
     */
    public boolean deleteTournament(Long tournamentId, Long organiserId) {
        Tournament tournament = tournamentDAO.getTournamentById(tournamentId);

        if (tournament == null) {
            System.err.println("ERROR: Tournament not found.");
            return false;
        }

        // Vérifier que le tournoi appartient à l'organisateur
        if (!tournament.getOrganiserId().equals(organiserId)) {
            System.err.println("ERROR: Not authorized. Tournament does not belong to this organizer.");
            return false;
        }

        // Récupérer tous les participants avant suppression
        List<TournamentRegistration> registrations = registrationDAO.getRegistrationsByTournamentId(tournamentId);

        // Supprimer le tournoi (CASCADE supprimera les registrations)
        tournamentDAO.deleteTournament(tournamentId);

        // Notifier tous les participants de l'annulation
        try {
            for (TournamentRegistration reg : registrations) {
                notificationManager.createNotification(
                        reg.getUserId(),
                        NotificationType.TOURNAMENT_UPDATE,
                        "Tournament Cancelled",
                        "The tournament '" + tournament.getName() + "' has been cancelled by the organizer."
                );
            }
        } catch (Exception e) {
            System.err.println("WARN: Failed to notify participants: " + e.getMessage());
        }

        return true;
    }

    /**
     * Approuver un tournoi (Admin uniquement)
     */
    public boolean approveTournament(Long tournamentId) {
        Tournament tournament = tournamentDAO.getTournamentById(tournamentId);
        if (tournament == null) {
            return false;
        }

        tournament.setStatus(TournamentStatus.Open);
        tournamentDAO.updateTournament(tournament);

        // Notifier l'organisateur
        try {
            notificationManager.createNotification(
                    tournament.getOrganiserId(),
                    NotificationType.TOURNAMENT_UPDATE,
                    "Tournament Approved",
                    "Your tournament '" + tournament.getName() + "' has been approved and is now open for registrations!"
            );
        } catch (Exception e) {
            System.err.println("WARN: Failed to notify organizer: " + e.getMessage());
        }

        return true;
    }

    /**
     * Rejeter un tournoi (Admin uniquement)
     */
    public boolean rejectTournament(Long tournamentId, String reason) {
        Tournament tournament = tournamentDAO.getTournamentById(tournamentId);
        if (tournament == null) {
            return false;
        }

        tournament.setStatus(TournamentStatus.Rejected);
        tournamentDAO.updateTournament(tournament);

        // Notifier l'organisateur
        try {
            notificationManager.createNotification(
                    tournament.getOrganiserId(),
                    NotificationType.TOURNAMENT_UPDATE,
                    "Tournament Rejected",
                    "Your tournament '" + tournament.getName() + "' has been rejected. Reason: " + reason
            );
        } catch (Exception e) {
            System.err.println("WARN: Failed to notify organizer: " + e.getMessage());
        }

        return true;
    }

    // === Méthodes de récupération ===

    public Tournament getTournamentById(Long id) {
        return tournamentDAO.getTournamentById(id);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentDAO.getAllTournaments();
    }

    public List<Tournament> getTournamentsByOrganiser(Long organiserId) {
        return tournamentDAO.getTournamentsByOrganiserId(organiserId);
    }

    public List<Tournament> getAvailableTournaments() {
        return tournamentDAO.getAvailableTournaments();
    }

    public List<Tournament> getTournamentsPendingApproval() {
        return tournamentDAO.getTournamentsPendingApproval();
    }

    public List<Tournament> getTournamentsByStatus(TournamentStatus status) {
        return tournamentDAO.getTournamentsByStatus(status);
    }
}