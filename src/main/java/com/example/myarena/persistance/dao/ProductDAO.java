package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Product;
import java.util.List;

/**
 * Interface pour les opérations de persistance des produits
 */
public interface ProductDAO {

    /**
     * Récupère un produit par son ID
     * @param id ID du produit
     * @return Le produit trouvé, ou null si non trouvé
     */
    Product getProductById(Long id);

    /**
     * Récupère tous les produits disponibles
     * @return Liste de tous les produits
     */
    List<Product> getAllProducts();

    /**
     * Récupère tous les produits d'un propriétaire spécifique
     * @param ownerId ID du propriétaire
     * @return Liste des produits du propriétaire
     */
    List<Product> getProductsByOwnerId(Long ownerId);

    /**
     * Sauvegarde un nouveau produit
     * @param product Le produit à sauvegarder
     */
    void saveProduct(Product product);

    /**
     * Met à jour un produit existant
     * @param product Le produit à mettre à jour
     */
    void updateProduct(Product product);

    /**
     * Supprime un produit
     * @param id ID du produit à supprimer
     */
    void deleteProduct(Long id);

    /**
     * Met à jour le stock d'un produit
     * @param productId ID du produit
     * @param newStock Nouveau stock
     */
    void updateStock(Long productId, int newStock);
}