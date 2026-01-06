package com.example.myarena.ui;

import com.example.myarena.domain.CartItem;
import com.example.myarena.domain.Discount;
import com.example.myarena.domain.Order;
import com.example.myarena.domain.Product;
import com.example.myarena.domain.ItemType;
import com.example.myarena.facade.CartFacade;
import com.example.myarena.persistance.dao.DiscountDAO;
import com.example.myarena.persistance.factory.AbstractFactory;
import com.example.myarena.persistance.factory.PostgresFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller pour la vue Cart
 * Contient UNIQUEMENT la logique métier
 * USE CASE 1 : Make an Order (étapes 3-7)
 */
public class CartController {

    private CartFrame view;
    private CartFacade cartFacade;
    private DiscountDAO discountDAO;
    private Long userId;
    private Discount appliedDiscount; // Promotion appliquée
    private List<Discount> availableDiscounts; // Promotions disponibles

    /**
     * Constructeur - reçoit le Frame (view)
     */
    public CartController(CartFrame view) {
        this.view = view;
        this.cartFacade = CartFacade.getInstance();

        // Utiliser AbstractFactory pour obtenir DiscountDAO
        AbstractFactory factory = new PostgresFactory();
        this.discountDAO = factory.createDiscountDAO();

        // TODO: Récupérer le userId de la session
        this.userId = 2L; // Temporaire - à remplacer par SessionFacade.getCurrentUser().getId()
    }

    /**
     * Charge les données du panier
     * USE CASE 1 : Make an Order (étape 4)
     */
    public void loadCartData() {
        try {
            // Récupérer les articles du panier
            List<CartItem> items = cartFacade.getCartItems(userId);

            if (items == null || items.isEmpty()) {
                view.displayCartItems(new ArrayList<>(), new ArrayList<>());
                view.updateOrderSummary(0.0, 0.0);
                return;
            }

            // Récupérer les produits correspondants
            List<Product> products = new ArrayList<>();
            for (CartItem item : items) {
                Product product = cartFacade.getProductById(item.getProductId());
                products.add(product);
            }

            // Afficher dans l'UI
            view.displayCartItems(items, products);

            // Calculer et afficher les totaux
            double subtotal = cartFacade.calculateCartTotal(userId);
            double total = subtotal;

            // Appliquer la promotion si une est active
            if (appliedDiscount != null) {
                double discountAmount = (subtotal * appliedDiscount.getDiscountValue()) / 100.0;
                total = subtotal - discountAmount;
                System.out.println("Promotion applied: " + appliedDiscount.getCode() + " (-$" + String.format("%.2f", discountAmount) + ")");
            }

            view.updateOrderSummary(subtotal, total);

        } catch (Exception e) {
            view.showError("Erreur lors du chargement du panier : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Incrémente la quantité d'un article
     * USE CASE 1 : Make an Order (alternative flow A2)
     */
    public void incrementQuantity(Long cartItemId) {
        try {
            // TODO: Implémenter l'incrémentation dans CartManager
            System.out.println("Increment quantity for item: " + cartItemId);

            // Recharger le panier
            loadCartData();

        } catch (Exception e) {
            view.showError("Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Décrémente la quantité d'un article
     * USE CASE 1 : Make an Order (alternative flow A2)
     */
    public void decrementQuantity(Long cartItemId) {
        try {
            // TODO: Implémenter la décrémentation dans CartManager
            System.out.println("Decrement quantity for item: " + cartItemId);

            // Recharger le panier
            loadCartData();

        } catch (Exception e) {
            view.showError("Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Supprime un article du panier
     * USE CASE 1 : Make an Order (alternative flow A2)
     */
    public void removeItem(Long cartItemId) {
        try {
            // Demander confirmation
            boolean confirmed = view.showConfirmation("Êtes-vous sûr de vouloir supprimer cet article ?");

            if (!confirmed) {
                return;
            }

            // Supprimer l'article
            cartFacade.removeItemFromCart(cartItemId);

            view.showSuccess("Article supprimé du panier.");

            // Recharger le panier
            loadCartData();

        } catch (Exception e) {
            view.showError("Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge les promotions disponibles
     */
    public void loadPromotions() {
        try {
            // Récupérer les promotions actives depuis la base de données
            availableDiscounts = discountDAO.getActiveDiscounts();

            if (availableDiscounts == null || availableDiscounts.isEmpty()) {
                view.setPromotions(new ArrayList<>());
                return;
            }

            // Convertir en liste de strings pour l'affichage
            List<String> promotionLabels = availableDiscounts.stream()
                .map(d -> d.getCode() + " (-" + d.getDiscountValue() + "%)")
                .collect(Collectors.toList());

            view.setPromotions(promotionLabels);

            System.out.println("Loaded " + availableDiscounts.size() + " promotions");

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des promotions: " + e.getMessage());
            view.setPromotions(new ArrayList<>());
        }
    }

    /**
     * Applique une promotion
     * USE CASE 1 : Make an Order (alternative flow A5)
     */
    public void applyPromotion() {
        try {
            // Récupérer la promotion sélectionnée
            String selectedPromotion = view.getSelectedPromotion();

            if (selectedPromotion == null || selectedPromotion.isEmpty()) {
                view.showError("Please select a promotion first");
                return;
            }

            // Extraire le code promo de la sélection (format: "CODE (-XX%)")
            String promoCode = selectedPromotion.split(" ")[0];

            // Trouver la promotion correspondante
            Discount discount = availableDiscounts.stream()
                .filter(d -> d.getCode().equals(promoCode))
                .findFirst()
                .orElse(null);

            if (discount == null) {
                view.showError("Promotion not found");
                return;
            }

            // Appliquer la promotion
            appliedDiscount = discount;

            // Recharger le panier avec la promotion appliquée
            loadCartData();

            view.showSuccess("Promotion '" + discount.getCode() + "' applied! (-" + discount.getDiscountValue() + "%)");

        } catch (Exception e) {
            view.showError("Erreur lors de l'application de la promotion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Passe à la validation de la commande
     * USE CASE 1 : Make an Order (étapes 5-13)
     */
    public void proceedToCheckout() {
        try {
            // Vérifier que le panier n'est pas vide (alternative flow A1)
            if (cartFacade.isCartEmpty(userId)) {
                view.showError("Votre panier est vide. Veuillez ajouter des articles avant de passer commande.");
                return;
            }

            // Demander confirmation (étape 6)
            boolean confirmed = view.showConfirmation(
                    "Êtes-vous sûr de vouloir passer cette commande ?\n\n" +
                            "Une fois confirmée, la commande sera envoyée au propriétaire pour validation."
            );

            if (!confirmed) {
                // Alternative flow A4 - Order Cancelled by Client
                System.out.println("Order cancelled by client");
                return;
            }

            // Créer la commande (étape 8)
            Order order = cartFacade.submitOrder(userId);

            // Afficher le message de succès (étape 13)
            view.showSuccess(
                    "Commande créée avec succès !\n\n" +
                            "Numéro de référence : " + order.getReferenceNumber() + "\n" +
                            "Montant : $" + String.format("%.2f", order.getAmount()) + "\n\n" +
                            "Votre commande est en attente de validation par le propriétaire."
            );

            // Recharger le panier (maintenant vide)
            loadCartData();

            // NOTE: Les notifications sont gérées par un autre module
            // USE CASE 1 : Make an Order (étapes 10 et 11)
            // - Notification au Owner (étape 10)
            // - Notification au Client (étape 11)

        } catch (Exception e) {
            // Alternative flow A6 - Order Submission Failure
            view.showError(
                    "Erreur lors de la création de la commande : " + e.getMessage() + "\n\n" +
                            "Votre panier a été conservé. Veuillez réessayer plus tard."
            );
            e.printStackTrace();
        }
    }

    /**
     * Continue les achats (retour au catalogue)
     */
    public void continueShopping() {
        System.out.println("Continue shopping");
        // TODO: Naviguer vers le catalogue des produits
        view.closeWindow();
    }

    /**
     * Déconnexion
     */
    public void logout() {
        System.out.println("Logging out");
        // TODO: Appeler SessionFacade.logout()
        view.closeWindow();
    }

    // ============================================
    // MÉTHODES POUR BuyProductDialog
    // ============================================

    /**
     * Charge les données du produit pour le dialog d'achat
     */
    public void loadProductDataForDialog(Long productId, BuyProductDialog dialogView) {
        try {
            Product product = cartFacade.getProductById(productId);

            if (product == null) {
                dialogView.showError("Produit introuvable.");
                return;
            }

            // Vérifier que le produit est vendable
            if (!product.isSellable()) {
                dialogView.showError("Ce produit n'est pas disponible à la vente.");
                dialogView.closeDialog();
                return;
            }

            // Vérifier le stock
            if (product.getStock() <= 0) {
                dialogView.showError("Ce produit est en rupture de stock.");
                dialogView.closeDialog();
                return;
            }

            // Mettre à jour l'UI
            dialogView.setProductName(product.getName());
            dialogView.setProductDescription(product.getDescription());
            dialogView.setUnitPrice(product.getPrice());
            dialogView.setStockAvailable(product.getStock());

        } catch (Exception e) {
            dialogView.showError("Erreur lors du chargement du produit : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Confirme l'achat et ajoute au panier
     *
     * USE CASE 1 : Make an Order (étape 3 - SALE)
     */
    public void confirmPurchaseFromDialog(Long productId, int quantity, BuyProductDialog dialogView) {
        try {
            // Validation
            if (quantity < 1) {
                dialogView.showError("La quantité doit être au moins 1.");
                return;
            }

            // Vérifier la disponibilité
            if (!cartFacade.checkProductAvailability(productId, ItemType.SALE, quantity)) {
                dialogView.showError("Stock insuffisant pour cette quantité.");
                return;
            }

            // Ajouter au panier via la Facade
            cartFacade.addItemToCart(
                    userId,
                    productId,
                    quantity,
                    ItemType.SALE,
                    null,  // Pas de dates pour SALE
                    null
            );

            dialogView.showSuccess("Le produit a été ajouté au panier.");
            dialogView.closeDialog();

        } catch (Exception e) {
            dialogView.showError("Erreur lors de l'ajout au panier : " + e.getMessage());
            e.printStackTrace();
        }
    }
}