package com.example.myarena.tests;

import com.example.myarena.domain.Reservation;
import com.example.myarena.domain.ReservationStatus;
import com.example.myarena.services.ReservationManager;
import com.example.myarena.util.DatabaseConfig;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ReservationUseCaseTest {

    private ReservationManager reservationManager;

    // IDs for testing
    private static Long testUserId = 1L;
    private static Long testTerrainId;

    @BeforeAll
    static void cleanupBefore() {
        System.out.println("=== RESERVATION TEST SETUP ===");
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            //Clean up previous test data
            stmt.executeUpdate("DELETE FROM notifications WHERE user_id = " + testUserId);
            stmt.executeUpdate("DELETE FROM reservations WHERE user_id = " + testUserId);
            stmt.executeUpdate("DELETE FROM terrains WHERE name = 'Test Arena'");

            String createUserSql = "INSERT INTO users (id, name, email, password_hash, phone, role, status) " +
                    "VALUES (" + testUserId + ", 'Test User', 'test@example.com', 'hash123', '00000000', 'CLIENT', 'ACTIVE') " +
                    "ON CONFLICT (id) DO NOTHING";
            stmt.executeUpdate(createUserSql);

            System.out.println("✓ Database cleaned & User ensured");
        } catch (Exception e) {
            System.err.println("⚠ Setup warning: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        reservationManager = new ReservationManager();

        // Create a test terrain if not exists
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO terrains (name, type, price_per_hour, location, capacity, available) " +
                    "VALUES ('Test Arena', 'FOOTBALL', 50.0, 'Test Location', 22, true) RETURNING id";

            var rs = stmt.executeQuery(sql);
            if (rs != null && rs.next()) {
                testTerrainId = rs.getLong(1);
            }

        } catch (Exception e) {
            System.err.println("Error creating test terrain: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("UC1 - Check Availability (Empty Slot)")
    void test01CheckAvailability() {
        assertNotNull(testTerrainId, "Test Setup Failed: Terrain ID is null");

        Date start = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        Date end = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0));

        boolean available = reservationManager.checkAvailability(testTerrainId, start, end);
        assertTrue(available, "Terrain should be available for a fresh slot");
        System.out.println("✓ Availability Check Passed");
    }

    @Test
    @DisplayName("UC2 - Create Reservation")
    void test02CreateReservation() {
        assertNotNull(testTerrainId, "Test Setup Failed: Terrain ID is null");

        Date start = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0));
        Date end = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0)); // 2 hours

        Reservation res = reservationManager.createReservation(
                testUserId, testTerrainId, start, end, 10, "Friendly Match"
        );

        assertNotNull(res, "Reservation creation failed (Check console for Notification errors)");
        assertEquals(ReservationStatus.Confirmed, res.getStatus());
        assertEquals(100.0, res.getTotalPrice().doubleValue(), 0.01, "Price calculation error");

        System.out.println("✓ Reservation Created: ID " + res.getId());
    }

    @Test
    @DisplayName("UC3 - Check Overlap Conflict")
    void test03CheckOverlap() {
        assertNotNull(testTerrainId, "Test Setup Failed: Terrain ID is null");

        Date bookStart = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0));
        Date bookEnd = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0));

        // Ensure base reservation exists
        if (reservationManager.checkAvailability(testTerrainId, bookStart, bookEnd)) {
            reservationManager.createReservation(testUserId, testTerrainId, bookStart, bookEnd, 10, "Base Booking");
        }

        // Try to check overlapping slot
        Date start = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(13).withMinute(0));
        Date end = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(15).withMinute(0));

        boolean available = reservationManager.checkAvailability(testTerrainId, start, end);
        assertFalse(available, "Should detect conflict with existing reservation");
        System.out.println("✓ Conflict Detection Passed");
    }

    @Test
    @DisplayName("UC4 - Retrieve History")
    void test04GetHistory() {
        // Ensure at least one reservation exists
        Date start = Timestamp.valueOf(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
        Date end = Timestamp.valueOf(LocalDateTime.now().plusDays(2).withHour(12).withMinute(0));

        if (testTerrainId != null) {
            reservationManager.createReservation(testUserId, testTerrainId, start, end, 10, "History Test");
        }

        List<Reservation> history = reservationManager.getReservationHistory(testUserId);
        assertFalse(history.isEmpty(), "History should not be empty");
        System.out.println("✓ History Size: " + history.size());
    }

    @Test
    @DisplayName("UC5 - Cancel Reservation")
    void test05CancelReservation() {
        List<Reservation> history = reservationManager.getReservationHistory(testUserId);

        if (history.isEmpty() && testTerrainId != null) {
            Date start = Timestamp.valueOf(LocalDateTime.now().plusDays(3).withHour(10).withMinute(0));
            Date end = Timestamp.valueOf(LocalDateTime.now().plusDays(3).withHour(11).withMinute(0));
            reservationManager.createReservation(testUserId, testTerrainId, start, end, 5, "Cancel Test");
            history = reservationManager.getReservationHistory(testUserId);
        }

        assertFalse(history.isEmpty(), "Cannot test cancellation: No reservations found");

        Reservation r = history.get(0);
        reservationManager.cancelReservation(r.getId());

        Reservation updated = reservationManager.getReservationById(r.getId());
        assertEquals(ReservationStatus.Cancelled, updated.getStatus());
        System.out.println("✓ Reservation Cancelled");
    }
}