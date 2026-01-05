package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Reservation;
import com.example.myarena.domain.ReservationStatus;

import java.util.List;

public interface ReservationDAO {
    Reservation getReservationById(Long id);
    List<Reservation> getReservationByUserId(Long userId);
    List<Reservation> getReservationByTerrainId(Long terrainId);

    void saveReservation(Reservation r);
    void updateReservation(Reservation r);
    void deleteReservation(Long id);
}
