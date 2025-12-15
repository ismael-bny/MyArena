package com.example.myarena.persistance.factory;

import com.example.myarena.persistance.dao.UserDAO;

public abstract class AbstractFactory {

    // Retourne un UserDAO
    public abstract UserDAO createUserDAO();

}