package com.example.myarena.ui;

import com.example.myarena.domain.Tournament;
import com.example.myarena.domain.TournamentRegistration;
import com.example.myarena.facade.TournamentFacade;
import com.example.myarena.facade.UserSession;

import java.util.List;

/**
 * Controller pour ManageTournamentRegistrationsFrame
 * Permet à l'organisateur de valider/rejeter les inscriptions
 */
public class ManageTournamentRegistrationsController {
    private final TournamentFacade tournamentFacade;
    private ManageTournamentRegistrationsFrame view;

    public ManageTournamentRegistrationsController(ManageTournamentRegistrationsFrame view) {
        this.view = view;
        this.tournamentFacade = TournamentFacade.getInstance();
    }

    /**
     * Récupérer les tournois de l'organisateur connecté
     */
    public List<Tournament> getMyTournaments() {
        Long organiserId = UserSession.getInstance().getUser().getId();
        return tournamentFacade.getOrganiserTournaments(organiserId);
    }

    /**
     * Récupérer toutes les inscriptions d'un tournoi
     */
    public List<TournamentRegistration> getTournamentRegistrations(Long tournamentId) {
        return tournamentFacade.getTournamentRegistrations(tournamentId);
    }

    /**
     * Récupérer les inscriptions en attente de validation
     */
    public List<TournamentRegistration> getPendingRegistrations(Long tournamentId) {
        return tournamentFacade.getPendingRegistrations(tournamentId);
    }

    /**
     * Valider une inscription
     */
    public boolean validateRegistration(Long registrationId) {
        Long organiserId = UserSession.getInstance().getUser().getId();
        return tournamentFacade.validateRegistration(registrationId, organiserId);
    }

    /**
     * Rejeter une inscription
     */
    public boolean rejectRegistration(Long registrationId, String reason) {
        Long organiserId = UserSession.getInstance().getUser().getId();
        return tournamentFacade.rejectRegistration(registrationId, organiserId, reason);
    }

    /**
     * Récupérer un tournoi par ID
     */
    public Tournament getTournamentById(Long id) {
        return tournamentFacade.getTournamentById(id);
    }

    /**
     * Récupérer le nom d'un utilisateur par son ID
     */
    public String getUserName(Long userId) {
        try {
            com.example.myarena.persistance.factory.PostgresFactory factory = new com.example.myarena.persistance.factory.PostgresFactory();
            com.example.myarena.persistance.dao.UserDAO userDAO = factory.createUserDAO();
            com.example.myarena.domain.User user = userDAO.getUserByID(userId);
            return user != null ? user.getName() : "User #" + userId;
        } catch (Exception e) {
            return "User #" + userId;
        }
    }
}