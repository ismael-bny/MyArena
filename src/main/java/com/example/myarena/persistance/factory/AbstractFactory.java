package com.example.myarena.persistance.factory;

import com.example.myarena.persistance.dao.*;

public abstract class AbstractFactory {

    // Retourne un UserDAO
    public abstract UserDAO createUserDAO();

    // Retourne un SubscriptionDAO
    public abstract SubscriptionDAO createSubscriptionDAO();

    // Retourne un SubscriptionPlanDAO
    public abstract SubscriptionPlanDAO createSubscriptionPlanDAO();

    // Retourne un ReservationDAO
    public abstract ReservationDAO createReservationDAO();

    // Retourne un TerrainDAO
    public abstract TerrainDAO createTerrainDAO();

    // Retourne un DiscountDAO
    public abstract DiscountDAO createDiscountDAO();

    // Retourne un ProductDAO
    public abstract ProductDAO createProductDAO();

    // Retourne un CartDAO
    public abstract CartDAO createCartDAO();

    // Retourne un OrderDAO
    public abstract OrderDAO createOrderDAO();

    public abstract NotificationDAO createNotificationDAO();
}
