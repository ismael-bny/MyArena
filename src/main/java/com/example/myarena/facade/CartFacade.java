package com.example.myarena.facade;

import com.example.myarena.domain.Cart;
import com.example.myarena.domain.CartItem;
import com.example.myarena.domain.Order;
import com.example.myarena.domain.Product;
import com.example.myarena.domain.ItemType;
import com.example.myarena.services.CartManager;
import com.example.myarena.services.OrderManager;
import com.example.myarena.services.ProductManager;

import java.util.List;

/** Gestion du panier */
public class CartFacade {

    private static CartFacade instance;
    private final CartManager cartManager;
    private final ProductManager productManager;
    private final OrderManager orderManager;

    private CartFacade() {
        this.cartManager = CartManager.getInstance();
        this.productManager = ProductManager.getInstance();
        this.orderManager = OrderManager.getInstance();
    }

    public static CartFacade getInstance() {
        if (instance == null) {
            instance = new CartFacade();
        }
        return instance;
    }

    /** Récupère tous les produits */
    public List<Product> getAllProducts() {
        return productManager.getAllProducts();
    }

    /** Récupère un produit par ID */
    public Product getProductById(Long productId) {
        return productManager.getProductById(productId);
    }

    /** Vérifie la disponibilité d'un produit */
    public boolean checkProductAvailability(Long productId, ItemType itemType, int quantity) {
        return productManager.checkAvailability(productId, itemType, quantity);
    }

    /** Ajoute un article au panier */
    public void addItemToCart(Long userId, Long productId, int quantity, ItemType itemType,
                              String rentalStart, String rentalEnd) {
        cartManager.addItemToCart(userId, productId, quantity, itemType, rentalStart, rentalEnd);
    }

    /** Récupère le panier actif */
    public Cart getActiveCart(Long userId) {
        return cartManager.getActiveCart(userId);
    }

    /** Récupère les articles du panier */
    public List<CartItem> getCartItems(Long userId) {
        Cart cart = cartManager.getActiveCart(userId);
        if (cart == null) {
            return List.of(); 
        }
        return cartManager.getCartItems(cart.getId());
    }

    /** Supprime un article du panier */
    public void removeItemFromCart(Long cartItemId) {
        cartManager.removeItemFromCart(cartItemId);
    }

    /** Calcule le montant total */
    public double calculateCartTotal(Long userId) {
        Cart cart = cartManager.getActiveCart(userId);
        if (cart == null) {
            return 0.0;
        }
        return cartManager.calculateTotal(cart);
    }

    /** Soumet la commande */
    public Order submitOrder(Long userId) {
        Cart cart = cartManager.getActiveCart(userId);

        if (cart == null || cart.isEmpty()) {
            throw new RuntimeException("Le panier est vide. Impossible de créer une commande.");
        }

        Order order = orderManager.createOrder(cart, userId);
        cartManager.clearCart(cart.getId());
        return order;
    }

    /** Vérifie si le panier est vide */
    public boolean isCartEmpty(Long userId) {
        Cart cart = cartManager.getActiveCart(userId);
        return cart == null || cart.isEmpty();
    }
}