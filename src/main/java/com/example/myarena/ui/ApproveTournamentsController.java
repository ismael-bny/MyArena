package com.example.myarena.ui;

import com.example.myarena.domain.Tournament;
import com.example.myarena.facade.TournamentFacade;

import java.util.List;

/**
 * Controller pour ApproveTournamentsFrame
 * Permet à l'admin d'approuver ou rejeter les tournois
 */
public class ApproveTournamentsController {
    private final TournamentFacade tournamentFacade;
    private ApproveTournamentsFrame view;

    public ApproveTournamentsController(ApproveTournamentsFrame view) {
        this.view = view;
        this.tournamentFacade = TournamentFacade.getInstance();
    }

    /**
     * Récupérer tous les tournois en attente d'approbation
     */
    public List<Tournament> getPendingTournaments() {
        return tournamentFacade.getPendingApprovalTournaments();
    }

    /**
     * Approuver un tournoi
     */
    public boolean approveTournament(Long tournamentId) {
        return tournamentFacade.approveTournament(tournamentId);
    }

    /**
     * Rejeter un tournoi
     */
    public boolean rejectTournament(Long tournamentId, String reason) {
        return tournamentFacade.rejectTournament(tournamentId, reason);
    }

    /**
     * Récupérer un tournoi par ID
     */
    public Tournament getTournamentById(Long id) {
        return tournamentFacade.getTournamentById(id);
    }
}