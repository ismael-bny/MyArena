package com.example.myarena.persistance.factory;

import com.example.myarena.persistance.dao.SubscriptionDAO;
import com.example.myarena.persistance.dao.SubscriptionPlanDAO;
import com.example.myarena.persistance.dao.UserDAO;

public abstract class AbstractFactory {

    // Retourne un UserDAO
    public abstract UserDAO createUserDAO();

    // Retourne un SubscriptionDAO
    public abstract SubscriptionDAO createSubscriptionDAO();

    // Retourne un SubscriptionPlanDAO
    public abstract SubscriptionPlanDAO createSubscriptionPlanDAO();
}