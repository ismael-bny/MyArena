package com.example.myarena.services;

import com.example.myarena.domain.Cart;
import com.example.myarena.domain.CartItem;
import com.example.myarena.domain.Product;
import com.example.myarena.domain.CartStatus;
import com.example.myarena.domain.ItemType;
import com.example.myarena.persistance.dao.CartDAO;
import com.example.myarena.persistance.dao.CartDAOPostgres;

import java.util.List;

/** Logique métier des paniers */
public class CartManager {

    private static CartManager instance;
    private final CartDAO cartDAO;
    private final ProductManager productManager;

    private CartManager() {
        this.cartDAO = new CartDAOPostgres();
        this.productManager = ProductManager.getInstance();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    /** Récupère ou crée le panier actif */
    public Cart getOrCreateActiveCart(Long userId) {
        Cart cart = cartDAO.getActiveCartByUserId(userId);

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setStatus(CartStatus.ACTIVE);
            cartDAO.saveCart(cart);
        }

        return cart;
    }

    /** Ajoute un article au panier */
    public void addItemToCart(Long userId, Long productId, int quantity, ItemType itemType,
                              String rentalStart, String rentalEnd) {

        if (!productManager.checkAvailability(productId, itemType, quantity)) {
            throw new RuntimeException("Produit non disponible");
        }

        Cart cart = getOrCreateActiveCart(userId);
        Product product = productManager.getProductById(productId);

        double unitPrice;
        if (itemType == ItemType.SALE) {
            unitPrice = product.getPrice();
        } else {
            unitPrice = product.getRentalPricePerDay();
        }

        CartItem item = new CartItem();
        item.setCartId(cart.getId());
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setItemType(itemType);
        item.setRentalStart(rentalStart);
        item.setRentalEnd(rentalEnd);
        item.setUnitPrice(unitPrice);

        cartDAO.addCartItem(item);
    }

    /** Supprime un article du panier */
    public void removeItemFromCart(Long cartItemId) {
        cartDAO.deleteCartItem(cartItemId);
    }

    /** Met à jour la quantité d'un article */
    public void updateCartItemQuantity(Long cartItemId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }

        CartItem item = new CartItem();
        item.setId(cartItemId);
        item.setQuantity(newQuantity);
        cartDAO.updateCartItem(item);
    }

    /** Calcule le montant total */
    public double calculateTotal(Cart cart) {
        return cart.getTotalAmount();
    }

    /** Vide le panier */
    public void clearCart(Long cartId) {
        cartDAO.updateCartStatus(cartId, CartStatus.CHECKED_OUT);
    }

    /** Récupère le panier actif */
    public Cart getActiveCart(Long userId) {
        return cartDAO.getActiveCartByUserId(userId);
    }

    /** Récupère les articles d'un panier */
    public List<CartItem> getCartItems(Long cartId) {
        return cartDAO.getCartItemsByCartId(cartId);
    }
}