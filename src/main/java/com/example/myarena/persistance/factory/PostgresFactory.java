package com.example.myarena.persistance.factory;

import com.example.myarena.persistance.dao.ReservationDAO;
import com.example.myarena.persistance.dao.ReservationDAOPostgres;
import com.example.myarena.persistance.dao.UserDAO;
import com.example.myarena.persistance.dao.UserDAOPostgres;
import com.example.myarena.persistance.dao.TerrainDAO;
import com.example.myarena.persistance.dao.TerrainDAOPostgres;
import com.example.myarena.persistance.dao.*;

public class PostgresFactory extends AbstractFactory {

    @Override
    public UserDAO createUserDAO() {
        return new UserDAOPostgres();
    }

    @Override
    public SubscriptionDAO createSubscriptionDAO() {
        return new SubscriptionDAOPostgres();
    }

    @Override
    public SubscriptionPlanDAO createSubscriptionPlanDAO() {
        return new SubscriptionPlanDAOPostgres();
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