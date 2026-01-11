package com.example.myarena.ui;

import com.example.myarena.domain.Tournament;
import com.example.myarena.domain.TournamentRegistration;
import com.example.myarena.facade.TournamentFacade;
import com.example.myarena.facade.UserSession;

import java.util.List;

/**
 * Controller pour MyTournamentRegistrationsFrame
 * Gère l'affichage des inscriptions de l'utilisateur
 */
public class MyTournamentRegistrationsController {
    private final TournamentFacade tournamentFacade;
    private MyTournamentRegistrationsFrame view;

    public MyTournamentRegistrationsController(MyTournamentRegistrationsFrame view) {
        this.view = view;
        this.tournamentFacade = TournamentFacade.getInstance();
    }

    /**
     * Récupérer toutes les inscriptions de l'utilisateur connecté
     */
    public List<TournamentRegistration> getMyRegistrations() {
        Long userId = UserSession.getInstance().getUser().getId();
        return tournamentFacade.getUserRegistrations(userId);
    }

    /**
     * Récupérer un tournoi par ID
     */
    public Tournament getTournamentById(Long tournamentId) {
        return tournamentFacade.getTournamentById(tournamentId);
    }

    /**
     * Annuler une inscription
     */
    public boolean cancelRegistration(Long tournamentId) {
        Long userId = UserSession.getInstance().getUser().getId();
        return tournamentFacade.cancelRegistration(tournamentId, userId);
    }
}