package com.example.myarena.persistance.factory;

import com.example.myarena.persistance.dao.ReservationDAO;
import com.example.myarena.persistance.dao.ReservationDAOPostgres;
import com.example.myarena.persistance.dao.UserDAO;
import com.example.myarena.persistance.dao.UserDAOPostgres;

public class PostgresFactory extends AbstractFactory {

    @Override
    public UserDAO createUserDAO() {
        return new UserDAOPostgres();
    }

    @Override
    public ReservationDAO createReservationDAO() {
        return new ReservationDAOPostgres();
    }

}