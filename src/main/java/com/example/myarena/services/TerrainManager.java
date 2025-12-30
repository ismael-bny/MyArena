package com.example.myarena.services;

import com.example.myarena.domain.Terrain;
import com.example.myarena.persistance.dao.TerrainDAO;
import com.example.myarena.persistance.factory.AbstractFactory;
import com.example.myarena.persistance.factory.PostgresFactory;

import java.util.List;

public class TerrainManager {

    private final TerrainDAO terrainDAO;

    public TerrainManager() {
        AbstractFactory factory = new PostgresFactory();
        this.terrainDAO = factory.createTerrainDAO();
    }

    public Terrain createTerrain(Terrain t) {
        if (!isDataValid(t)) {
            // on retourne null si invalide (le Facade traduira en false)
            return null;
        }

        terrainDAO.saveTerrain(t); // va setter l'id à partir du RETURNING
        return t;
    }

    public Terrain updateTerrain(Terrain t) {
        if (t.getId() == null || !isDataValid(t)) {
            return null;
        }

        terrainDAO.updateTerrain(t);
        return t;
    }

    public void deleteTerrain(Long id) {
        terrainDAO.deleteTerrain(id);
    }

    private boolean isDataValid(Terrain t) {
        if (t == null) return false;
        if (t.getName() == null || t.getName().isBlank()) return false;
        if (t.getPricePerHour() < 0) return false;
        if (t.getCapacity() < 0) return false;
        return true;
    }

    public List<Terrain> getAllTerrains() {
        return terrainDAO.getAllTerrains();
    }

}
