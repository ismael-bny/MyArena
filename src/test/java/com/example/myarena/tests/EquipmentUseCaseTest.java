package com.example.myarena.tests;

import com.example.myarena.domain.ItemType;
import com.example.myarena.domain.Product;
import com.example.myarena.services.ProductManager;
import com.example.myarena.util.DatabaseConfig;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class EquipmentUseCaseTest {

    private ProductManager productManager;

    // IDs for testing
    private static Long testOwnerId = 999L; // Using a specific ID to avoid conflicts
    private static Long testProductId;

    @BeforeAll
    static void cleanupBefore() {
        System.out.println("=== EQUIPMENT TEST SETUP ===");
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Clean up previous test data to start fresh
            // We delete from child tables first if necessary (like cart_items)
            stmt.executeUpdate("DELETE FROM cart_items WHERE product_id IN (SELECT id FROM products WHERE owner_id = " + testOwnerId + ")");
            stmt.executeUpdate("DELETE FROM products WHERE owner_id = " + testOwnerId);

            // 2. Ensure Owner Exists in 'users' table
            // This prevents "insert or update on table 'products' violates foreign key constraint"
            String createOwnerSql = "INSERT INTO users (id, name, email, password_hash, phone, role, status) " +
                    "VALUES (" + testOwnerId + ", 'Test Owner', 'owner@test.com', 'hash123', '1234567890', 'OWNER', 'ACTIVE') " +
                    "ON CONFLICT (id) DO NOTHING";
            stmt.executeUpdate(createOwnerSql);

            System.out.println("✓ Database cleaned & Owner ensured");

        } catch (Exception e) {
            System.err.println("⚠ Setup warning: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        productManager = ProductManager.getInstance();
    }

    @Test
    @DisplayName("UC1 - Create Product")
    void test01CreateProduct() {
        Product p = new Product();
        p.setOwnerId(testOwnerId); // Link to our test owner
        p.setName("Test Football Pro");
        p.setDescription("Official Match Ball");
        p.setPrice(120.0);
        p.setRentalPricePerDay(15.0);
        p.setStock(50);
        p.setSellable(true);
        p.setRentable(true);

        productManager.saveProduct(p);

        // Verify ID generation
        assertNotNull(p.getId(), "Product ID should be generated upon save");
        testProductId = p.getId();

        System.out.println("✓ Product Created: ID " + testProductId);
    }

    @Test
    @DisplayName("UC2 - Verify Product Persistence")
    void test02VerifyPersistence() {
        assertNotNull(testProductId, "Previous test failed to generate ID, cannot verify persistence.");

        Product retrieved = productManager.getProductById(testProductId);
        assertNotNull(retrieved, "Product should exist in database");
        assertEquals("Test Football Pro", retrieved.getName());
        assertEquals(120.0, retrieved.getPrice());
        assertEquals(testOwnerId, retrieved.getOwnerId());
    }

    @Test
    @DisplayName("UC3 - Update Product")
    void test03UpdateProduct() {
        assertNotNull(testProductId);

        Product p = productManager.getProductById(testProductId);
        p.setStock(100);
        p.setPrice(99.99);

        productManager.updateProduct(p);

        Product updated = productManager.getProductById(testProductId);
        assertEquals(100, updated.getStock(), "Stock update failed");
        assertEquals(99.99, updated.getPrice(), 0.01, "Price update failed");
        System.out.println("✓ Product Updated");
    }

    @Test
    @DisplayName("UC4 - Check Availability (Sale)")
    void test04CheckSaleAvailability() {
        // Current Stock is 100
        boolean available = productManager.checkAvailability(testProductId, ItemType.SALE, 10);
        assertTrue(available, "Should be available (10 <= 100)");

        boolean notAvailable = productManager.checkAvailability(testProductId, ItemType.SALE, 200);
        assertFalse(notAvailable, "Should not be available (200 > 100)");

        System.out.println("✓ Availability Check Passed");
    }

    @Test
    @DisplayName("UC5 - Decrement Stock")
    void test05DecrementStock() {
        // Current Stock is 100. Buying 5.
        boolean success = productManager.decrementStock(testProductId, 5);
        assertTrue(success, "Decrement operation should succeed");

        Product p = productManager.getProductById(testProductId);
        assertEquals(95, p.getStock(), "Stock should be 100 - 5 = 95");
        System.out.println("✓ Stock Decremented");
    }

    @Test
    @DisplayName("UC6 - Delete Product")
    void test06DeleteProduct() {
        productManager.deleteProduct(testProductId);

        Product p = productManager.getProductById(testProductId);
        assertNull(p, "Product should be null (deleted) after deletion");
        System.out.println("✓ Product Deleted");
    }
}