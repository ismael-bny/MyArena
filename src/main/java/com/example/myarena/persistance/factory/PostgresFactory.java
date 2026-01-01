package com.example.myarena.persistance.factory;

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

}