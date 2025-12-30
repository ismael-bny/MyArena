package com.example.myarena.persistance.factory;

import com.example.myarena.persistance.dao.UserDAO;
import com.example.myarena.persistance.dao.TerrainDAO;

public abstract class AbstractFactory {

    // Retourne un UserDAO
    public abstract UserDAO createUserDAO();

    // Retourne un TerrainDAO
    public abstract TerrainDAO createTerrainDAO();
}
