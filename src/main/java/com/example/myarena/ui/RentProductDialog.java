package com.example.myarena.ui;

import com.example.myarena.domain.Product;
import com.example.myarena.services.ProductManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.io.IOException;

public class RentProductDialog {

    @FXML private Label lblProductName;
    @FXML private Label lblDailyPrice;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Label lblTotalDays;
    @FXML private Label lblTotalPrice;
    @FXML private Button btnConfirm;
    @FXML private Button btnCancel;

    private Stage dialogStage;
    private Product product;
    private CartController cartController; // Reusing Cart Logic to add items

    public void setProduct(Product product) {
        this.product = product;
        lblProductName.setText(product.getName());
        lblDailyPrice.setText("$" + product.getRentalPricePerDay() + " / day");

        // Setup Quantity Spinner based on stock
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, product.getStock(), 1);
        quantitySpinner.setValueFactory(valueFactory);
    }

    @FXML
    public void initialize() {
        cartController = new CartController(null); // Helper for backend calls

        // Update calculations when dates change
        startDatePicker.valueProperty().addListener(e -> calculateTotal());
        endDatePicker.valueProperty().addListener(e -> calculateTotal());
        quantitySpinner.valueProperty().addListener(e -> calculateTotal());

        btnConfirm.setOnAction(e -> handleConfirm());
        btnCancel.setOnAction(e -> dialogStage.close());
    }

    private void calculateTotal() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) return;

        long days = ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
        if (days < 1) days = 0; // Invalid range handle

        int qty = quantitySpinner.getValue();
        double total = days * product.getRentalPricePerDay() * qty;

        lblTotalDays.setText(days + " days");
        lblTotalPrice.setText("$" + String.format("%.2f", total));
    }

    private void handleConfirm() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null || !end.isAfter(start)) {
            showAlert("Invalid Dates", "Please select valid start and end dates.");
            return;
        }

        // Delegate to CartController to add RENTAL item
        // Note: You might need to add a specific method in CartController for rentals
        // or use the generic logic passing ItemType.RENTAL
        cartController.confirmRentalFromDialog(
                product.getId(),
                quantitySpinner.getValue(),
                start.toString(),
                end.toString(),
                this
        );

        dialogStage.close();
    }

    public void setDialogStage(Stage stage) { this.dialogStage = stage; }

    public Stage getDialogStage() { return dialogStage; }

    public void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Static helper to launch
    public static void showDialog(Long productId) {
        try {
            FXMLLoader loader = new FXMLLoader(RentProductDialog.class.getResource("/com/example/myarena/rent-product-dialog.fxml"));
            Scene scene = new Scene(loader.load());

            RentProductDialog controller = loader.getController();
            controller.setDialogStage(new Stage());

            // Load Product Data
            Product p = ProductManager.getInstance().getProductById(productId);
            controller.setProduct(p);

            Stage stage = controller.dialogStage;
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }
}