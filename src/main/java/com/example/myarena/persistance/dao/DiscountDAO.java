package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Discount;
import java.util.List;

/**
 * Interface pour les opérations de persistance des codes promo
 */
public interface DiscountDAO {

    /**
     * Récupère un code promo par son ID
     * @param id ID du code promo
     * @return Le code promo trouvé, ou null si non trouvé
     */
    Discount getDiscountById(Long id);

    /**
     * Récupère un code promo par son code unique
     * @param code Le code promo
     * @return Le code promo trouvé, ou null si non trouvé
     */
    Discount getDiscountByCode(String code);

    /**
     * Récupère tous les codes promo actifs
     * @return Liste des codes promo actifs
     */
    List<Discount> getActiveDiscounts();

    /**
     * Récupère tous les codes promo
     * @return Liste de tous les codes promo
     */
    List<Discount> getAllDiscounts();

    /**
     * Sauvegarde un nouveau code promo
     * @param discount Le code promo à sauvegarder
     */
    void saveDiscount(Discount discount);

    /**
     * Met à jour un code promo existant
     * @param discount Le code promo à mettre à jour
     */
    void updateDiscount(Discount discount);

    /**
     * Supprime un code promo
     * @param id ID du code promo à supprimer
     */
    void deleteDiscount(Long id);
}