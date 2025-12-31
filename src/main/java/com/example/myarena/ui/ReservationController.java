package com.example.myarena.ui;

import com.example.myarena.facade.ReservationFacade;

import java.util.Date;

public class ReservationController {
    private ReservationFrame view;
    private ReservationFacade reservationFacade;

    public ReservationController (ReservationFrame view) {
        this.view = view;
        this.reservationFacade = ReservationFacade.getInstance();
    }

    public void createReservation(Long userId, Long terrainId, Date startDate, Date endDate, int participants, String purpose) {
        boolean success = reservationFacade.createReservation(userId, terrainId, startDate, endDate, participants, purpose);
        if (success) {
            System.out.println("Reservation created successfully!");
        } else {
            System.out.println("Failed to create reservation (Unavailable or Error)");
        }
    }
}
