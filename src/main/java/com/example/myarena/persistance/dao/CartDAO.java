package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Cart;
import com.example.myarena.domain.CartItem;
import com.example.myarena.domain.CartStatus;
import java.util.List;

/** Persistance des paniers */
public interface CartDAO {

    Cart getActiveCartByUserId(Long userId);

    void saveCart(Cart cart);

    void updateCart(Cart cart);

    void updateCartStatus(Long cartId, CartStatus status);

    void addCartItem(CartItem cartItem);

    void updateCartItem(CartItem cartItem);

    void deleteCartItem(Long cartItemId);

    List<CartItem> getCartItemsByCartId(Long cartId);

    void clearCart(Long cartId);
}