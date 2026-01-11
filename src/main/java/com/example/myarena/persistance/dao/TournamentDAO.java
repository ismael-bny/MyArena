package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Tournament;
import com.example.myarena.domain.TournamentStatus;

import java.util.List;

public interface TournamentDAO {
    // CRUD de base
    Tournament getTournamentById(Long id);
    List<Tournament> getAllTournaments();
    void saveTournament(Tournament tournament);
    void updateTournament(Tournament tournament);
    void deleteTournament(Long id);

    // Queries spécifiques pour les use cases
    List<Tournament> getTournamentsByOrganiserId(Long organiserId);
    List<Tournament> getTournamentsByStatus(TournamentStatus status);
    List<Tournament> getAvailableTournaments(); // Status = Open et pas complet
    List<Tournament> getTournamentsPendingApproval(); // Pour l'admin

    // Gestion du nombre de participants
    void incrementParticipantCount(Long tournamentId);
    void decrementParticipantCount(Long tournamentId);
}