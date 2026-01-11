package com.example.myarena.tests;

import com.example.myarena.domain.Reservation;
import com.example.myarena.services.ReservationManager;
import com.example.myarena.util.DatabaseConfig;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class NotificationUseCaseTest {

    private ReservationManager reservationManager;

    private static final Long testUserId = 9999L;
    private static Long testTerrainId;
    private static Long createdReservationId;
    private static Long createdNotificationId;

    @BeforeAll
    static void cleanupBefore() {
        System.out.println("=== NOTIFICATION TEST SETUP ===");

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Ensure user exists
            String createUserSql =
                    "INSERT INTO users (id, name, email, password_hash, phone, role, status) " +
                            "VALUES (" + testUserId + ", 'Notif Test User', 'notif_user@test.com', 'hash123', '00000000', 'CLIENT', 'ACTIVE') " +
                            "ON CONFLICT (id) DO NOTHING";
            stmt.executeUpdate(createUserSql);

            // Clean previous test data
            stmt.executeUpdate("DELETE FROM notifications WHERE user_id = " + testUserId);
            stmt.executeUpdate("DELETE FROM reservations WHERE user_id = " + testUserId);
            stmt.executeUpdate("DELETE FROM terrains WHERE name = 'TEST_NOTIF_ARENA'");

            System.out.println("✓ Database cleaned & User ensured");

        } catch (Exception e) {
            System.err.println("⚠ Setup warning: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        reservationManager = new ReservationManager();

        // Create a test terrain
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO terrains (name, type, price_per_hour, location, capacity, available) " +
                    "VALUES ('TEST_NOTIF_ARENA', 'FOOTBALL', 50.0, 'Test Location', 22, true) RETURNING id";

            ResultSet rs = stmt.executeQuery(sql);
            assertTrue(rs.next());
            testTerrainId = rs.getLong(1);

        } catch (Exception e) {
            fail("Error creating test terrain: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UC1 - Create Reservation should generate Notification")
    void test01CreateReservationGeneratesNotification() {
        assertNotNull(testTerrainId);

        Date start = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0));
        Date end = Timestamp.valueOf(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0));

        Reservation res = reservationManager.createReservation(
                testUserId, testTerrainId, start, end, 10, "Notif UseCase"
        );

        assertNotNull(res, "Reservation creation failed");
        assertNotNull(res.getId(), "Reservation ID should be generated");
        createdReservationId = res.getId();

        // Verify notification exists in DB
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT id, type, status FROM notifications " +
                             "WHERE user_id = " + testUserId + " ORDER BY created_at DESC LIMIT 1"
             )) {

            assertTrue(rs.next(), "Expected a notification for the reservation");

            createdNotificationId = rs.getLong("id");
            String type = rs.getString("type");
            String status = rs.getString("status");

            assertNotNull(createdNotificationId);
            assertNotNull(type);
            assertEquals("PENDING", status, "New notification should be PENDING by default");

            System.out.println("✓ Reservation created: " + createdReservationId +
                    " => Notification: " + createdNotificationId + " (" + type + ")");

        } catch (Exception e) {
            fail("DB verification failed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UC2 - Mark Notification as READ (DB)")
    void test02MarkNotificationAsRead() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            if (createdNotificationId == null) {
                ResultSet rs = stmt.executeQuery(
                        "SELECT id FROM notifications WHERE user_id = " + testUserId + " ORDER BY created_at DESC LIMIT 1"
                );
                assertTrue(rs.next(), "No notification found to mark as READ");
                createdNotificationId = rs.getLong(1);
            }

            stmt.executeUpdate("UPDATE notifications SET status = 'READ' WHERE id = " + createdNotificationId);

            ResultSet verify = stmt.executeQuery("SELECT status FROM notifications WHERE id = " + createdNotificationId);
            assertTrue(verify.next());
            assertEquals("READ", verify.getString("status"));

            System.out.println("✓ Notification marked READ: " + createdNotificationId);

        } catch (Exception e) {
            fail("Update/verify failed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UC3 - Count Unread Notifications")
    void test03CountUnread() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) FROM notifications WHERE user_id = " + testUserId + " AND status = 'PENDING'"
             )) {

            assertTrue(rs.next());
            int unread = rs.getInt(1);

            assertTrue(unread >= 0);
            System.out.println("✓ Unread notifications = " + unread);

        } catch (Exception e) {
            fail("Unread count query failed: " + e.getMessage());
        }
    }

    @AfterAll
    static void cleanupAfter() {
        System.out.println("=== NOTIFICATION TEST CLEANUP ===");
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM notifications WHERE user_id = " + testUserId);
            stmt.executeUpdate("DELETE FROM reservations WHERE user_id = " + testUserId);
            stmt.executeUpdate("DELETE FROM terrains WHERE name = 'TEST_NOTIF_ARENA'");
            System.out.println("✓ Cleanup done");
        } catch (Exception e) {
            System.err.println("⚠ Cleanup warning: " + e.getMessage());
        }
    }
}
