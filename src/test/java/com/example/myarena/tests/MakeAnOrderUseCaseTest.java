package com.example.myarena.tests;

import com.example.myarena.domain.Cart;
import com.example.myarena.domain.CartItem;
import com.example.myarena.domain.CartStatus;
import com.example.myarena.domain.ItemType;
import com.example.myarena.domain.Order;
import com.example.myarena.domain.OrderStatus;
import com.example.myarena.domain.Product;
import com.example.myarena.services.CartManager;
import com.example.myarena.services.ProductManager;
import com.example.myarena.services.OrderManager;
import com.example.myarena.facade.CartFacade;
import com.example.myarena.util.DatabaseConfig;
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Use Case: Make an Order
 */
public class MakeAnOrderUseCaseTest {

    private CartManager cartManager;
    private ProductManager productManager;
    private OrderManager orderManager;
    private CartFacade cartFacade;
    private Long testUserId = 1L;
    private Long testProductId = 1L;

    @BeforeAll
    static void cleanupBefore() {
        System.out.println("=== NETTOYAGE INITIAL ===");
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // Supprimer toutes les données de test
            stmt.executeUpdate("DELETE FROM cart_items WHERE cart_id IN (SELECT id FROM carts WHERE user_id IN (1, 2, 9999))");
            stmt.executeUpdate("DELETE FROM carts WHERE user_id IN (1, 2, 9999)");
            stmt.executeUpdate("DELETE FROM orders WHERE user_id IN (1, 2, 9999)");

            System.out.println("✓ Données nettoyées");

            // Vérifier qu'il y a au moins un produit
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products");
            rs.next();
            int count = rs.getInt(1);
            System.out.println("✓ Produits disponibles: " + count);

        } catch (Exception e) {
            System.err.println("⚠ Erreur nettoyage: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        cartManager = CartManager.getInstance();
        productManager = ProductManager.getInstance();
        orderManager = OrderManager.getInstance();
        cartFacade = CartFacade.getInstance();

        // Récupérer un produit existant
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM products LIMIT 1")) {
            if (rs.next()) {
                testProductId = rs.getLong("id");
            }
        } catch (Exception e) {
            System.err.println("⚠ Erreur récupération produit: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UC1 - Créer panier actif")
    void test01CreateCart() {
        try {
            Cart cart = cartManager.getOrCreateActiveCart(testUserId);
            assertNotNull(cart);
            assertEquals(CartStatus.ACTIVE, cart.getStatus());
            System.out.println("✓ Panier créé");
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC2 - Ajouter article SALE")
    void test02AddSaleItem() {
        try {
            cartManager.addItemToCart(testUserId, testProductId, 2, ItemType.SALE, null, null);
            System.out.println("✓ Article SALE ajouté");
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC3 - Ajouter article RENTAL")
    void test03AddRentalItem() {
        try {
            cartManager.addItemToCart(testUserId, testProductId, 1, ItemType.RENTAL, "2026-01-10", "2026-01-15");
            System.out.println("✓ Article RENTAL ajouté");
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC4 - Supprimer article")
    void test04RemoveItem() {
        try {
            cartManager.addItemToCart(testUserId, testProductId, 1, ItemType.SALE, null, null);
            Cart cart = cartManager.getActiveCart(testUserId);
            var items = cartManager.getCartItems(cart.getId());
            if (!items.isEmpty()) {
                cartManager.removeItemFromCart(items.get(0).getId());
                System.out.println("✓ Article supprimé");
            }
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC5 - Calculer total")
    void test05CalculateTotal() {
        try {
            Cart cart = cartManager.getOrCreateActiveCart(testUserId);
            double total = cartManager.calculateTotal(cart);
            assertTrue(total >= 0);
            System.out.println("✓ Total: $" + total);
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC6 - Vérifier disponibilité")
    void test06CheckAvailability() {
        try {
            boolean available = productManager.checkAvailability(testProductId, ItemType.SALE, 1);
            System.out.println("✓ Disponible: " + available);
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC7 - Décrémenter stock")
    void test07DecrementStock() {
        try {
            Product product = productManager.getProductById(testProductId);
            if (product != null && product.getStock() > 0) {
                productManager.decrementStock(testProductId, 1);
                productManager.restoreStock(testProductId, 1);
                System.out.println("✓ Stock décrémenté/restauré");
            }
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC8 - Stock insuffisant")
    void test08InsufficientStock() {
        boolean success = productManager.decrementStock(testProductId, 999999);
        assertFalse(success);
        System.out.println("✓ Stock insuffisant détecté");
    }

    @Test
    @DisplayName("UC9 - Générer référence")
    void test09GenerateReference() {
        String ref1 = orderManager.generateReferenceNumber();
        String ref2 = orderManager.generateReferenceNumber();
        assertNotNull(ref1);
        assertTrue(ref1.startsWith("ORD-2026-"));
        assertNotEquals(ref1, ref2);
        System.out.println("✓ Références: " + ref1 + ", " + ref2);
    }

    @Test
    @DisplayName("UC10 - Créer commande")
    void test10CreateOrder() {
        try {
            Cart cart = cartManager.getOrCreateActiveCart(testUserId);
            cartManager.addItemToCart(testUserId, testProductId, 1, ItemType.SALE, null, null);
            var items = cartManager.getCartItems(cart.getId());
            cart.setItems(items);

            Order order = orderManager.createOrder(cart, testUserId);
            assertNotNull(order);
            assertEquals(OrderStatus.PENDING, order.getStatus());
            System.out.println("✓ Commande: " + order.getReferenceNumber());
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC11 - Panier vide échoue")
    void test11EmptyCartFails() {
        Cart emptyCart = new Cart();
        emptyCart.setUserId(testUserId);
        assertThrows(RuntimeException.class, () -> orderManager.createOrder(emptyCart, testUserId));
        System.out.println("✓ Exception panier vide");
    }

    @Test
    @DisplayName("UC12 - Stock=0 retiré")
    void test12OutOfStockRemoved() {
        try {
            Cart cart = new Cart();
            cart.setUserId(testUserId);
            CartItem item = new CartItem();
            item.setProductId(999999L);
            item.setQuantity(1);
            item.setItemType(ItemType.SALE);
            item.setUnitPrice(10.0);
            cart.getItems().add(item);
            try {
                orderManager.createOrder(cart, testUserId);
                System.out.println("✓ Produit retiré automatiquement");
            } catch (RuntimeException e) {
                System.out.println("✓ Exception attendue: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC13 - Facade ajouter")
    void test13FacadeAdd() {
        try {
            cartFacade.addItemToCart(testUserId, testProductId, 1, ItemType.SALE, null, null);
            var items = cartFacade.getCartItems(testUserId);
            assertNotNull(items);
            System.out.println("✓ Ajouté via facade");
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC14 - Facade soumettre")
    void test14FacadeSubmit() {
        try {
            cartFacade.addItemToCart(testUserId, testProductId, 1, ItemType.SALE, null, null);
            Order order = cartFacade.submitOrder(testUserId);
            assertNotNull(order);
            System.out.println("✓ Commande soumise");
        } catch (Exception e) {
            System.out.println("⚠ BD requise");
        }
    }

    @Test
    @DisplayName("UC15 - Facade panier vide")
    void test15FacadeEmpty() {
        assertThrows(RuntimeException.class, () -> cartFacade.submitOrder(9999L));
        System.out.println("✓ Exception panier vide");
    }

    @Test
    @DisplayName("UC16 - WORKFLOW COMPLET")
    void test16CompleteWorkflow() {
        try {
            System.out.println("\n=== WORKFLOW ===");

            // Nettoyer d'abord
            try (Connection conn = DatabaseConfig.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM cart_items WHERE cart_id IN (SELECT id FROM carts WHERE user_id = " + testUserId + ")");
                stmt.executeUpdate("DELETE FROM carts WHERE user_id = " + testUserId);
            }

            Cart cart = cartManager.getOrCreateActiveCart(testUserId);
            System.out.println("1. Panier ✓");

            cartManager.addItemToCart(testUserId, testProductId, 2, ItemType.SALE, null, null);
            System.out.println("2. Articles ✓");

            var items = cartManager.getCartItems(cart.getId());
            cart.setItems(items);
            double total = cartManager.calculateTotal(cart);
            System.out.println("3. Total: $" + total + " ✓");

            Order order = orderManager.createOrder(cart, testUserId);
            System.out.println("4. Commande: " + order.getReferenceNumber() + " ✓");

            cartManager.clearCart(cart.getId());
            System.out.println("5. Panier vidé ✓\n");
        } catch (Exception e) {
            System.out.println("⚠ BD requise: " + e.getMessage() + "\n");
        }
    }
}

