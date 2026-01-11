package com.example.myarena.ui;

import com.example.myarena.domain.Product;
import com.example.myarena.services.ProductManager;
import com.example.myarena.ui.BuyProductDialog;
import com.example.myarena.ui.RentProductDialog;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ProductCatalogController {

    @FXML private Button backButton;
    @FXML private TextField searchField;
    @FXML private TableView<Product> catalogTable;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colDesc;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Double> colRentalPrice;
    @FXML private TableColumn<Product, String> colStatus;
    @FXML private TableColumn<Product, Void> colAction;

    private final ProductManager productManager = ProductManager.getInstance();
    private FilteredList<Product> filteredData;

    @FXML
    public void initialize() {
        //Setup Navigation Logic
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

        setupTable();
        loadCatalog();

        // Search Logic
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

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnBuy = new Button("Buy");
            private final Button btnRent = new Button("Rent");
            private final HBox pane = new HBox(5, btnBuy, btnRent);

            {
                btnBuy.getStyleClass().addAll("button", "primary", "small");
                btnRent.getStyleClass().addAll("button", "secondary", "small");

                btnBuy.setOnAction(event -> {
                    Product p = getTableView().getItems().get(getIndex());
                    BuyProductDialog.showDialog(p.getId());
                });

                btnRent.setOnAction(event -> {
                    Product p = getTableView().getItems().get(getIndex());
                    RentProductDialog.showDialog(p.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Product p = getTableView().getItems().get(getIndex());
                    btnBuy.setVisible(p.isSellable());
                    btnRent.setVisible(p.isRentable());

                    // Disable if out of stock
                    if(p.getStock() <= 0) {
                        btnBuy.setDisable(true);
                        btnRent.setDisable(true);
                    }

                    setGraphic(pane);
                }
            }
        });
    }

    private void loadCatalog() {
        var list = productManager.getAllProducts();
        filteredData = new FilteredList<>(FXCollections.observableArrayList(list), p -> true);
        catalogTable.setItems(filteredData);
    }
}