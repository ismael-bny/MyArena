package com.example.myarena.services;

import com.example.myarena.domain.Reservation;
import com.example.myarena.domain.ReservationStatus;
import com.example.myarena.domain.Terrain;
import com.example.myarena.persistance.dao.ReservationDAO;
import com.example.myarena.persistance.dao.TerrainDAO;
import com.example.myarena.persistance.factory.AbstractFactory;
import com.example.myarena.persistance.factory.PostgresFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ReservationManager {
    private final ReservationDAO reservationDAO;
    private final TerrainDAO terrainDAO;

    public ReservationManager(){
        AbstractFactory factory = new PostgresFactory();
        this.reservationDAO = factory.createReservationDAO();
        this.terrainDAO = factory.createTerrainDAO();
    }

    public Reservation createReservation(Long userId, Long terrainId, Date startDate, Date endDate, int participants, String purpose) {

        // Fetch teraain info for Price
        Terrain terrain = terrainDAO.getTerrainByID(terrainId);
        if(terrain == null) {
            System.out.println("Terrain not found");
            return null;
        }

        //Check the terrain's availability
        if (!checkAvailability(terrainId, startDate, endDate)) {
            System.out.println("Terrain not available for these dates!");
            return null;
        }

        //Price calculation
        long durationInMillis = endDate.getTime() - startDate.getTime();
        long durationInHours = durationInMillis / (1000 * 60 *60);
        if (durationInHours < 1) durationInHours = 1;

        BigDecimal pricePerHour = BigDecimal.valueOf(terrain.getPricePerHour());
        BigDecimal totalPrice = pricePerHour.multiply(new BigDecimal(durationInHours));

        //Creation of the reservation
        Reservation newReservation = new Reservation(userId, terrainId, startDate, endDate, participants, purpose);
        newReservation.setTotalPrice(totalPrice);
        newReservation.setStatus(ReservationStatus.Confirmed);

        //Save the reservation
        reservationDAO.saveReservation(newReservation);
        return newReservation;
    }

    public boolean checkAvailability(Long terrainId, Date startDate, Date endDate){
        List<Reservation> existingReservations  = reservationDAO.getReservationByTerrainId(terrainId);

        for (Reservation r : existingReservations){
            if(r.getStatus() == ReservationStatus.Cancelled) continue;

            if (startDate.before(r.getEndDate()) && endDate.after(r.getStartDate())){
                return false;
            }
        }

        return true;
    }

    public void cancelReservation(Long reservationId){
        Reservation r = reservationDAO.getReservationById(reservationId);
        if(r != null){
            r.setStatus(ReservationStatus.Cancelled);
            reservationDAO.updateReservation(r);
        }
    }

    public List<Reservation> getReservationHistory(Long userId){
        return reservationDAO.getReservationByUserId(userId);
    }
}
