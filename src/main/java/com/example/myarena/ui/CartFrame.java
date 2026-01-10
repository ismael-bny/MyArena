package com.example.myarena.ui;

import com.example.myarena.domain.CartItem;
import com.example.myarena.domain.Product;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * Frame pour afficher le panier (Shopping Cart)
 * Contient les @FXML et gère les événements UI
 * Délègue la logique métier au Controller
 */
public class CartFrame {

    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label lblUsername;
    @FXML private Button btnLogout;
    @FXML private Label lblCartItemsCount;
    @FXML private ListView<HBox> cartItemsListView;
    @FXML private VBox emptyCartMessage;
    @FXML private ComboBox<String> cmbPromotions; 
    @FXML private Button btnApplyPromotion;
    @FXML private Label lblPromotionStatus;
    @FXML private Label lblSubtotal;
    @FXML private Label lblTotal;
    @FXML private Button btnProceedToCheckout;
    @FXML private Button btnContinueShopping;
    @FXML private Button btnBackToMenu;

    private CartController controller;

    /**
     * Constructeur - crée le Controller et se passe lui-même
     */
    public CartFrame() {
        this.controller = new CartController(this);
    }

    /**
     * Méthode appelée automatiquement après le chargement du FXML
     */
    @FXML
    public void initialize() {
        System.out.println("CartFrame initialized");

        // Configurer les event handlers
        btnApplyPromotion.setOnAction(event -> handleApplyPromotion());
        btnProceedToCheckout.setOnAction(event -> handleProceedToCheckout());
        btnContinueShopping.setOnAction(event -> handleContinueShopping());
        btnBackToMenu.setOnAction(event -> handleBackToMenu());

        // Charger les données
        if (controller != null) {
            controller.loadCartData();
            controller.loadPromotions();
        }
    }

    /**
     * Handler pour "Apply Promotion"
     */
    private void handleApplyPromotion() {
        if (controller != null) {
            controller.applyPromotion();
        }
    }

    /**
     * Handler pour "Proceed to Checkout"
     * USE CASE 1 : Make an Order (étape 5)
     */
    private void handleProceedToCheckout() {
        if (controller != null) {
            controller.proceedToCheckout();
        }
    }

    /**
     * Handler pour "Continue Shopping"
     */
    private void handleContinueShopping() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/com/example/myarena/equipment-catalog.fxml")
            );
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) btnContinueShopping.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur navigation vers catalogue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handler pour "Back to Menu"
     */
    private void handleBackToMenu() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/com/example/myarena/main-menu.fxml")
            );
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) btnBackToMenu.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur navigation vers menu principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handler pour "Logout"
     */
    private void handleLogout() {
        if (controller != null) {
            controller.logout();
        }
    }

    // ============================================
    // SETTERS pour que le Controller mette à jour l'UI
    // ============================================

    public void setUsername(String username) {
        lblUsername.setText(username);
    }

    /**
     * Remplit le ComboBox avec les promotions disponibles
     */
    public void setPromotions(List<String> promotions) {
        if (cmbPromotions != null) {
            cmbPromotions.getItems().clear();
            cmbPromotions.getItems().addAll(promotions);

            if (promotions.isEmpty()) {
                cmbPromotions.setPromptText("No promotions available");
                btnApplyPromotion.setDisable(true);
            } else {
                cmbPromotions.setPromptText("Select a promotion");
                btnApplyPromotion.setDisable(false);
            }
        }
    }

    /**
     * Récupère la promotion sélectionnée
     */
    public String getSelectedPromotion() {
        return cmbPromotions != null ? cmbPromotions.getValue() : null;
    }

    /**
     * Affiche les articles du panier
     * USE CASE 1 : Make an Order (étape 4)
     */
    public void displayCartItems(List<CartItem> items, List<Product> products) {
        cartItemsListView.getItems().clear();

        if (items == null || items.isEmpty()) {
            // Afficher le message "panier vide"
            emptyCartMessage.setVisible(true);
            emptyCartMessage.setManaged(true);
            cartItemsListView.setVisible(false);
            cartItemsListView.setManaged(false);
            lblCartItemsCount.setText("Cart Items (0)");
            return;
        }

        // Masquer le message "panier vide"
        emptyCartMessage.setVisible(false);
        emptyCartMessage.setManaged(false);
        cartItemsListView.setVisible(true);
        cartItemsListView.setManaged(true);
        lblCartItemsCount.setText("Cart Items (" + items.size() + ")");

        // Créer un HBox pour chaque article
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Product product = products.get(i);

            HBox itemBox = createCartItemBox(item, product);
            cartItemsListView.getItems().add(itemBox);
        }
    }

    /**
     * Crée un HBox pour afficher un article du panier
     */
    private HBox createCartItemBox(CartItem item, Product product) {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");

        // Icône produit
        Label icon = new Label("📦");
        icon.setStyle("-fx-font-size: 32px;");

        // Nom + Badge
        VBox nameBox = new VBox(5);
        Label name = new Label(product.getName());
        name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label badge = new Label(item.getItemType().toString());
        badge.setStyle("-fx-background-color: #FFE0B2; -fx-text-fill: #FF9800; -fx-padding: 3 8 3 8; -fx-background-radius: 10; -fx-font-size: 11px;");

        nameBox.getChildren().addAll(name, badge);
        HBox.setHgrow(nameBox, Priority.ALWAYS);

        // Contrôles de quantité
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER);

        Button btnMinus = new Button("−");
        btnMinus.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 5 10 5 10; -fx-background-radius: 3; -fx-cursor: hand;");
        btnMinus.setOnAction(e -> controller.decrementQuantity(item.getId()));

        Label quantity = new Label(String.valueOf(item.getQuantity()));
        quantity.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-min-width: 30; -fx-alignment: center;");

        Button btnPlus = new Button("+");
        btnPlus.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 5 10 5 10; -fx-background-radius: 3; -fx-cursor: hand;");
        btnPlus.setOnAction(e -> controller.incrementQuantity(item.getId()));

        quantityBox.getChildren().addAll(btnMinus, quantity, btnPlus);

        // Prix
        Label price = new Label("$" + String.format("%.2f", item.getSubtotal()));
        price.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FF9800;");

        // Bouton supprimer
        Button btnDelete = new Button("🗑️");
        btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #F44336; -fx-font-size: 18px; -fx-cursor: hand; -fx-border-width: 0;");
        btnDelete.setOnAction(e -> controller.removeItem(item.getId()));

        container.getChildren().addAll(icon, nameBox, quantityBox, price, btnDelete);
        return container;
    }

    /**
     * Met à jour le résumé (Subtotal + Total)
     */
    public void updateOrderSummary(double subtotal, double total) {
        lblSubtotal.setText("$" + String.format("%.2f", subtotal));
        lblTotal.setText("$" + String.format("%.2f", total));
    }

    /**
     * Affiche un message d'erreur
     */
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche un message de succès
     */
    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une confirmation
     * USE CASE 1 : Make an Order (étape 6)
     */
    public boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().get() == ButtonType.OK;
    }

    /**
     * Ferme la fenêtre actuelle
     */
    public void closeWindow() {
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Ouvre le panier dans une nouvelle fenêtre
     */
    public static void showCart(Long userId) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                CartFrame.class.getResource("/com/example/myarena/cart-view.fxml")
            );
            javafx.scene.Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Mon Panier");
            stage.setScene(new javafx.scene.Scene(root, 900, 700));
            stage.show();

            // Charger les données du panier
            CartFrame frame = loader.getController();
            if (frame != null && frame.controller != null) {
                frame.controller.loadCartData();
            }
        } catch (Exception e) {
            System.err.println("Erreur ouverture panier: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

