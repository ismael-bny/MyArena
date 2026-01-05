package com.example.myarena.facade;

import com.example.myarena.domain.Terrain;
import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;
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
        if (!isAuthorized()) return false;
        Terrain created = terrainManager.createTerrain(t);
        return created != null;
    }

    public boolean deleteTerrain(Long id) {
        if (!isAuthorized()) return false;
        try {
            terrainManager.deleteTerrain(id);
            return true;
        } catch (Exception e) { return false; }
    }

    private boolean isAuthorized() {
        User user = UserSession.getInstance().getUser();
        return user != null && (user.getRole() == UserRole.OWNER || user.getRole() == UserRole.ADMIN);
    }

    public boolean updateTerrain(Terrain t) {
        Terrain updated = terrainManager.updateTerrain(t);
        return updated != null;
    }

    public List<Terrain> getAllTerrains() {
        return terrainManager.getAllTerrains();
    }

}
