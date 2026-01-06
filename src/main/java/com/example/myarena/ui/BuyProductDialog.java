package com.example.myarena.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Dialog (Frame) pour l'achat d'un produit
 * Contient les @FXML et gère les événements UI
 * Délègue la logique métier au Controller
 */
public class BuyProductDialog {

    @FXML private Label lblProductName;
    @FXML private Label lblProductDescription;
    @FXML private Label lblUnitPrice;
    @FXML private Label lblStockAvailable;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Label lblSummaryUnitPrice;
    @FXML private Label lblSummaryQuantity;
    @FXML private Label lblTotalAmount;
    @FXML private Button btnConfirm;
    @FXML private Button btnCancel;

    private CartController controller;
    private Stage dialogStage;
    private Long productId;
    private double unitPrice;

    /**
     * Constructeur par défaut
     */
    public BuyProductDialog() {
        // Le controller sera initialisé dans initialize()
    }

    /**
     * Définit le controller à utiliser
     */
    public void setController(CartController controller) {
        this.controller = controller;
    }

    /**
     * Méthode appelée automatiquement après le chargement du FXML
     */
    @FXML
    public void initialize() {
        System.out.println("BuyProductDialog initialized");

        // Configurer le Spinner
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantitySpinner.setValueFactory(valueFactory);

        // Listener pour mettre à jour le total quand on change la quantité
        quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateTotal();
        });

        // Configurer les event handlers
        btnConfirm.setOnAction(event -> handleConfirm());
        btnCancel.setOnAction(event -> handleCancel());
    }

    /**
     * Handler pour le bouton "Confirm Purchase"
     */
    private void handleConfirm() {
        if (controller != null) {
            controller.confirmPurchaseFromDialog(productId, getQuantity(), this);
        }
    }

    /**
     * Handler pour le bouton "Cancel"
     */
    private void handleCancel() {
        closeDialog();
    }

    /**
     * Met à jour le total
     */
    private void updateTotal() {
        int quantity = quantitySpinner.getValue();
        double total = unitPrice * quantity;
        lblSummaryQuantity.setText(String.valueOf(quantity));
        lblTotalAmount.setText("$" + String.format("%.2f", total));
    }

    // ============================================
    // GETTERS pour que le Controller accède aux données
    // ============================================

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantitySpinner.getValue();
    }

    // ============================================
    // SETTERS pour que le Controller mette à jour l'UI
    // ============================================

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String name) {
        lblProductName.setText(name);
    }

    public void setProductDescription(String description) {
        lblProductDescription.setText(description);
    }

    public void setUnitPrice(double price) {
        this.unitPrice = price;
        lblUnitPrice.setText("💲 $" + String.format("%.2f", price));
        lblSummaryUnitPrice.setText("$" + String.format("%.2f", price));
        updateTotal();
    }

    public void setStockAvailable(int stock) {
        lblStockAvailable.setText(stock + " units available");

        // Mettre à jour le max du spinner
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, stock, 1);
        quantitySpinner.setValueFactory(valueFactory);
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
     * Ferme le dialog
     */
    public void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    /**
     * Méthode statique pour ouvrir le dialog
     */
    public static void showDialog(Long productId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    BuyProductDialog.class.getResource("/com/example/myarena/cart/buy-product-dialog.fxml")
            );

            Scene scene = new Scene(loader.load(), 500, 600);

            BuyProductDialog dialog = loader.getController();
            dialog.setProductId(productId);

            // Créer le CartController pour ce dialog
            CartController cartController = new CartController(null);
            dialog.setController(cartController);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Buy Product");
            dialogStage.setScene(scene);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);

            dialog.dialogStage = dialogStage;

            // Charger les données du produit
            cartController.loadProductDataForDialog(productId, dialog);

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}