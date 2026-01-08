package com.example.myarena.ui;

import com.example.myarena.domain.Terrain;
import com.example.myarena.domain.TerrainType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class TerrainManagementFrame {

    @FXML
    private Label messageLabel;

    @FXML
    private Button addButton;

    @FXML
    private Button backButton;

    @FXML
    private FlowPane cardsContainer;

    private final TerrainController controller;

    public TerrainManagementFrame() {
        this.controller = new TerrainController(this);
    }

    @FXML
    public void initialize() {
        addButton.setOnAction(e -> controller.onAddTerrain());
        controller.loadTerrains(); // charge la liste au démarrage

        if (backButton != null) {
            backButton.setOnAction(this::goBack);
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            // Load the Main Menu
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Create the scene
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Méthodes appelées par le controller =====

    public void showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setStyle(
                success
                        ? "-fx-text-fill: #16a34a;"  // vert succès
                        : "-fx-text-fill: #dc2626;"  // rouge erreur
        );
    }

    /** Affiche toutes les cartes terrain. */
    public void showTerrains(List<Terrain> terrains) {
        cardsContainer.getChildren().clear();
        for (Terrain t : terrains) {
            VBox card = buildCardForTerrain(t);
            cardsContainer.getChildren().add(card);
        }
    }

    /** Construit une card visuellement proche du Figma (liste des terrains). */
    private VBox buildCardForTerrain(Terrain t) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setSpacing(8);
        card.setPrefWidth(420);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 16;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 12, 0, 0, 4);"
        );

        // === Ligne du haut : nom + type pill + icônes à droite ===
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(t.getName());
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label typeLabel = new Label(t.getType() != null ? t.getType().name() : "Unknown");
        typeLabel.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-text-fill: #329aff;" +
                        "-fx-background-color: #e0f0ff;" +
                        "-fx-padding: 3 10;" +
                        "-fx-background-radius: 999;"
        );

        HBox leftTitle = new HBox(8, nameLabel, typeLabel);
        leftTitle.setAlignment(Pos.CENTER_LEFT);

        // Icônes à droite (vue / edit / delete)
        HBox rightIcons = new HBox(12);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);

        Button viewBtn = new Button("\uD83D\uDC41"); // 👁
        viewBtn.setOnAction(e -> controller.onViewTerrain(t));
        styleIconButton(viewBtn, "#6b7280");

        Button editBtn = new Button("\u270E"); // ✎
        editBtn.setOnAction(e -> controller.onEditTerrain(t));
        styleIconButton(editBtn, "#329aff");

        Button deleteBtn = new Button("\uD83D\uDDD1"); // 🗑
        deleteBtn.setOnAction(e -> controller.onDeleteTerrain(t));
        styleIconButton(deleteBtn, "#ef4444");

        rightIcons.getChildren().addAll(viewBtn, editBtn, deleteBtn);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        topRow.getChildren().addAll(leftTitle, spacer, rightIcons);

        // === Bloc infos : Location / Price / Availability ===
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(40);
        infoGrid.setVgap(4);

        // Location
        Label locLabel = new Label("Location:");
        locLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #6b7280;");
        Label locValue = new Label(nullToDash(t.getLocation()));
        locValue.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");

        // Price
        Label priceLabel = new Label("Price:");
        priceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #6b7280;");
        Label priceValue = new Label("$ " + t.getPricePerHour() + " / hour");
        priceValue.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");

        // Availability (global on/off)
        Label availLabel = new Label("Availability:");
        availLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #6b7280;");

        Label availValue;
        if (t.isAvailable()) {
            availValue = new Label("Available");
            availValue.setStyle("-fx-font-size: 12px; -fx-text-fill: #16a34a;"); // vert
        } else {
            availValue = new Label("Unavailable");
            availValue.setStyle("-fx-font-size: 12px; -fx-text-fill: #dc2626;"); // rouge
        }

        infoGrid.add(locLabel, 0, 0);
        infoGrid.add(locValue, 0, 1);

        infoGrid.add(priceLabel, 1, 0);
        infoGrid.add(priceValue, 1, 1);

        infoGrid.add(availLabel, 2, 0);
        infoGrid.add(availValue, 2, 1);

        card.getChildren().addAll(topRow, infoGrid);
        return card;
    }

    private void styleIconButton(Button btn, String colorHex) {
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + colorHex + ";" +
                        "-fx-font-size: 13px;"
        );
        btn.setPadding(new Insets(2, 4, 2, 4));
    }

    private String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    // ===== Dialog de création / édition =====

    /**
     * Ouvre un dialog pour créer ou éditer un terrain.
     * @param existing terrain existant (edit) ou null (création)
     * @return le terrain rempli (objet modifié ou nouveau), ou null si cancel / invalide
     */
    public Terrain openTerrainForm(Terrain existing) {
        Dialog<Terrain> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Create New Field" : "Edit Field");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        ComboBox<TerrainType> typeCombo = new ComboBox<>();
        typeCombo.getItems().setAll(TerrainType.values());

        TextField locationField = new TextField();
        TextField priceField = new TextField();
        TextField capacityField = new TextField();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);

        CheckBox availableCheck = new CheckBox("Field is available");
        availableCheck.setSelected(true);


        if (existing != null) {
            nameField.setText(existing.getName());
            typeCombo.setValue(existing.getType());
            locationField.setText(existing.getLocation());
            priceField.setText(String.valueOf(existing.getPricePerHour()));
            capacityField.setText(String.valueOf(existing.getCapacity()));
            descriptionArea.setText(existing.getDescription());
            availableCheck.setSelected(existing.isAvailable());
        }

        VBox content = new VBox(8);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(
                new Label("Field Name:"), nameField,
                new Label("Type:"), typeCombo,
                new Label("Location:"), locationField,
                new Label("Price per hour:"), priceField,
                new Label("Capacity:"), capacityField,
                availableCheck,
                new Label("Description:"), descriptionArea
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                String name = nameField.getText();
                TerrainType type = typeCombo.getValue();
                String location = locationField.getText();
                String desc = descriptionArea.getText();
                String priceText = priceField.getText();
                String capacityText = capacityField.getText();

                if (name == null || name.isBlank()) {
                    showAlert("Validation error", "Name is required.");
                    return null;
                }

                double price;
                int capacity;
                try {
                    price = Double.parseDouble(priceText);
                    capacity = Integer.parseInt(capacityText);
                } catch (NumberFormatException e) {
                    showAlert("Validation error", "Price and capacity must be numeric.");
                    return null;
                }

                Terrain result = (existing != null) ? existing : new Terrain();
                result.setName(name);
                result.setType(type);
                result.setLocation(location);
                result.setDescription(desc);
                result.setPricePerHour(price);
                result.setCapacity(capacity);
                result.setAvailable(availableCheck.isSelected());
                // ownerId sera branché plus tard avec l'user connecté

                return result;
            }
            return null;
        });

        Optional<Terrain> res = dialog.showAndWait();
        return res.orElse(null);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void openTerrainDetails(Terrain t) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Field Details");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        Label title = new Label(t.getName());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label type = new Label(t.getType() != null ? t.getType().name() : "-");
        type.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-text-fill: #329aff;" +
                        "-fx-background-color: #e0f0ff;" +
                        "-fx-padding: 3 10;" +
                        "-fx-background-radius: 999;"
        );

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);

        addRow(grid, 0, "Location", nullToDash(t.getLocation()));
        addRow(grid, 1, "Price", "$ " + t.getPricePerHour() + " / hour");
        addRow(grid, 2, "Capacity", t.getCapacity() + " people");
        addRow(grid, 3, "Description", nullToDash(t.getDescription()));
        addRow(grid, 4, "Availability", "Not configured"); // Placeholder pour plus tard

        VBox content = new VBox(10, title, type, grid);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void addRow(GridPane g, int row, String label, String value) {
        Label l = new Label(label + ":");
        l.setStyle("-fx-font-weight: bold; -fx-text-fill:#6b7280;");

        Label v = new Label(value);
        v.setStyle("-fx-text-fill:#374151;");

        g.add(l, 0, row);
        g.add(v, 1, row);
    }
}
