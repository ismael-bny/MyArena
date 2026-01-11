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

    @Override
    public TerrainDAO createTerrainDAO() {
        return new TerrainDAOPostgres();
    }

    @Override
    public ReservationDAO createReservationDAO() {
        return new ReservationDAOPostgres();
    }

    @Override
    public DiscountDAO createDiscountDAO() {
        return new DiscountDAOPostgres();
    }

    @Override
    public ProductDAO createProductDAO() {
        return new ProductDAOPostgres();
    }

    @Override
    public CartDAO createCartDAO() {
        return new CartDAOPostgres();
    }

    @Override
    public OrderDAO createOrderDAO() {
        return new OrderDAOPostgres();
    }

    @Override
    public NotificationDAO createNotificationDAO() {
        return new NotificationDAOPostgres();
    }

    @Override
    public TournamentDAO createTournamentDAO() { return new TournamentDAOPostgres(); }

    @Override
    public TournamentRegistrationDAO createTournamentRegistrationDAO() { return new TournamentRegistrationDAOPostgres();}

}

