package com.example.myarena.persistance.dao;

import com.example.myarena.domain.TournamentRegistration;
import com.example.myarena.domain.RegistrationStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentRegistrationDAOPostgres implements TournamentRegistrationDAO {

    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public TournamentRegistration getRegistrationById(Long id) {
        String sql = "SELECT * FROM tournament_registrations WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRegistration(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TournamentRegistration> getAllRegistrations() {
        List<TournamentRegistration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM tournament_registrations ORDER BY registered_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                registrations.add(mapResultSetToRegistration(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registrations;
    }

    @Override
    public void saveRegistration(TournamentRegistration registration) {
        String sql = "INSERT INTO tournament_registrations (tournament_id, user_id, status, registered_at, notes) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (registration.getTournamentId() == null || registration.getUserId() == null) {
                System.err.println("ERROR: Cannot save registration. Tournament ID or User ID is NULL.");
                return;
            }

            stmt.setLong(1, registration.getTournamentId());
            stmt.setLong(2, registration.getUserId());
            stmt.setString(3, registration.getStatus().name());
            stmt.setTimestamp(4, new Timestamp(registration.getRegisteredAt().getTime()));
            stmt.setString(5, registration.getNotes());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        registration.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateRegistration(TournamentRegistration registration) {
        String sql = "UPDATE tournament_registrations SET status=?, notes=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, registration.getStatus().name());
            stmt.setString(2, registration.getNotes());
            stmt.setLong(3, registration.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteRegistration(Long id) {
        String sql = "DELETE FROM tournament_registrations WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TournamentRegistration> getRegistrationsByTournamentId(Long tournamentId) {
        List<TournamentRegistration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM tournament_registrations WHERE tournament_id = ? ORDER BY registered_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tournamentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                registrations.add(mapResultSetToRegistration(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registrations;
    }

    @Override
    public List<TournamentRegistration> getRegistrationsByUserId(Long userId) {
        List<TournamentRegistration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM tournament_registrations WHERE user_id = ? ORDER BY registered_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                registrations.add(mapResultSetToRegistration(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registrations;
    }

    @Override
    public TournamentRegistration getRegistrationByTournamentAndUser(Long tournamentId, Long userId) {
        String sql = "SELECT * FROM tournament_registrations WHERE tournament_id = ? AND user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tournamentId);
            stmt.setLong(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRegistration(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TournamentRegistration> getRegistrationsByStatus(Long tournamentId, RegistrationStatus status) {
        List<TournamentRegistration> registrations = new ArrayList<>();
        String sql = "SELECT * FROM tournament_registrations WHERE tournament_id = ? AND status = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tournamentId);
            stmt.setString(2, status.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                registrations.add(mapResultSetToRegistration(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registrations;
    }

    @Override
    public boolean isUserRegistered(Long tournamentId, Long userId) {
        String sql = "SELECT COUNT(*) FROM tournament_registrations WHERE tournament_id = ? AND user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tournamentId);
            stmt.setLong(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int countRegistrationsByTournament(Long tournamentId) {
        String sql = "SELECT COUNT(*) FROM tournament_registrations WHERE tournament_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tournamentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private TournamentRegistration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        TournamentRegistration registration = new TournamentRegistration();
        registration.setId(rs.getLong("id"));
        registration.setTournamentId(rs.getLong("tournament_id"));
        registration.setUserId(rs.getLong("user_id"));
        registration.setStatus(RegistrationStatus.valueOf(rs.getString("status")));
        registration.setRegisteredAt(rs.getTimestamp("registered_at"));
        registration.setNotes(rs.getString("notes"));
        return registration;
    }
}