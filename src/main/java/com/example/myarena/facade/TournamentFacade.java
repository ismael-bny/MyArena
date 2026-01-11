package com.example.myarena.facade;

import com.example.myarena.domain.*;
import com.example.myarena.services.TournamentManager;
import com.example.myarena.services.TournamentRegistrationManager;

import java.util.List;

/**
 * Facade qui simplifie l'accès aux services Tournament
 * Pattern Singleton
 */
public class TournamentFacade {
    private static TournamentFacade instance;
    private final TournamentManager tournamentManager;
    private final TournamentRegistrationManager registrationManager;

    private TournamentFacade() {
        this.tournamentManager = new TournamentManager();
        this.registrationManager = new TournamentRegistrationManager();
    }

    public static TournamentFacade getInstance() {
        if (instance == null) {
            instance = new TournamentFacade();
        }
        return instance;
    }

    // ===================================
    // TOURNAMENT OPERATIONS (Organisateur)
    // ===================================

    /**
     * Créer un nouveau tournoi (USE CASE: Create Tournament)
     */
    public Tournament createTournament(Tournament tournament) {
        return tournamentManager.createTournament(tournament);
    }

    /**
     * Modifier un tournoi existant (USE CASE: Modify Tournament)
     */
    public boolean updateTournament(Tournament tournament, Long organiserId) {
        return tournamentManager.updateTournament(tournament, organiserId);
    }

    /**
     * Supprimer un tournoi (USE CASE: Delete Tournament)
     */
    public boolean deleteTournament(Long tournamentId, Long organiserId) {
        return tournamentManager.deleteTournament(tournamentId, organiserId);
    }

    /**
     * Récupérer les tournois d'un organisateur
     */
    public List<Tournament> getOrganiserTournaments(Long organiserId) {
        return tournamentManager.getTournamentsByOrganiser(organiserId);
    }

    // ===================================
    // TOURNAMENT QUERIES (Client/Public)
    // ===================================

    /**
     * Récupérer un tournoi par ID
     */
    public Tournament getTournamentById(Long id) {
        return tournamentManager.getTournamentById(id);
    }

    /**
     * Récupérer tous les tournois disponibles (USE CASE: Register to Tournament - step 1)
     */
    public List<Tournament> getAvailableTournaments() {
        return tournamentManager.getAvailableTournaments();
    }

    /**
     * Récupérer tous les tournois
     */
    public List<Tournament> getAllTournaments() {
        return tournamentManager.getAllTournaments();
    }

    /**
     * Récupérer les tournois par statut
     */
    public List<Tournament> getTournamentsByStatus(TournamentStatus status) {
        return tournamentManager.getTournamentsByStatus(status);
    }

    // ===================================
    // ADMIN OPERATIONS
    // ===================================

    /**
     * Récupérer les tournois en attente d'approbation (Admin)
     */
    public List<Tournament> getPendingApprovalTournaments() {
        return tournamentManager.getTournamentsPendingApproval();
    }

    /**
     * Approuver un tournoi (Admin)
     */
    public boolean approveTournament(Long tournamentId) {
        return tournamentManager.approveTournament(tournamentId);
    }

    /**
     * Rejeter un tournoi (Admin)
     */
    public boolean rejectTournament(Long tournamentId, String reason) {
        return tournamentManager.rejectTournament(tournamentId, reason);
    }

    // ===================================
    // REGISTRATION OPERATIONS (Client)
    // ===================================

    /**
     * S'inscrire à un tournoi (USE CASE: Register to Tournament)
     */
    public boolean registerToTournament(Long tournamentId, Long userId) {
        TournamentRegistration registration = registrationManager.registerToTournament(tournamentId, userId);
        return registration != null;
    }

    /**
     * Annuler son inscription à un tournoi
     */
    public boolean cancelRegistration(Long tournamentId, Long userId) {
        return registrationManager.cancelRegistration(tournamentId, userId);
    }

    /**
     * Vérifier si un utilisateur est déjà inscrit
     */
    public boolean isUserRegistered(Long tournamentId, Long userId) {
        return registrationManager.isUserRegistered(tournamentId, userId);
    }

    /**
     * Récupérer les inscriptions d'un utilisateur
     */
    public List<TournamentRegistration> getUserRegistrations(Long userId) {
        return registrationManager.getRegistrationsByUser(userId);
    }

    // ===================================
    // REGISTRATION MANAGEMENT (Organisateur)
    // ===================================

    /**
     * Récupérer toutes les inscriptions pour un tournoi
     */
    public List<TournamentRegistration> getTournamentRegistrations(Long tournamentId) {
        return registrationManager.getRegistrationsByTournament(tournamentId);
    }

    /**
     * Récupérer les inscriptions en attente de validation
     */
    public List<TournamentRegistration> getPendingRegistrations(Long tournamentId) {
        return registrationManager.getPendingRegistrations(tournamentId);
    }

    /**
     * Valider une inscription (Organisateur)
     */
    public boolean validateRegistration(Long registrationId, Long organiserId) {
        return registrationManager.validateRegistration(registrationId, organiserId);
    }

    /**
     * Rejeter une inscription (Organisateur)
     */
    public boolean rejectRegistration(Long registrationId, Long organiserId, String reason) {
        return registrationManager.rejectRegistration(registrationId, organiserId, reason);
    }

    /**
     * Compter le nombre d'inscrits à un tournoi
     */
    public int getRegistrationCount(Long tournamentId) {
        return registrationManager.getRegistrationCount(tournamentId);
    }
}