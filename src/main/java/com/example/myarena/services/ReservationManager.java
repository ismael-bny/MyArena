package com.example.myarena.services;

import com.example.myarena.domain.Reservation;
import com.example.myarena.domain.ReservationStatus;
import com.example.myarena.domain.Terrain;
import com.example.myarena.persistance.dao.ReservationDAO;
import com.example.myarena.persistance.dao.TerrainDAO;
import com.example.myarena.persistance.factory.AbstractFactory;
import com.example.myarena.persistance.factory.PostgresFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
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

    public List<String> getAvailableSlots(Long terrainId, LocalDate date) {
        List<String> availableSlots = new ArrayList<>();

        // 1. Get all reservations for this terrain
        List<Reservation> reservations = reservationDAO.getReservationByTerrainId(terrainId);

        // 2. Define Opening Hours (9 AM to 10 PM)
        int startHour = 9;
        int endHour = 22;

        for (int hour = startHour; hour < endHour; hour++) {
            // Construct the time slot (e.g., 09:00 - 10:00)
            Calendar slotStart = Calendar.getInstance();
            slotStart.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), hour, 0, 0);

            Calendar slotEnd = Calendar.getInstance();
            slotEnd.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth(), hour + 1, 0, 0);

            // 3. Check if this slot overlaps with any existing reservation
            boolean isBooked = false;
            for (Reservation r : reservations) {
                if (r.getStatus() == ReservationStatus.Cancelled) continue;

                // Convert Date to Calendar for comparison
                long rStart = r.getStartDate().getTime();
                long rEnd = r.getEndDate().getTime();
                long sStart = slotStart.getTimeInMillis();
                long sEnd = slotEnd.getTimeInMillis();

                // Check overlap: (StartA < EndB) and (EndA > StartB)
                if (sStart < rEnd && sEnd > rStart) {
                    isBooked = true;
                    break;
                }
            }

            if (!isBooked) {
                availableSlots.add(String.format("%02d:00 - %02d:00", hour, hour + 1));
            }
        }
        return availableSlots;
    }

    public boolean checkAvailability(Long terrainId, Date startDate, Date endDate){
        List<Reservation> existingReservations = reservationDAO.getReservationByTerrainId(terrainId);
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
