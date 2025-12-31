package com.example.myarena.ui;

import com.example.myarena.domain.Terrain;
import com.example.myarena.facade.TerrainFacade;

import java.util.List;

public class TerrainController {

    private final TerrainManagementFrame view;
    private final TerrainFacade terrainFacade;

    public TerrainController(TerrainManagementFrame view) {
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
            return; // cancel ou invalide
        }

        boolean success = terrainFacade.createTerrain(newTerrain);
        if (success) {
            view.showMessage("Field created with ID: " + newTerrain.getId(), true);
            loadTerrains();
        } else {
            view.showMessage("Failed to create field.", false);
        }
    }

    public void onEditTerrain(Terrain t) {
        Terrain edited = view.openTerrainForm(t);
        if (edited == null) {
            return;
        }

        boolean success = terrainFacade.updateTerrain(edited);
        if (success) {
            view.showMessage("Field updated (ID: " + edited.getId() + ")", true);
            loadTerrains();
        } else {
            view.showMessage("Failed to update field.", false);
        }
    }

    public void onDeleteTerrain(Terrain t) {
        if (t == null || t.getId() == null) {
            view.showMessage("No field selected to delete.", false);
            return;
        }

        boolean success = terrainFacade.deleteTerrain(t.getId());
        if (success) {
            view.showMessage("Field deleted (ID: " + t.getId() + ")", true);
            loadTerrains();
        } else {
            view.showMessage("Failed to delete field.", false);
        }
    }

    public void onViewTerrain(Terrain t) {
        view.openTerrainDetails(t);
    }

}
