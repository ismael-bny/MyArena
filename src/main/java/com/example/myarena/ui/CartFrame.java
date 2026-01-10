package com.example.myarena.ui;

import com.example.myarena.domain.CartItem;
import com.example.myarena.domain.Product;
import com.example.myarena.facade.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class CartFrame {

    @FXML private Button btnBackToMenu;
    @FXML private Label lblCartItemsCount;
    @FXML private ListView<HBox> cartItemsListView;
    @FXML private VBox emptyCartMessage;

    @FXML private ComboBox<String> cmbPromotions;
    @FXML private Button btnApplyPromotion;

    @FXML private Label lblSubtotal;
    @FXML private Label lblTotal;

    @FXML private Button btnProceedToCheckout;
    @FXML private Button btnContinueShopping;

    @FXML private Label lblUsername;
    @FXML private Button btnLogout;

    private CartController controller;

    public CartFrame() {
        this.controller = new CartController(this);
    }

    @FXML
    public void initialize() {
        // Safe binding with null checks
        if(btnApplyPromotion != null) btnApplyPromotion.setOnAction(e -> handleApplyPromotion());
        if(btnProceedToCheckout != null) btnProceedToCheckout.setOnAction(e -> handleProceedToCheckout());
        if(btnContinueShopping != null) btnContinueShopping.setOnAction(e -> handleContinueShopping());
        if(btnBackToMenu != null) btnBackToMenu.setOnAction(e -> handleBackToMenu());
        if(btnLogout != null) btnLogout.setOnAction(e -> handleLogout());

        // Initial Data Load
        if (controller != null) {
            controller.loadCartData();
            controller.loadPromotions();
        }
    }

    private void handleApplyPromotion() {
        if (controller != null) controller.applyPromotion();
    }

    private void handleProceedToCheckout() {
        if (controller != null) controller.proceedToCheckout();
    }

    private void handleContinueShopping() {
        // Logic to go back to Catalog
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/myarena/product-catalog.fxml")
            );
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) btnContinueShopping.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            System.err.println("Error navigating to catalog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleBackToMenu() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/myarena/main-menu.fxml")
            );
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) btnBackToMenu.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLogout() {
        if (controller != null) controller.logout();
    }

    public void setPromotions(List<String> promotions) {
        if (cmbPromotions != null) {
            cmbPromotions.getItems().clear();
            cmbPromotions.getItems().addAll(promotions);

            if (promotions.isEmpty()) {
                cmbPromotions.setPromptText("No promotions available");
                if(btnApplyPromotion != null) btnApplyPromotion.setDisable(true);
            } else {
                cmbPromotions.setPromptText("Select a promotion");
                if(btnApplyPromotion != null) btnApplyPromotion.setDisable(false);
            }
        }
    }

    public String getSelectedPromotion() {
        return cmbPromotions != null ? cmbPromotions.getValue() : null;
    }

    public void displayCartItems(List<CartItem> items, List<Product> products) {
        if (cartItemsListView == null) return;

        cartItemsListView.getItems().clear();

        if (items == null || items.isEmpty()) {
            if(emptyCartMessage != null) emptyCartMessage.setVisible(true);
            cartItemsListView.setVisible(false);
            if(lblCartItemsCount != null) lblCartItemsCount.setText("Cart Items (0)");
            return;
        }

        if(emptyCartMessage != null) emptyCartMessage.setVisible(false);
        cartItemsListView.setVisible(true);
        if(lblCartItemsCount != null) lblCartItemsCount.setText("Cart Items (" + items.size() + ")");

        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Product product = products.get(i);
            cartItemsListView.getItems().add(createCartItemBox(item, product));
        }
    }

    private HBox createCartItemBox(CartItem item, Product product) {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(10));

        // Product Icon
        Label icon = new Label("📦");
        icon.setStyle("-fx-font-size: 24px;");

        // Name & Badge
        VBox nameBox = new VBox(5);
        Label name = new Label(product != null ? product.getName() : "Unknown");
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label badge = new Label(item.getItemType().toString());
        badge.setStyle("-fx-background-color: #e0f2f1; -fx-text-fill: #00695c; -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 10px;");

        nameBox.getChildren().addAll(name, badge);
        HBox.setHgrow(nameBox, Priority.ALWAYS);

        // Quantity Controls
        Button btnMinus = new Button("-");
        btnMinus.setOnAction(e -> controller.decrementQuantity(item.getId()));

        Label quantity = new Label(String.valueOf(item.getQuantity()));
        quantity.setStyle("-fx-font-weight: bold; -fx-padding: 0 5;");

        Button btnPlus = new Button("+");
        btnPlus.setOnAction(e -> controller.incrementQuantity(item.getId()));

        // Price
        Label price = new Label("$" + String.format("%.2f", item.getSubtotal()));
        price.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Delete
        Button btnDelete = new Button("✕");
        btnDelete.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-font-weight: bold;");
        btnDelete.setOnAction(e -> controller.removeItem(item.getId()));

        container.getChildren().addAll(icon, nameBox, btnMinus, quantity, btnPlus, price, btnDelete);
        return container;
    }

    public void updateOrderSummary(double subtotal, double total) {
        if(lblSubtotal != null) lblSubtotal.setText("$" + String.format("%.2f", subtotal));
        if(lblTotal != null) lblTotal.setText("$" + String.format("%.2f", total));
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    public boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    public void closeWindow() {
        if(btnBackToMenu != null) {
            Stage stage = (Stage) btnBackToMenu.getScene().getWindow();
            stage.close();
        }
    }
}