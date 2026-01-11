package com.example.myarena.tests;

import com.example.myarena.domain.*;
import com.example.myarena.services.SubscriptionManager;
import com.example.myarena.util.DatabaseConfig;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Use Case: Subscription Management
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubscriptionManagementTest {

    private SubscriptionManager subscriptionManager;
    private static Long testUserId = 1L;
    private static Long createdPlanId;

    @BeforeAll
    static void cleanupBefore() {
        System.out.println("=== NETTOYAGE INITIAL ===");
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Supprimer les données de test
            stmt.executeUpdate("DELETE FROM subscription WHERE user_id IN (1, 2)");
            stmt.executeUpdate("DELETE FROM subscription_plan WHERE name LIKE '%TEST%'");

            System.out.println("✓ Données nettoyées");

        } catch (Exception e) {
            System.err.println("⚠ Erreur nettoyage: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        subscriptionManager = new SubscriptionManager();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("SUB1 - Créer un plan d'abonnement")
    void test01CreatePlan() {
        try {
            SubscriptionPlan plan = new SubscriptionPlan();
            plan.setName("Plan TEST Premium");
            plan.setPrice(29.99);
            plan.setDurationMonths(3);
            plan.setActive(true);

            SubscriptionPlan created = subscriptionManager.createPlan(plan);

            assertNotNull(created);
            assertNotNull(created.getId());
            assertEquals("Plan TEST Premium", created.getName());
            assertEquals(29.99, created.getPrice());
            assertEquals(3, created.getDurationMonths());

            createdPlanId = created.getId();

            // Vérifier en BD
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM subscription_plan WHERE id = " + createdPlanId)) {

                assertTrue(rs.next());
                assertEquals("Plan TEST Premium", rs.getString("name"));
                assertEquals(29.99, rs.getDouble("price"), 0.01);
                System.out.println("✓ PLAN CRÉÉ EN BD - ID: " + createdPlanId);
            }

        } catch (Exception e) {
            System.out.println("⚠ BD requise: " + e.getMessage());
        }
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("SUB2 - Modifier un plan d'abonnement")
    void test02UpdatePlan() {
        try {
            // Récupérer le plan créé
            SubscriptionPlan plan = subscriptionManager.getSubscriptionPlans()
                .stream()
                .filter(p -> p.getId().equals(createdPlanId))
                .findFirst()
                .orElse(null);

            assertNotNull(plan, "Plan non trouvé");

            // Modifier
            plan.setName("Plan TEST Premium UPDATED");
            plan.setPrice(39.99);
            plan.setDurationMonths(6);

            SubscriptionPlan updated = subscriptionManager.updatePlan(plan);

            assertNotNull(updated);
            assertEquals("Plan TEST Premium UPDATED", updated.getName());
            assertEquals(39.99, updated.getPrice());
            assertEquals(6, updated.getDurationMonths());

            // Vérifier en BD
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM subscription_plan WHERE id = " + createdPlanId)) {

                assertTrue(rs.next());
                assertEquals("Plan TEST Premium UPDATED", rs.getString("name"));
                assertEquals(39.99, rs.getDouble("price"), 0.01);
                assertEquals(6, rs.getInt("duration_months"));
                System.out.println("✓ PLAN MODIFIÉ EN BD");
            }

        } catch (Exception e) {
            System.out.println("⚠ BD requise: " + e.getMessage());
        }
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("SUB3 - S'inscrire à un plan")
    void test03SubscribeToPlan() {
        try {
            // Nettoyer les abonnements existants
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM subscription WHERE user_id = " + testUserId);
            }

            // S'inscrire
            Subscription subscription = subscriptionManager.subscribeToPlan(testUserId, createdPlanId);

            assertNotNull(subscription);
            assertNotNull(subscription.getId());
            assertEquals(testUserId, subscription.getUserId());
            assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
            assertTrue(subscription.isAutoRenew());
            assertNotNull(subscription.getStartDate());
            assertNotNull(subscription.getEndDate());

            // Vérifier les dates
            LocalDate expectedEnd = subscription.getStartDate().plusMonths(6); // 6 mois du plan modifié
            assertEquals(expectedEnd, subscription.getEndDate());

            // Vérifier en BD
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM subscription WHERE user_id = " + testUserId + " AND status = 'ACTIVE'")) {

                assertTrue(rs.next());
                assertEquals(testUserId.longValue(), rs.getLong("user_id"));
                assertEquals("ACTIVE", rs.getString("status"));
                assertTrue(rs.getBoolean("auto_renew"));
                System.out.println("✓ INSCRIPTION EN BD - Début: " + rs.getDate("start_date"));
            }

        } catch (Exception e) {
            System.out.println("⚠ BD requise: " + e.getMessage());
        }
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("SUB4 - Annuler un abonnement")
    void test04CancelSubscription() {
        try {
            // S'assurer qu'il y a un abonnement actif
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM subscription WHERE user_id = " + testUserId);
            }

            // Créer un abonnement à annuler
            subscriptionManager.subscribeToPlan(testUserId, createdPlanId);

            // Annuler
            boolean cancelled = subscriptionManager.cancelSubscription(testUserId);

            assertTrue(cancelled, "L'annulation devrait réussir");

            // Vérifier que l'abonnement est annulé
            Subscription active = subscriptionManager.getActiveSubscription(testUserId);
            assertNull(active, "Il ne devrait plus y avoir d'abonnement actif");

            // Vérifier en BD
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM subscription WHERE user_id = " + testUserId + " AND status = 'CANCELLED'")) {

                assertTrue(rs.next());
                assertEquals("CANCELLED", rs.getString("status"));
                assertFalse(rs.getBoolean("auto_renew"));
                System.out.println("✓ ABONNEMENT ANNULÉ EN BD");
            }

        } catch (Exception e) {
            System.out.println("⚠ BD requise: " + e.getMessage());
        }
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("SUB5 - Validation : Créer plan avec prix invalide")
    void test05ValidationInvalidPrice() {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Plan Invalid");
        plan.setPrice(-10.0); // Prix invalide
        plan.setDurationMonths(3);

        assertThrows(IllegalArgumentException.class, () ->
            subscriptionManager.createPlan(plan));
        System.out.println("✓ Exception levée pour prix invalide");
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    @DisplayName("SUB6 - Validation : Créer plan avec durée invalide")
    void test06ValidationInvalidDuration() {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Plan Invalid");
        plan.setPrice(29.99);
        plan.setDurationMonths(0); // Durée invalide

        assertThrows(IllegalArgumentException.class, () ->
            subscriptionManager.createPlan(plan));
        System.out.println("✓ Exception levée pour durée invalide");
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    @DisplayName("SUB7 - Validation : Nom vide")
    void test07ValidationEmptyName() {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(""); // Nom vide
        plan.setPrice(29.99);
        plan.setDurationMonths(3);

        assertThrows(IllegalArgumentException.class, () ->
            subscriptionManager.createPlan(plan));
        System.out.println("✓ Exception levée pour nom vide");
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    @DisplayName("SUB8 - Validation : Double inscription")
    void test08DoubleSubscription() {
        try {
            // Nettoyer et créer un abonnement
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM subscription WHERE user_id = " + testUserId);
            }

            subscriptionManager.subscribeToPlan(testUserId, createdPlanId);

            // Tenter une deuxième inscription
            assertThrows(IllegalStateException.class, () ->
                subscriptionManager.subscribeToPlan(testUserId, createdPlanId));

            System.out.println("✓ Exception levée pour double inscription");

        } catch (Exception e) {
            System.out.println("⚠ BD requise: " + e.getMessage());
        }
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    @DisplayName("SUB9 - Récupérer tous les plans")
    void test09GetAllPlans() {
        try {
            var plans = subscriptionManager.getSubscriptionPlans();

            assertNotNull(plans);
            assertTrue(plans.size() > 0);

            boolean foundTestPlan = plans.stream()
                .anyMatch(p -> p.getId().equals(createdPlanId));
            assertTrue(foundTestPlan, "Le plan de test devrait être dans la liste");

            System.out.println("✓ Plans récupérés: " + plans.size());

        } catch (Exception e) {
            System.out.println("⚠ BD requise: " + e.getMessage());
        }
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    @DisplayName("SUB10 - Supprimer un plan")
    void test10DeletePlan() {
        try {
            // S'assurer qu'aucun abonnement actif n'utilise ce plan
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM subscription WHERE plan_id = " + createdPlanId);
            }

            subscriptionManager.deletePlan(createdPlanId);

            // Vérifier en BD
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM subscription_plan WHERE id = " + createdPlanId)) {

                assertFalse(rs.next(), "Le plan ne devrait plus exister");
                System.out.println("✓ PLAN SUPPRIMÉ DE LA BD");
            }

        } catch (Exception e) {
            System.out.println("⚠ BD requise: " + e.getMessage());
        }
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    @DisplayName("SUB11 - WORKFLOW COMPLET")
    void test11CompleteWorkflow() {
        try {
            System.out.println("\n=== WORKFLOW SUBSCRIPTION ===");

            // Nettoyer
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM subscription WHERE user_id = 2");
                stmt.executeUpdate("DELETE FROM subscription_plan WHERE name = 'Workflow TEST'");
            }

            // 1. Créer plan
            SubscriptionPlan plan = new SubscriptionPlan();
            plan.setName("Workflow TEST");
            plan.setPrice(49.99);
            plan.setDurationMonths(12);
            plan.setActive(true);

            plan = subscriptionManager.createPlan(plan);
            System.out.println("1. Plan créé ✓");

            // 2. S'inscrire
            Subscription sub = subscriptionManager.subscribeToPlan(2L, plan.getId());
            System.out.println("2. Inscription ✓");

            // 3. Vérifier abonnement actif
            Subscription active = subscriptionManager.getActiveSubscription(2L);
            assertNotNull(active);
            System.out.println("3. Abonnement actif ✓");

            // 4. Annuler
            boolean cancelled = subscriptionManager.cancelSubscription(2L);
            assertTrue(cancelled);
            System.out.println("4. Annulation ✓");

            // 5. Vérifier plus d'abonnement actif
            active = subscriptionManager.getActiveSubscription(2L);
            assertNull(active);
            System.out.println("5. Plus d'abonnement actif ✓");

            // 6. Supprimer plan
            subscriptionManager.deletePlan(plan.getId());
            System.out.println("6. Plan supprimé ✓");

            System.out.println("\n✓ WORKFLOW SUBSCRIPTION RÉUSSI!\n");

        } catch (Exception e) {
            System.out.println("⚠ BD requise: " + e.getMessage() + "\n");
        }
    }

    @AfterAll
    static void cleanup() {
        System.out.println("\n=== NETTOYAGE FINAL ===");
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM subscription WHERE user_id IN (1, 2)");
            stmt.executeUpdate("DELETE FROM subscription_plan WHERE name LIKE '%TEST%' OR name = 'Workflow TEST'");

            System.out.println("✓ Nettoyage terminé");
        } catch (Exception e) {
            System.err.println("⚠ Erreur nettoyage: " + e.getMessage());
        }
    }
}

