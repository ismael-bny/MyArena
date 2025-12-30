package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Terrain;
import com.example.myarena.domain.TerrainType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TerrainDAOPostgres implements TerrainDAO {

    // Même config que UserDAOPostgres
    private static final String URL = "jdbc:postgresql://ep-gentle-term-ag1gorm7-pooler.c-2.eu-central-1.aws.neon.tech/myarena?sslmode=require&channel_binding=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_Zym8Fjrpg4iz";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public Terrain getTerrainByID(Long id) {
        String sql = "SELECT * FROM terrains WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTerrain(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du terrain avec id=" + id, e);
        }
    }

    @Override
    public void saveTerrain(Terrain t) {
        String sql = """
                INSERT INTO terrains (name, type, description, location, price_per_hour, capacity, available, owner_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getName());
            stmt.setString(2, t.getType() != null ? t.getType().name() : null);
            stmt.setString(3, t.getDescription());
            stmt.setString(4, t.getLocation());
            stmt.setDouble(5, t.getPricePerHour());
            stmt.setInt(6, t.getCapacity());
            stmt.setBoolean(7, t.isAvailable());
            if (t.getOwnerId() != null) {
                stmt.setLong(8, t.getOwnerId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                long generatedId = rs.getLong("id");
                t.setId(generatedId); // on met à jour l'objet
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du terrain", e);
        }
    }

    @Override
    public void updateTerrain(Terrain t) {
        if (t.getId() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un terrain sans id");
        }

        String sql = """
                UPDATE terrains
                SET name = ?, type = ?, description = ?, location = ?, price_per_hour = ?, capacity = ?, available = ?, owner_id = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getName());
            stmt.setString(2, t.getType() != null ? t.getType().name() : null);
            stmt.setString(3, t.getDescription());
            stmt.setString(4, t.getLocation());
            stmt.setDouble(5, t.getPricePerHour());
            stmt.setInt(6, t.getCapacity());
            stmt.setBoolean(7, t.isAvailable());
            if (t.getOwnerId() != null) {
                stmt.setLong(8, t.getOwnerId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }
            stmt.setLong(9, t.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du terrain id=" + t.getId(), e);
        }
    }

    @Override
    public void deleteTerrain(Long id) {
        String sql = "DELETE FROM terrains WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du terrain id=" + id, e);
        }
    }

    private Terrain mapResultSetToTerrain(ResultSet rs) throws SQLException {
        Terrain terrain = new Terrain();
        terrain.setId(rs.getLong("id"));
        terrain.setName(rs.getString("name"));

        String typeStr = rs.getString("type");
        if (typeStr != null) {
            terrain.setType(TerrainType.valueOf(typeStr));
        }

        terrain.setDescription(rs.getString("description"));
        terrain.setLocation(rs.getString("location"));
        terrain.setPricePerHour(rs.getDouble("price_per_hour"));
        terrain.setCapacity(rs.getInt("capacity"));
        terrain.setAvailable(rs.getBoolean("available"));

        long ownerId = rs.getLong("owner_id");
        if (!rs.wasNull()) {
            terrain.setOwnerId(ownerId);
        }

        return terrain;
    }

    @Override
    public List<Terrain> getAllTerrains() {
        String sql = "SELECT * FROM terrains ORDER BY id";
        List<Terrain> terrains = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                terrains.add(mapResultSetToTerrain(rs));
            }
            return terrains;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les terrains", e);
        }
    }
}
