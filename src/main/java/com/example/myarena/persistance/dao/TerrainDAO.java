package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Terrain;

import java.util.List;

public interface TerrainDAO {

    Terrain getTerrainByID(Long id);

    // Sauvegarde un terrain (INSERT)
    // La méthode doit mettre à jour t.setId(idGénéré) si l’INSERT réussit
    void saveTerrain(Terrain t);

    // Met à jour un terrain existant (UPDATE).
    void updateTerrain(Terrain t);

    // Supprime un terrain par son id.
    void deleteTerrain(Long id);

    List<Terrain> getAllTerrains();
}
