package com.example.myarena.tests;

import com.example.myarena.domain.*;
import com.example.myarena.services.TournamentManager;
import com.example.myarena.services.TournamentRegistrationManager;
import com.example.myarena.util.DatabaseConfig;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TournamentUseCaseTest {

    private TournamentManager tournamentManager;
    private TournamentRegistrationManager registrationManager;

    // IDs for testing
    private static Long testOrganiserId = 2L;
    private static Long testClientId = 3L;
    private static Long testAdminId = 1L;
    private static Long testTournamentId;

    @BeforeAll
    static void cleanupBefore() {
        System.out.println("=== TOURNAMENT TEST SETUP ===");
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Clean up previous test data
            stmt.executeUpdate("DELETE FROM tournament_registrations WHERE tournament_id IN (SELECT id FROM tournaments WHERE name = 'Test Tournament')");
            stmt.executeUpdate("DELETE FROM tournaments WHERE name = 'Test Tournament'");
            stmt.executeUpdate("DELETE FROM notifications WHERE user_id IN (" + testOrganiserId + ", " + testClientId + ", " + testAdminId + ")");

            // Ensure test users exist
            String createOrganiser = "INSERT INTO users (id, name, email, password_hash, phone, role, status) " +
                    "VALUES (" + testOrganiserId + ", 'Test Organiser', 'organiser@test.com', 'hash123', '00000001', 'ORGANISATEUR', 'ACTIVE') " +
                    "ON CONFLICT (id) DO NOTHING";
            stmt.executeUpdate(createOrganiser);

            String createClient = "INSERT INTO users (id, name, email, password_hash, phone, role, status) " +
                    "VALUES (" + testClientId + ", 'Test Client', 'client@test.com', 'hash456', '00000002', 'CLIENT', 'ACTIVE') " +
                    "ON CONFLICT (id) DO NOTHING";
            stmt.executeUpdate(createClient);

            String createAdmin = "INSERT INTO users (id, name, email, password_hash, phone, role, status) " +
                    "VALUES (" + testAdminId + ", 'Test Admin', 'admin@test.com', 'hash789', '00000003', 'ADMIN', 'ACTIVE') " +
                    "ON CONFLICT (id) DO NOTHING";
            stmt.executeUpdate(createAdmin);

            System.out.println("✓ Database cleaned & Test users ensured");
        } catch (Exception e) {
            System.err.println("⚠ Setup warning: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        tournamentManager = new TournamentManager();
        registrationManager = new TournamentRegistrationManager();
    }

    @Test
    @DisplayName("UC1 - Create Tournament (Organiser)")
    void test01CreateTournament() {
        Date startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(30).withHour(10).withMinute(0));
        Date endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(30).withHour(18).withMinute(0));

        Tournament tournament = new Tournament(
                testOrganiserId,
                "Test Tournament",
                "Football",
                "Test tournament for JUnit",
                "Standard FIFA rules",
                startDate,
                endDate,
                "Test Stadium",
                16,
                new BigDecimal("10.00"),
                "Trophy + 500€"
        );

        Tournament created = tournamentManager.createTournament(tournament);

        assertNotNull(created, "Tournament creation failed");
        assertNotNull(created.getId(), "Tournament ID should be generated");
        assertEquals(TournamentStatus.AwaitingApproval, created.getStatus(), "Status should be AwaitingApproval");
        assertEquals(0, created.getCurrentParticipants(), "Initial participants should be 0");

        testTournamentId = created.getId();
        System.out.println("✓ Tournament Created: ID " + testTournamentId);
    }

    @Test
    @DisplayName("UC2 - Get Pending Tournaments (Admin)")
    void test02GetPendingTournaments() {
        // Ensure a tournament exists
        if (testTournamentId == null) {
            test01CreateTournament();
        }

        List<Tournament> pending = tournamentManager.getTournamentsPendingApproval();
        assertFalse(pending.isEmpty(), "Should have at least one pending tournament");

        Tournament found = pending.stream()
                .filter(t -> t.getId().equals(testTournamentId))
                .findFirst()
                .orElse(null);

        assertNotNull(found, "Created tournament should be in pending list");
        System.out.println("✓ Pending Tournaments Count: " + pending.size());
    }

    @Test
    @DisplayName("UC3 - Approve Tournament (Admin)")
    void test03ApproveTournament() {
        // Ensure a tournament exists
        if (testTournamentId == null) {
            test01CreateTournament();
        }

        boolean approved = tournamentManager.approveTournament(testTournamentId);
        assertTrue(approved, "Tournament approval should succeed");

        Tournament tournament = tournamentManager.getTournamentById(testTournamentId);
        assertEquals(TournamentStatus.Open, tournament.getStatus(), "Status should be Open after approval");

        System.out.println("✓ Tournament Approved");
    }

    @Test
    @DisplayName("UC4 - Get Available Tournaments (Client)")
    void test04GetAvailableTournaments() {
        // Ensure tournament is approved
        if (testTournamentId == null) {
            test01CreateTournament();
            test03ApproveTournament();
        }

        List<Tournament> available = tournamentManager.getAvailableTournaments();
        assertFalse(available.isEmpty(), "Should have at least one available tournament");

        Tournament found = available.stream()
                .filter(t -> t.getId().equals(testTournamentId))
                .findFirst()
                .orElse(null);

        assertNotNull(found, "Approved tournament should be in available list");
        System.out.println("✓ Available Tournaments Count: " + available.size());
    }

    @Test
    @DisplayName("UC5 - Register to Tournament (Client)")
    void test05RegisterToTournament() {
        // Ensure tournament is approved
        if (testTournamentId == null) {
            test01CreateTournament();
            test03ApproveTournament();
        }

        TournamentRegistration registration = registrationManager.registerToTournament(testTournamentId, testClientId);

        assertNotNull(registration, "Registration should succeed");
        assertNotNull(registration.getId(), "Registration ID should be generated");
        assertEquals(RegistrationStatus.PendingValidation, registration.getStatus(), "Status should be PendingValidation");

        // Verify participant count increased
        Tournament tournament = tournamentManager.getTournamentById(testTournamentId);
        assertEquals(1, tournament.getCurrentParticipants(), "Participant count should be 1");

        System.out.println("✓ Client Registered: Registration ID " + registration.getId());
    }

    @Test
    @DisplayName("UC6 - Prevent Duplicate Registration")
    void test06PreventDuplicateRegistration() {
        // Ensure first registration exists
        if (testTournamentId == null) {
            test01CreateTournament();
            test03ApproveTournament();
            test05RegisterToTournament();
        }

        // Try to register again
        TournamentRegistration duplicate = registrationManager.registerToTournament(testTournamentId, testClientId);

        assertNull(duplicate, "Duplicate registration should be prevented");
        System.out.println("✓ Duplicate Registration Blocked");
    }

    @Test
    @DisplayName("UC7 - Get Tournament Registrations (Organiser)")
    void test07GetTournamentRegistrations() {
        // Ensure registration exists
        if (testTournamentId == null) {
            test01CreateTournament();
            test03ApproveTournament();
            test05RegisterToTournament();
        }

        List<TournamentRegistration> registrations = registrationManager.getRegistrationsByTournament(testTournamentId);

        assertFalse(registrations.isEmpty(), "Should have at least one registration");
        assertEquals(1, registrations.size(), "Should have exactly 1 registration");

        TournamentRegistration reg = registrations.get(0);
        assertEquals(testClientId, reg.getUserId(), "User ID should match");

        System.out.println("✓ Registrations Retrieved: " + registrations.size());
    }

    @Test
    @DisplayName("UC8 - Validate Registration (Organiser)")
    void test08ValidateRegistration() {
        // Ensure registration exists
        if (testTournamentId == null) {
            test01CreateTournament();
            test03ApproveTournament();
            test05RegisterToTournament();
        }

        List<TournamentRegistration> registrations = registrationManager.getRegistrationsByTournament(testTournamentId);
        assertFalse(registrations.isEmpty(), "No registrations found to validate");

        TournamentRegistration reg = registrations.get(0);
        boolean validated = registrationManager.validateRegistration(reg.getId(), testOrganiserId);

        assertTrue(validated, "Registration validation should succeed");

        TournamentRegistration updated = registrationManager.getRegistrationById(reg.getId());
        assertEquals(RegistrationStatus.Validated, updated.getStatus(), "Status should be Validated");

        System.out.println("✓ Registration Validated");
    }

    @Test
    @DisplayName("UC9 - Cancel Registration (Client)")
    void test09CancelRegistration() {
        // Create a new registration for cancellation test
        if (testTournamentId == null) {
            test01CreateTournament();
            test03ApproveTournament();
        }

        // Register a different user (ID 4) for this test
        Long testUserId = 4L;
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            String createUser = "INSERT INTO users (id, name, email, password_hash, phone, role, status) " +
                    "VALUES (" + testUserId + ", 'Test User 4', 'user4@test.com', 'hash', '00000004', 'CLIENT', 'ACTIVE') " +
                    "ON CONFLICT (id) DO NOTHING";
            stmt.executeUpdate(createUser);
        } catch (Exception e) {
            System.err.println("Setup error: " + e.getMessage());
        }

        TournamentRegistration registration = registrationManager.registerToTournament(testTournamentId, testUserId);
        assertNotNull(registration, "Registration setup failed");

        boolean cancelled = registrationManager.cancelRegistration(testTournamentId, testUserId);
        assertTrue(cancelled, "Registration cancellation should succeed");

        TournamentRegistration updated = registrationManager.getRegistrationById(registration.getId());
        assertEquals(RegistrationStatus.Cancelled, updated.getStatus(), "Status should be Cancelled");

        System.out.println("✓ Registration Cancelled");
    }

    @Test
    @DisplayName("UC10 - Update Tournament (Organiser)")
    void test10UpdateTournament() {
        // Ensure tournament exists
        if (testTournamentId == null) {
            test01CreateTournament();
        }

        Tournament tournament = tournamentManager.getTournamentById(testTournamentId);
        tournament.setDescription("Updated description for testing");
        tournament.setMaxParticipants(32); // Increase capacity

        boolean updated = tournamentManager.updateTournament(tournament, testOrganiserId);
        assertTrue(updated, "Tournament update should succeed");

        Tournament refreshed = tournamentManager.getTournamentById(testTournamentId);
        assertEquals("Updated description for testing", refreshed.getDescription());
        assertEquals(32, refreshed.getMaxParticipants());

        System.out.println("✓ Tournament Updated");
    }

    @Test
    @DisplayName("UC11 - Reject Tournament (Admin)")
    void test11RejectTournament() {
        // Create a new tournament for rejection test
        Date startDate = Timestamp.valueOf(LocalDateTime.now().plusDays(60).withHour(10).withMinute(0));
        Date endDate = Timestamp.valueOf(LocalDateTime.now().plusDays(60).withHour(18).withMinute(0));

        Tournament tournament = new Tournament(
                testOrganiserId,
                "Test Tournament",
                "Basketball",
                "To be rejected",
                "Standard rules",
                startDate,
                endDate,
                "Test Gym",
                8,
                new BigDecimal("15.00"),
                "Medal"
        );

        Tournament created = tournamentManager.createTournament(tournament);
        assertNotNull(created, "Tournament creation failed");

        boolean rejected = tournamentManager.rejectTournament(created.getId(), "Insufficient details provided");
        assertTrue(rejected, "Tournament rejection should succeed");

        Tournament refreshed = tournamentManager.getTournamentById(created.getId());
        assertEquals(TournamentStatus.Rejected, refreshed.getStatus(), "Status should be Rejected");

        System.out.println("✓ Tournament Rejected");
    }

    @Test
    @DisplayName("UC12 - Get User Registrations (Client)")
    void test12GetUserRegistrations() {
        // Ensure registration exists
        if (testTournamentId == null) {
            test01CreateTournament();
            test03ApproveTournament();
            test05RegisterToTournament();
        }

        List<TournamentRegistration> userRegistrations = registrationManager.getRegistrationsByUser(testClientId);

        assertFalse(userRegistrations.isEmpty(), "User should have at least one registration");

        System.out.println("✓ User Registrations Count: " + userRegistrations.size());
    }

    @AfterAll
    static void cleanup() {
        System.out.println("=== TOURNAMENT TEST CLEANUP ===");
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Clean up test data
            stmt.executeUpdate("DELETE FROM tournament_registrations WHERE tournament_id IN (SELECT id FROM tournaments WHERE name = 'Test Tournament')");
            stmt.executeUpdate("DELETE FROM tournaments WHERE name = 'Test Tournament'");
            stmt.executeUpdate("DELETE FROM notifications WHERE user_id IN (" + testOrganiserId + ", " + testClientId + ", " + testAdminId + ")");

            System.out.println("✓ Test data cleaned");
        } catch (Exception e) {
            System.err.println("⚠ Cleanup warning: " + e.getMessage());
        }
    }
}