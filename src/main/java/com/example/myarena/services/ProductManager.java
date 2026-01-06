package com.example.myarena.services;

import com.example.myarena.domain.Product;
import com.example.myarena.domain.ItemType;
import com.example.myarena.persistance.dao.ProductDAO;
import com.example.myarena.persistance.dao.ProductDAOPostgres;

import java.util.List;

/**
 * Gestionnaire de la logique métier des produits
 * Utilisé par les 2 use cases : Make an Order + Validate Payment
 */
public class ProductManager {

    private static ProductManager instance;
    private final ProductDAO productDAO;

    private ProductManager() {
        this.productDAO = new ProductDAOPostgres();
    }

    public static ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    /**
     * Récupère un produit par son ID
     */
    public Product getProductById(Long id) {
        return productDAO.getProductById(id);
    }

    /**
     * Récupère tous les produits disponibles
     */
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    /**
     * Récupère les produits d'un propriétaire
     */
    public List<Product> getProductsByOwnerId(Long ownerId) {
        return productDAO.getProductsByOwnerId(ownerId);
    }

    /**
     * Vérifie la disponibilité d'un produit
     * Pour SALE : vérifie isSellable == true et stock > 0
     * Pour RENTAL : vérifie isRentable == true et stock > 0
     *
     * USE CASE 1 : Make an Order (étape 2)
     */
    public boolean checkAvailability(Long productId, ItemType itemType, int quantity) {
        Product product = productDAO.getProductById(productId);

        if (product == null) {
            return false;
        }

        if (product.getStock() < quantity) {
            return false;
        }

        if (itemType == ItemType.SALE && !product.isSellable()) {
            return false;
        }

        if (itemType == ItemType.RENTAL && !product.isRentable()) {
            return false;
        }

        return true;
    }

    /**
     * Décrémente le stock d'un produit (pour SALE ou RENTAL)
     *
     * USE CASE 1 : Make an Order (lors de la création de la commande)
     * @return true si le stock a été décrémenté avec succès, false si stock insuffisant
     */
    public boolean decrementStock(Long productId, int quantity) {
        Product product = productDAO.getProductById(productId);

        if (product == null) {
            return false;
        }

        int newStock = product.getStock() - quantity;

        if (newStock < 0) {
            return false;  // Stock insuffisant, retourne false au lieu de lever une exception
        }

        productDAO.updateStock(productId, newStock);
        return true;
    }

    /**
     * Restaure le stock d'un produit (pour RENTAL uniquement)
     * Utilisé quand la période de location se termine
     *
     * USE CASE 2 : Validate Payment (restauration automatique du stock)
     */
    public void restoreStock(Long productId, int quantity) {
        Product product = productDAO.getProductById(productId);

        if (product == null) {
            throw new RuntimeException("Produit introuvable avec id=" + productId);
        }

        int newStock = product.getStock() + quantity;
        productDAO.updateStock(productId, newStock);
    }

    /**
     * Sauvegarde un nouveau produit
     */
    public void saveProduct(Product product) {
        productDAO.saveProduct(product);
    }

    /**
     * Met à jour un produit
     */
    public void updateProduct(Product product) {
        productDAO.updateProduct(product);
    }

    /**
     * Supprime un produit
     */
    public void deleteProduct(Long id) {
        productDAO.deleteProduct(id);
    }
}