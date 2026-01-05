package com.example.myarena.services;

import com.example.myarena.domain.*;
import com.example.myarena.persistance.dao.*;
import com.example.myarena.persistance.factory.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ReservationManager {
    private final ReservationDAO reservationDAO;
    private final TerrainDAO terrainDAO;
    private final SubscriptionDAO subscriptionDAO;

    public ReservationManager() {
        PostgresFactory factory = new PostgresFactory();
        this.reservationDAO = factory.createReservationDAO();
        this.terrainDAO = factory.createTerrainDAO();
        this.subscriptionDAO = factory.createSubscriptionDAO();
    }

    public Reservation createReservation(Long userId, Long terrainId, Date start, Date end, int part, String purp) {
        Terrain terrain = terrainDAO.getTerrainByID(terrainId);
        if (terrain == null || !checkAvailability(terrainId, start, end)) return null;

        // Base Price
        long hours = Math.max(1, (end.getTime() - start.getTime()) / (1000 * 60 * 60));
        BigDecimal price = BigDecimal.valueOf(terrain.getPricePerHour()).multiply(new BigDecimal(hours));

        // --- INTEGRATION: Discount for Subscribers ---
        Subscription activeSub = subscriptionDAO.findActiveByUserId(userId);
        if (activeSub != null) {
            price = price.multiply(new BigDecimal("0.80")); // 20% discount
        }

        Reservation res = new Reservation(userId, terrainId, start, end, part, purp);
        res.setTotalPrice(price);
        res.setStatus(ReservationStatus.Confirmed);
        reservationDAO.saveReservation(res);
        return res;
    }

    public List<String> getAvailableSlots(Long terrainId, LocalDate date) {
        List<String> slots = new ArrayList<>();
        List<Reservation> reservations = reservationDAO.getReservationByTerrainId(terrainId);
        for (int h = 9; h < 22; h++) {
            boolean booked = false;
            // Simplified overlap logic
            slots.add(String.format("%02d:00 - %02d:00", h, h + 1));
        }
        return slots;
    }

    public boolean checkAvailability(Long terrainId, Date start, Date end) {
        List<Reservation> existing = reservationDAO.getReservationByTerrainId(terrainId);
        for (Reservation r : existing) {
            if (r.getStatus() != ReservationStatus.Cancelled &&
                    start.before(r.getEndDate()) && end.after(r.getStartDate())) return false;
        }
        return true;
    }

    public void cancelReservation(Long reservationId) {
        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r != null) {
            r.setStatus(ReservationStatus.Cancelled);
            reservationDAO.updateReservation(r);
        }
    }

    public boolean updateReservation(Reservation reservation) {
        try {
            // Validate availability for the new time slot
            Reservation existing = reservationDAO.getReservationById(reservation.getId());
            if (existing != null) {
                // Check if the new time slot is available (excluding current reservation)
                List<Reservation> conflicting = reservationDAO.getReservationByTerrainId(reservation.getTerrainId());
                for (Reservation r : conflicting) {
                    if (!r.getId().equals(reservation.getId()) && 
                        r.getStatus() != ReservationStatus.Cancelled &&
                        reservation.getStartDate().before(r.getEndDate()) && 
                        reservation.getEndDate().after(r.getStartDate())) {
                        return false; // Conflict found
                    }
                }
                reservationDAO.updateReservation(reservation);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReservation(Long reservationId) {
        try {
            Reservation r = reservationDAO.getReservationById(reservationId);
            if (r != null) {
                reservationDAO.deleteReservation(reservationId);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Reservation getReservationById(Long reservationId) {
        return reservationDAO.getReservationById(reservationId);
    }

    public List<Reservation> getReservationHistory(Long userId) {
        return reservationDAO.getReservationByUserId(userId);
    }
}