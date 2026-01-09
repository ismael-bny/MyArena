package com.example.myarena.persistance.factory;

import com.example.myarena.persistance.dao.*;
import com.example.myarena.persistance.dao.SubscriptionDAO;
import com.example.myarena.persistance.dao.SubscriptionPlanDAO;
import com.example.myarena.persistance.dao.ReservationDAO;
import com.example.myarena.persistance.dao.UserDAO;
import com.example.myarena.persistance.dao.TerrainDAO;

public abstract class AbstractFactory {

    // Retourne un UserDAO
    public abstract UserDAO createUserDAO();

    // Retourne un SubscriptionDAO
    public abstract SubscriptionDAO createSubscriptionDAO();

    // Retourne un SubscriptionPlanDAO
    public abstract SubscriptionPlanDAO createSubscriptionPlanDAO();
    // Retourne un TerrainDAO
    public abstract TerrainDAO createTerrainDAO();

    public abstract NotificationDAO createNotificationDAO();
    public abstract ReservationDAO createReservationDAO();

}
