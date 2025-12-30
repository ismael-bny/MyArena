package com.example.myarena.ui;

import com.example.myarena.domain.Terrain;
import com.example.myarena.facade.TerrainFacade;

import java.util.List;

public class TerrainController {

    private final TerrainMaintenanceFrame view;
    private final TerrainFacade terrainFacade;

    public TerrainController(TerrainMaintenanceFrame view) {
        this.view = view;
        this.terrainFacade = TerrainFacade.getInstance();
    }

    public void loadTerrains() {
        List<Terrain> terrains = terrainFacade.getAllTerrains();
        view.showTerrains(terrains);
    }

    public void onAddTerrain() {
        Terrain newTerrain = view.openTerrainForm(null);
        if (newTerrain == null) {
            // cancel ou invalid
            return;
        }

        boolean success = terrainFacade.createTerrain(newTerrain);
        if (success) {
            view.showMessage("Terrain created with ID: " + newTerrain.getId(), true);
            loadTerrains();
        } else {
            view.showMessage("Failed to create terrain.", false);
        }
    }

    public void onEditTerrain(Terrain t) {
        Terrain edited = view.openTerrainForm(t);
        if (edited == null) {
            return;
        }

        boolean success = terrainFacade.updateTerrain(edited);
        if (success) {
            view.showMessage("Terrain updated (ID: " + edited.getId() + ")", true);
            loadTerrains();
        } else {
            view.showMessage("Failed to update terrain.", false);
        }
    }

    public void onDeleteTerrain(Terrain t) {
        if (t == null || t.getId() == null) {
            view.showMessage("No terrain selected to delete.", false);
            return;
        }

        // petite confirmation
        // (tu peux l'améliorer plus tard)
        boolean success = terrainFacade.deleteTerrain(t.getId());
        if (success) {
            view.showMessage("Terrain deleted (ID: " + t.getId() + ")", true);
            loadTerrains();
        } else {
            view.showMessage("Failed to delete terrain.", false);
        }
    }
}
