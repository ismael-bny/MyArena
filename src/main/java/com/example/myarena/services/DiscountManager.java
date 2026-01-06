package com.example.myarena.services;

import com.example.myarena.domain.Discount;
import com.example.myarena.persistance.dao.DiscountDAO;
import com.example.myarena.persistance.dao.DiscountDAOPostgres;

import java.time.LocalDate;
import java.util.List;

/**
 * Gestionnaire de la logique métier des codes promo
 */
public class DiscountManager {

    private static DiscountManager instance;
    private final DiscountDAO discountDAO;

    private DiscountManager() {
        this.discountDAO = new DiscountDAOPostgres();
    }

    public static DiscountManager getInstance() {
        if (instance == null) {
            instance = new DiscountManager();
        }
        return instance;
    }

    /**
     * Valide un code promo
     * Vérifie que le code existe, est actif et non expiré
     */
    public boolean validateDiscount(String code) {
        Discount discount = discountDAO.getDiscountByCode(code);

        if (discount == null) {
            return false; 
        }

        if (!discount.isActive()) {
            return false;
        }

        LocalDate expirationDate = LocalDate.parse(discount.getExpirationDate());
        if (expirationDate.isBefore(LocalDate.now())) {
            return false; // Code expiré
        }

        return true;
    }

    /**
     * Applique une réduction au montant total
     */
    public double applyDiscount(String code, double amount) {
        Discount discount = discountDAO.getDiscountByCode(code);

        if (discount == null) {
            throw new RuntimeException("Code promo invalide");
        }

        // Appliquer la réduction (supposons que c'est un pourcentage)
        double discountAmount = amount * (discount.getDiscountValue() / 100.0);
        return amount - discountAmount;
    }

    /**
     * Récupère un code promo par son code
     */
    public Discount getDiscountByCode(String code) {
        return discountDAO.getDiscountByCode(code);
    }

    /**
     * Récupère tous les codes promo actifs
     */
    public List<Discount> getActiveDiscounts() {
        return discountDAO.getActiveDiscounts();
    }

    /**
     * Récupère tous les codes promo
     */
    public List<Discount> getAllDiscounts() {
        return discountDAO.getAllDiscounts();
    }

    /**
     * Sauvegarde un nouveau code promo
     */
    public void saveDiscount(Discount discount) {
        discountDAO.saveDiscount(discount);
    }

    /**
     * Met à jour un code promo
     */
    public void updateDiscount(Discount discount) {
        discountDAO.updateDiscount(discount);
    }

    /**
     * Supprime un code promo
     */
    public void deleteDiscount(Long id) {
        discountDAO.deleteDiscount(id);
    }
}