package com.example.myarena.ui;

import com.example.myarena.domain.Tournament;
import com.example.myarena.domain.TournamentStatus;
import com.example.myarena.facade.TournamentFacade;
import com.example.myarena.facade.UserSession;

import java.util.List;

/**
 * Controller pour TournamentListFrame
 * Gère la logique de récupération et filtrage des tournois
 */
public class TournamentListController {
    private final TournamentFacade tournamentFacade;
    private TournamentListFrame view;

    public TournamentListController(TournamentListFrame view) {
        this.view = view;
        this.tournamentFacade = TournamentFacade.getInstance();
    }

    /**
     * Récupérer tous les tournois disponibles pour inscription
     */
    public List<Tournament> getAvailableTournaments() {
        return tournamentFacade.getAvailableTournaments();
    }

    /**
     * Récupérer tous les tournois (pour admin/organisateur)
     */
    public List<Tournament> getAllTournaments() {
        return tournamentFacade.getAllTournaments();
    }

    /**
     * Récupérer les tournois par statut
     */
    public List<Tournament> getTournamentsByStatus(TournamentStatus status) {
        return tournamentFacade.getTournamentsByStatus(status);
    }

    /**
     * Vérifier si l'utilisateur est déjà inscrit à un tournoi
     */
    public boolean isUserRegistered(Long tournamentId) {
        Long userId = UserSession.getInstance().getUser().getId();
        return tournamentFacade.isUserRegistered(tournamentId, userId);
    }

    /**
     * S'inscrire à un tournoi
     */
    public boolean registerToTournament(Long tournamentId) {
        Long userId = UserSession.getInstance().getUser().getId();
        return tournamentFacade.registerToTournament(tournamentId, userId);
    }

    /**
     * Récupérer un tournoi par ID
     */
    public Tournament getTournamentById(Long id) {
        return tournamentFacade.getTournamentById(id);
    }
}