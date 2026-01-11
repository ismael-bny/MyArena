package com.example.myarena.ui;

import com.example.myarena.domain.CartItem;
import com.example.myarena.domain.Discount;
import com.example.myarena.domain.Order;
import com.example.myarena.domain.Product;
import com.example.myarena.domain.ItemType;
import com.example.myarena.facade.CartFacade;
import com.example.myarena.facade.SessionFacade;
import com.example.myarena.persistance.dao.DiscountDAO;
import com.example.myarena.persistance.factory.AbstractFactory;
import com.example.myarena.persistance.factory.PostgresFactory;
import com.example.myarena.services.CartManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartController {

    private CartFrame view;
    private CartFacade cartFacade;
    private CartManager cartManager;
    private DiscountDAO discountDAO;
    private SessionFacade sessionFacade;
    private Long userId;
    private Discount appliedDiscount;
    private List<Discount> availableDiscounts;

    public CartController(CartFrame view) {
        this.view = view;
        this.cartFacade = CartFacade.getInstance();
        this.cartManager = CartManager.getInstance();
        this.sessionFacade = SessionFacade.getInstance();

        // Utiliser AbstractFactory pour obtenir DiscountDAO
        AbstractFactory factory = new PostgresFactory();
        this.discountDAO = factory.createDiscountDAO();

        // Récupérer le userId de la session
        if (sessionFacade.getCurrentUser() != null) {
            this.userId = sessionFacade.getCurrentUser().getId();
        } else {
            this.userId = null;
        }
    }

    public void loadCartData() {
        // ✅ FIX: Prevent crash if view is null (e.g. when used from Dialog)
        if (view == null) return;

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
            if (view != null) view.showError("Erreur lors du chargement du panier : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void incrementQuantity(Long cartItemId) {
        try {
            List<CartItem> items = cartFacade.getCartItems(userId);
            CartItem item = items.stream()
                    .filter(ci -> ci.getId().equals(cartItemId))
                    .findFirst()
                    .orElse(null);

            if (item == null) {
                if(view != null) view.showError("Article non trouvé.");
                return;
            }

            if (!cartFacade.checkProductAvailability(item.getProductId(), item.getItemType(), item.getQuantity() + 1)) {
                if(view != null) view.showError("Stock insuffisant pour augmenter la quantité.");
                return;
            }

            cartManager.updateCartItemQuantity(cartItemId, item.getQuantity() + 1);
            loadCartData();

        } catch (Exception e) {
            if(view != null) view.showError("Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void decrementQuantity(Long cartItemId) {
        try {
            List<CartItem> items = cartFacade.getCartItems(userId);
            CartItem item = items.stream()
                    .filter(ci -> ci.getId().equals(cartItemId))
                    .findFirst()
                    .orElse(null);

            if (item == null) {
                if(view != null) view.showError("Article non trouvé.");
                return;
            }

            if (item.getQuantity() <= 1) {
                removeItem(cartItemId);
                return;
            }

            cartManager.updateCartItemQuantity(cartItemId, item.getQuantity() - 1);
            loadCartData();

        } catch (Exception e) {
            if(view != null) view.showError("Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeItem(Long cartItemId) {
        try {
            boolean confirmed = view != null && view.showConfirmation("Êtes-vous sûr de vouloir supprimer cet article ?");

            if (!confirmed) return;

            cartFacade.removeItemFromCart(cartItemId);

            if(view != null) view.showSuccess("Article supprimé du panier.");
            loadCartData();

        } catch (Exception e) {
            if(view != null) view.showError("Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadPromotions() {
        if(view == null) return;
        try {
            availableDiscounts = discountDAO.getActiveDiscounts();

            if (availableDiscounts == null || availableDiscounts.isEmpty()) {
                view.setPromotions(new ArrayList<>());
                return;
            }

            List<String> promotionLabels = availableDiscounts.stream()
                    .map(d -> d.getCode() + " (-" + d.getDiscountValue() + "%)")
                    .collect(Collectors.toList());

            view.setPromotions(promotionLabels);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des promotions: " + e.getMessage());
            view.setPromotions(new ArrayList<>());
        }
    }

    public void applyPromotion() {
        if(view == null) return;
        try {
            String selectedPromotion = view.getSelectedPromotion();

            if (selectedPromotion == null || selectedPromotion.isEmpty()) {
                view.showError("Please select a promotion first");
                return;
            }

            String promoCode = selectedPromotion.split(" ")[0];

            Discount discount = availableDiscounts.stream()
                    .filter(d -> d.getCode().equals(promoCode))
                    .findFirst()
                    .orElse(null);

            if (discount == null) {
                view.showError("Promotion not found");
                return;
            }

            appliedDiscount = discount;
            loadCartData();
            view.showSuccess("Promotion '" + discount.getCode() + "' applied! (-" + discount.getDiscountValue() + "%)");

        } catch (Exception e) {
            view.showError("Erreur lors de l'application de la promotion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void proceedToCheckout() {
        if(view == null) return;
        try {
            if (cartFacade.isCartEmpty(userId)) {
                view.showError("Votre panier est vide. Veuillez ajouter des articles avant de passer commande.");
                return;
            }

            boolean confirmed = view.showConfirmation(
                    "Êtes-vous sûr de vouloir passer cette commande ?\n\n" +
                            "Une fois confirmée, la commande sera envoyée au propriétaire pour validation."
            );

            if (!confirmed) return;

            Order order = cartFacade.submitOrder(userId);

            view.showSuccess(
                    "Commande créée avec succès !\n\n" +
                            "Numéro de référence : " + order.getReferenceNumber() + "\n" +
                            "Montant : $" + String.format("%.2f", order.getAmount()) + "\n\n" +
                            "Votre commande est en attente de validation par le propriétaire."
            );

            loadCartData();

        } catch (Exception e) {
            view.showError("Erreur lors de la création de la commande : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void continueShopping() {
        if(view != null) view.closeWindow();
    }

    public void logout() {
        sessionFacade.logout();
        if(view != null) view.closeWindow();
    }

    // ============================================
    // MÉTHODES POUR BuyProductDialog
    // ============================================

    public void loadProductDataForDialog(Long productId, BuyProductDialog dialogView) {
        try {
            Product product = cartFacade.getProductById(productId);

            if (product == null) {
                dialogView.showError("Produit introuvable.");
                return;
            }

            if (!product.isSellable()) {
                dialogView.showError("Ce produit n'est pas disponible à la vente.");
                dialogView.closeDialog();
                return;
            }

            if (product.getStock() <= 0) {
                dialogView.showError("Ce produit est en rupture de stock.");
                dialogView.closeDialog();
                return;
            }

            dialogView.setProductName(product.getName());
            dialogView.setProductDescription(product.getDescription());
            dialogView.setUnitPrice(product.getPrice());
            dialogView.setStockAvailable(product.getStock());

        } catch (Exception e) {
            dialogView.showError("Erreur lors du chargement du produit : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void confirmPurchaseFromDialog(Long productId, int quantity, BuyProductDialog dialogView) {
        try {
            if (quantity < 1) {
                dialogView.showError("La quantité doit être au moins 1.");
                return;
            }

            if (!cartFacade.checkProductAvailability(productId, ItemType.SALE, quantity)) {
                dialogView.showError("Stock insuffisant pour cette quantité.");
                return;
            }

            cartFacade.addItemToCart(
                    userId,
                    productId,
                    quantity,
                    ItemType.SALE,
                    null,
                    null
            );

            // ✅ FIX: Removed redundant loadCartData() call that caused the NPE
            dialogView.showSuccess("Le produit a été ajouté au panier.");

            navigateToCart(dialogView.getDialogStage());

        } catch (Exception e) {
            dialogView.showError("Erreur lors de l'ajout au panier : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void confirmRentalFromDialog(Long productId, Integer quantity, String rentalStart, String rentalEnd, RentProductDialog rentProductDialog) {
        try {
            if (quantity == null || quantity < 1) {
                rentProductDialog.showAlert("Erreur", "La quantité doit être au moins 1.");
                return;
            }

            if (rentalStart == null || rentalStart.isEmpty() || rentalEnd == null || rentalEnd.isEmpty()) {
                rentProductDialog.showAlert("Erreur", "Les dates de location sont requises.");
                return;
            }

            if (!cartFacade.checkProductAvailability(productId, ItemType.RENTAL, quantity)) {
                rentProductDialog.showAlert("Erreur", "Stock insuffisant pour cette quantité.");
                return;
            }

            cartFacade.addItemToCart(
                    userId,
                    productId,
                    quantity,
                    ItemType.RENTAL,
                    rentalStart,
                    rentalEnd
            );

            // ✅ FIX: Removed redundant loadCartData() call
            rentProductDialog.showAlert("Succès", "Le produit a été ajouté au panier pour location.");

            navigateToCart(rentProductDialog.getDialogStage());

        } catch (Exception e) {
            rentProductDialog.showAlert("Erreur", "Erreur lors de l'ajout au panier : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToCart(javafx.stage.Stage dialogStage) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/myarena/cart-view.fxml")
            );
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage;
            if (dialogStage != null && dialogStage.getScene() != null) {
                stage = (javafx.stage.Stage) dialogStage.getScene().getWindow();
            } else if (view != null && view.getBtnContinueShopping() != null) {
                stage = (javafx.stage.Stage) view.getBtnContinueShopping().getScene().getWindow();
            } else {
                stage = new javafx.stage.Stage();
            }

            stage.setScene(new javafx.scene.Scene(root));

            if (dialogStage != null) {
                dialogStage.close();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la navigation vers le panier : " + e.getMessage());
            e.printStackTrace();
        }
    }
}