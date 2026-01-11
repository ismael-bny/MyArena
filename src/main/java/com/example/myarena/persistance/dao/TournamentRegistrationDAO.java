package com.example.myarena.persistance.dao;

import com.example.myarena.domain.TournamentRegistration;
import com.example.myarena.domain.RegistrationStatus;

import java.util.List;

public interface TournamentRegistrationDAO {
    // CRUD de base
    TournamentRegistration getRegistrationById(Long id);
    List<TournamentRegistration> getAllRegistrations();
    void saveRegistration(TournamentRegistration registration);
    void updateRegistration(TournamentRegistration registration);
    void deleteRegistration(Long id);

    // Queries spécifiques
    List<TournamentRegistration> getRegistrationsByTournamentId(Long tournamentId);
    List<TournamentRegistration> getRegistrationsByUserId(Long userId);
    TournamentRegistration getRegistrationByTournamentAndUser(Long tournamentId, Long userId);
    List<TournamentRegistration> getRegistrationsByStatus(Long tournamentId, RegistrationStatus status);

    // Vérifications
    boolean isUserRegistered(Long tournamentId, Long userId);
    int countRegistrationsByTournament(Long tournamentId);
}