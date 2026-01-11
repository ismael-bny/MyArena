package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Tournament;
import com.example.myarena.domain.TournamentStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentDAOPostgres implements TournamentDAO {

    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public Tournament getTournamentById(Long id) {
        String sql = "SELECT * FROM tournaments WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTournament(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Tournament> getAllTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments ORDER BY start_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tournaments.add(mapResultSetToTournament(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournaments;
    }

    @Override
    public void saveTournament(Tournament tournament) {
        String sql = "INSERT INTO tournaments (organiser_id, name, sport, description, rules, " +
                "start_date, end_date, location, max_participants, registration_fee, prize, " +
                "terrain_id, status, created_at, current_participants) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (tournament.getOrganiserId() == null) {
                System.err.println("ERROR: Cannot save tournament. Organiser ID is NULL.");
                return;
            }

            stmt.setLong(1, tournament.getOrganiserId());
            stmt.setString(2, tournament.getName());
            stmt.setString(3, tournament.getSport());
            stmt.setString(4, tournament.getDescription());
            stmt.setString(5, tournament.getRules());
            stmt.setTimestamp(6, new Timestamp(tournament.getStartDate().getTime()));
            stmt.setTimestamp(7, new Timestamp(tournament.getEndDate().getTime()));
            stmt.setString(8, tournament.getLocation());
            stmt.setInt(9, tournament.getMaxParticipants());
            stmt.setBigDecimal(10, tournament.getRegistrationFee());
            stmt.setString(11, tournament.getPrize());

            if (tournament.getTerrainId() != null) {
                stmt.setLong(12, tournament.getTerrainId());
            } else {
                stmt.setNull(12, Types.BIGINT);
            }

            stmt.setString(13, tournament.getStatus().name());
            stmt.setTimestamp(14, new Timestamp(tournament.getCreatedAt().getTime()));
            stmt.setInt(15, tournament.getCurrentParticipants() != null ? tournament.getCurrentParticipants() : 0);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tournament.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTournament(Tournament tournament) {
        String sql = "UPDATE tournaments SET name=?, sport=?, description=?, rules=?, " +
                "start_date=?, end_date=?, location=?, max_participants=?, " +
                "registration_fee=?, prize=?, terrain_id=?, status=? WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tournament.getName());
            stmt.setString(2, tournament.getSport());
            stmt.setString(3, tournament.getDescription());
            stmt.setString(4, tournament.getRules());
            stmt.setTimestamp(5, new Timestamp(tournament.getStartDate().getTime()));
            stmt.setTimestamp(6, new Timestamp(tournament.getEndDate().getTime()));
            stmt.setString(7, tournament.getLocation());
            stmt.setInt(8, tournament.getMaxParticipants());
            stmt.setBigDecimal(9, tournament.getRegistrationFee());
            stmt.setString(10, tournament.getPrize());

            if (tournament.getTerrainId() != null) {
                stmt.setLong(11, tournament.getTerrainId());
            } else {
                stmt.setNull(11, Types.BIGINT);
            }

            stmt.setString(12, tournament.getStatus().name());
            stmt.setLong(13, tournament.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTournament(Long id) {
        String sql = "DELETE FROM tournaments WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Tournament> getTournamentsByOrganiserId(Long organiserId) {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments WHERE organiser_id = ? ORDER BY created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, organiserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tournaments.add(mapResultSetToTournament(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournaments;
    }

    @Override
    public List<Tournament> getTournamentsByStatus(TournamentStatus status) {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments WHERE status = ? ORDER BY start_date";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tournaments.add(mapResultSetToTournament(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournaments;
    }

    @Override
    public List<Tournament> getAvailableTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments WHERE status = ? AND current_participants < max_participants ORDER BY start_date";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, TournamentStatus.Open.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tournaments.add(mapResultSetToTournament(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournaments;
    }

    @Override
    public List<Tournament> getTournamentsPendingApproval() {
        return getTournamentsByStatus(TournamentStatus.AwaitingApproval);
    }

    @Override
    public void incrementParticipantCount(Long tournamentId) {
        String sql = "UPDATE tournaments SET current_participants = current_participants + 1 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tournamentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decrementParticipantCount(Long tournamentId) {
        String sql = "UPDATE tournaments SET current_participants = GREATEST(current_participants - 1, 0) WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tournamentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Tournament mapResultSetToTournament(ResultSet rs) throws SQLException {
        Tournament tournament = new Tournament();
        tournament.setId(rs.getLong("id"));
        tournament.setOrganiserId(rs.getLong("organiser_id"));
        tournament.setName(rs.getString("name"));
        tournament.setSport(rs.getString("sport"));
        tournament.setDescription(rs.getString("description"));
        tournament.setRules(rs.getString("rules"));
        tournament.setStartDate(rs.getTimestamp("start_date"));
        tournament.setEndDate(rs.getTimestamp("end_date"));
        tournament.setLocation(rs.getString("location"));
        tournament.setMaxParticipants(rs.getInt("max_participants"));
        tournament.setRegistrationFee(rs.getBigDecimal("registration_fee"));
        tournament.setPrize(rs.getString("prize"));

        Long terrainId = rs.getLong("terrain_id");
        if (!rs.wasNull()) {
            tournament.setTerrainId(terrainId);
        }

        tournament.setStatus(TournamentStatus.valueOf(rs.getString("status")));
        tournament.setCreatedAt(rs.getTimestamp("created_at"));
        tournament.setCurrentParticipants(rs.getInt("current_participants"));

        return tournament;
    }
}