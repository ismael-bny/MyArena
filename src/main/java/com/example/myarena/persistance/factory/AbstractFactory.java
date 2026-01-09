package com.example.myarena.persistance.factory;

import com.example.myarena.persistance.dao.*;

public abstract class AbstractFactory {

    // Retourne un UserDAO
    public abstract UserDAO createUserDAO();

    // Retourne un SubscriptionDAO
    public abstract SubscriptionDAO createSubscriptionDAO();

    // Retourne un SubscriptionPlanDAO
    public abstract SubscriptionPlanDAO createSubscriptionPlanDAO();
    public abstract ReservationDAO createReservationDAO();

    // Retourne un TerrainDAO
    public abstract TerrainDAO createTerrainDAO();

    public abstract NotificationDAO createNotificationDAO();
}
