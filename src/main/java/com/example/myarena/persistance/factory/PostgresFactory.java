package com.example.myarena.persistance.factory;

import com.example.myarena.persistance.dao.ReservationDAO;
import com.example.myarena.persistance.dao.ReservationDAOPostgres;
import com.example.myarena.persistance.dao.UserDAO;
import com.example.myarena.persistance.dao.UserDAOPostgres;
import com.example.myarena.persistance.dao.TerrainDAO;
import com.example.myarena.persistance.dao.TerrainDAOPostgres;

public class PostgresFactory extends AbstractFactory {

    @Override
    public UserDAO createUserDAO() {
        return new UserDAOPostgres();
    }

    @Override
    public TerrainDAO createTerrainDAO() {
        return new TerrainDAOPostgres();
    }
    @Override
    public ReservationDAO createReservationDAO() {
        return new ReservationDAOPostgres();
    }

}