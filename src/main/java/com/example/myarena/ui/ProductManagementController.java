package com.example.myarena.ui;

import com.example.myarena.domain.Product;
import com.example.myarena.facade.UserSession;
import com.example.myarena.services.ProductManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ProductManagementController {

    @FXML Button backButton;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Long> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colType; // Derived from boolean flags
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;

    // Form Fields
    @FXML private TextField nameField;
    @FXML private TextArea descField;
    @FXML private TextField priceField;
    @FXML private TextField rentalPriceField;
    @FXML private TextField stockField;
    @FXML private CheckBox sellableCheck;
    @FXML private CheckBox rentableCheck;

    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    private final ProductManager productManager = ProductManager.getInstance();
    private Product selectedProduct; // Tracks the product currently being edited

    @FXML
    public void initialize() {
        setupTable();
        loadProducts();

        // Listen for table selection
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        saveButton.setOnAction(e -> handleSave());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> clearForm());

        backButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/main-menu.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Custom column to show "Sale", "Rental", or "Both"
        colType.setCellValueFactory(cellData -> {
            Product p = cellData.getValue();
            String type = "";
            if (p.isSellable()) type += "Sale ";
            if (p.isRentable()) type += "Rental";
            return new SimpleStringProperty(type.trim());
        });
    }

    private void loadProducts() {
        //we fetch all or filter by owner
        Long ownerId = UserSession.getInstance().getUser().getId();
        productTable.setItems(FXCollections.observableArrayList(productManager.getProductsByOwnerId(ownerId)));
    }

    private void populateForm(Product p) {
        selectedProduct = p;
        nameField.setText(p.getName());
        descField.setText(p.getDescription());
        priceField.setText(String.valueOf(p.getPrice()));
        rentalPriceField.setText(String.valueOf(p.getRentalPricePerDay()));
        stockField.setText(String.valueOf(p.getStock()));
        sellableCheck.setSelected(p.isSellable());
        rentableCheck.setSelected(p.isRentable());

        saveButton.setText("Update Product");
        deleteButton.setDisable(false);
    }

    private void clearForm() {
        selectedProduct = null;
        nameField.clear();
        descField.clear();
        priceField.clear();
        rentalPriceField.clear();
        stockField.clear();
        sellableCheck.setSelected(false);
        rentableCheck.setSelected(false);

        saveButton.setText("Create Product");
        deleteButton.setDisable(true);
        productTable.getSelectionModel().clearSelection();
    }

    private void handleSave() {
        try {
            // Validation
            if (nameField.getText().isEmpty() || stockField.getText().isEmpty()) {
                showAlert("Validation Error", "Name and Stock are required.");
                return;
            }

            // Create or Update Object
            Product p = (selectedProduct != null) ? selectedProduct : new Product();
            p.setOwnerId(UserSession.getInstance().getUser().getId());
            p.setName(nameField.getText());
            p.setDescription(descField.getText());
            p.setPrice(parseDoubleSafe(priceField.getText()));
            p.setRentalPricePerDay(parseDoubleSafe(rentalPriceField.getText()));
            p.setStock(Integer.parseInt(stockField.getText()));
            p.setSellable(sellableCheck.isSelected());
            p.setRentable(rentableCheck.isSelected());

            if (selectedProduct == null) {
                productManager.saveProduct(p);
                showAlert("Success", "Product Created!");
            } else {
                productManager.updateProduct(p);
                showAlert("Success", "Product Updated!");
            }

            clearForm();
            loadProducts(); // Refresh list

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for Price and Stock.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Operation failed: " + e.getMessage());
        }
    }

    private void handleDelete() {
        if (selectedProduct == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selectedProduct.getName() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            productManager.deleteProduct(selectedProduct.getId());
            clearForm();
            loadProducts();
        }
    }

    private double parseDoubleSafe(String val) {
        if (val == null || val.trim().isEmpty()) return 0.0;
        return Double.parseDouble(val);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}