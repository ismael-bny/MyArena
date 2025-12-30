package com.example.myarena.facade;

import com.example.myarena.domain.Reservation;
import com.example.myarena.services.ReservationManager;

import java.util.Date;
import java.util.List;

public class ReservationFacade {
    private static ReservationFacade instance;
    private final ReservationManager reservationManager;

    private ReservationFacade() {
        this.reservationManager = new ReservationManager();
    }

    public static ReservationFacade getInstance() {
        if (instance == null){
            instance = new ReservationFacade();
        }
        return instance;
    }

    public boolean createReservation(Long userId, Long terrainId, Date startDate, Date endDate){
        Reservation r = reservationManager.createReservation(userId, terrainId, startDate, endDate);
        return r != null;
    }

    public boolean cancelReservation(Long reservationId) {
        reservationManager.cancelReservation(reservationId);
        return true;
    }

    public List<Reservation> getReservationHistory(Long userId) {
        return reservationManager.getReservationHistory(userId);
    }
}
