package com.example.myarena.persistance.factory;

import com.example.myarena.persistance.dao.UserDAO;
import com.example.myarena.persistance.dao.UserDAOPostgres;

public class PostgresFactory extends DAOFactory {

    @Override
    public UserDAO createUserDAO() {
        return new UserDAOPostgres();
    }

}