package com.example.myarena.ui;

import com.example.myarena.domain.Product;
import com.example.myarena.services.ProductManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class ProductCatalogController {

    @FXML private TextField searchField;
    @FXML private TableView<Product> catalogTable;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colDesc;
    @FXML private TableColumn<Product, Double> colPrice; // Sale Price
    @FXML private TableColumn<Product, Double> colRentalPrice; // Rental Price
    @FXML private TableColumn<Product, String> colStatus;
    @FXML private TableColumn<Product, Void> colAction;

    private final ProductManager productManager = ProductManager.getInstance();
    private FilteredList<Product> filteredData;

    @FXML
    public void initialize() {
        setupTable();
        loadCatalog();

        // Search/Filter Logic
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return product.getName().toLowerCase().contains(lowerCaseFilter)
                        || product.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void setupTable() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colRentalPrice.setCellValueFactory(new PropertyValueFactory<>("rentalPricePerDay"));

        colStatus.setCellValueFactory(cellData -> {
            int stock = cellData.getValue().getStock();
            return new SimpleObjectProperty<>(stock > 0 ? "Available (" + stock + ")" : "Out of Stock");
        });

        // Add Action Buttons (Buy / Rent)
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnBuy = new Button("Buy");
            private final Button btnRent = new Button("Rent");
            private final HBox pane = new HBox(5, btnBuy, btnRent);

            {
                btnBuy.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                btnRent.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

                btnBuy.setOnAction(event -> {
                    Product p = getTableView().getItems().get(getIndex());
                    BuyProductDialog.showDialog(p.getId());
                });

                btnRent.setOnAction(event -> {
                    Product p = getTableView().getItems().get(getIndex());
                    RentProductDialog.showDialog(p.getId()); // Call the new Rent Dialog
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Product p = getTableView().getItems().get(getIndex());
                    // Hide buttons based on availability flags
                    btnBuy.setVisible(p.isSellable());
                    btnRent.setVisible(p.isRentable());
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadCatalog() {
        // Load all products for the client
        var list = productManager.getAllProducts();
        filteredData = new FilteredList<>(FXCollections.observableArrayList(list), p -> true);
        catalogTable.setItems(filteredData);
    }
}