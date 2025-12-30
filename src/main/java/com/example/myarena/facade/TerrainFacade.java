package com.example.myarena.facade;

import com.example.myarena.domain.Terrain;
import com.example.myarena.services.TerrainManager;

import java.util.List;

public class TerrainFacade {

    private static TerrainFacade instance;
    private final TerrainManager terrainManager;

    private TerrainFacade() {
        this.terrainManager = new TerrainManager();
    }

    public static TerrainFacade getInstance() {
        if (instance == null) {
            instance = new TerrainFacade();
        }
        return instance;
    }

    public boolean createTerrain(Terrain t) {
        Terrain created = terrainManager.createTerrain(t);
        return created != null;
    }

    public boolean updateTerrain(Terrain t) {
        Terrain updated = terrainManager.updateTerrain(t);
        return updated != null;
    }

    public boolean deleteTerrain(Long id) {
        try {
            terrainManager.deleteTerrain(id);
            return true;
        } catch (RuntimeException e) {
            // log éventuel
            return false;
        }
    }

    public List<Terrain> getAllTerrains() {
        return terrainManager.getAllTerrains();
    }

}
