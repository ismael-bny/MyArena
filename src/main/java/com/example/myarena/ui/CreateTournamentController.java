package com.example.myarena.ui;

import com.example.myarena.domain.Tournament;
import com.example.myarena.facade.TournamentFacade;
import com.example.myarena.facade.UserSession;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Controller pour CreateTournamentFrame
 * Gère la logique de création de tournoi
 */
public class CreateTournamentController {
    private final TournamentFacade tournamentFacade;
    private CreateTournamentFrame view;

    public CreateTournamentController(CreateTournamentFrame view) {
        this.view = view;
        this.tournamentFacade = TournamentFacade.getInstance();
    }

    /**
     * Créer un nouveau tournoi
     * Validation côté controller avant envoi
     */
    public boolean createTournament(String name, String sport, String description, String rules,
                                    Date startDate, Date endDate, String location,
                                    Integer maxParticipants, BigDecimal registrationFee,
                                    String prize, Long terrainId) {

        // Validation basique
        if (name == null || name.trim().isEmpty()) {
            view.showError("Tournament name is required");
            return false;
        }

        if (sport == null || sport.trim().isEmpty()) {
            view.showError("Sport category is required");
            return false;
        }

        if (startDate == null || endDate == null) {
            view.showError("Start and end dates are required");
            return false;
        }

        if (startDate.after(endDate)) {
            view.showError("Start date must be before end date");
            return false;
        }

        if (maxParticipants == null || maxParticipants <= 0) {
            view.showError("Max participants must be greater than 0");
            return false;
        }

        if (location == null || location.trim().isEmpty()) {
            view.showError("Location is required");
            return false;
        }

        // Créer l'objet Tournament
        Long organiserId = UserSession.getInstance().getUser().getId();
        Tournament tournament = new Tournament(
                organiserId, name, sport, description, rules,
                startDate, endDate, location, maxParticipants,
                registrationFee, prize
        );

        // Ajouter le terrain si spécifié
        if (terrainId != null && terrainId > 0) {
            tournament.setTerrainId(terrainId);
        }

        // Appeler la facade
        Tournament created = tournamentFacade.createTournament(tournament);

        if (created != null) {
            view.showSuccess("Tournament created successfully!\nStatus: Awaiting Admin Approval\nID: " + created.getId());
            return true;
        } else {
            view.showError("Failed to create tournament. Please try again.");
            return false;
        }
    }

    /**
     * Valider les dates
     */
    public boolean validateDates(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return startDate.before(endDate);
    }
}