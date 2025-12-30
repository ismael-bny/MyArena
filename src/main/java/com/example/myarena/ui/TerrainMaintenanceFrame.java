package com.example.myarena.ui;

import com.example.myarena.domain.Terrain;
import com.example.myarena.domain.TerrainType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class TerrainMaintenanceFrame {

    @FXML
    private Label messageLabel;

    @FXML
    private Button addButton;

    @FXML
    private FlowPane cardsContainer;

    private TerrainController controller;

    public TerrainMaintenanceFrame() {
        this.controller = new TerrainController(this);
    }

    @FXML
    public void initialize() {
        addButton.setOnAction(e -> controller.onAddTerrain());
        controller.loadTerrains();
    }

    // ==== Méthodes appelées par le controller ====

    /** Affiche le message en haut de l'écran. */
    public void showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setStyle(
                success
                        ? "-fx-text-fill: green; -fx-font-size: 14px;"
                        : "-fx-text-fill: red; -fx-font-size: 14px;"
        );
    }

    /** Affiche la liste des terrains sous forme de cartes. */
    public void showTerrains(List<Terrain> terrains) {
        cardsContainer.getChildren().clear();

        for (Terrain t : terrains) {
            VBox card = buildCardForTerrain(t);
            cardsContainer.getChildren().add(card);
        }
    }

    /** Construit une "carte" visuelle pour un terrain. */
    private VBox buildCardForTerrain(Terrain t) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dddddd;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);"
        );
        card.setPrefWidth(250);

        Label nameLabel = new Label(t.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label typeLabel = new Label(t.getType() != null ? t.getType().name() : "Unknown");
        typeLabel.setStyle("-fx-text-fill: #007bff;");

        Label locationLabel = new Label("Location: " + (t.getLocation() != null ? t.getLocation() : "-"));
        Label priceLabel = new Label("Price: " + t.getPricePerHour() + " / hour");
        Label capacityLabel = new Label("Capacity: " + t.getCapacity());

        HBox buttons = new HBox(10);
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        editButton.setOnAction(e -> controller.onEditTerrain(t));
        deleteButton.setOnAction(e -> controller.onDeleteTerrain(t));

        buttons.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(nameLabel, typeLabel, locationLabel, priceLabel, capacityLabel, buttons);
        return card;
    }

    /**
     * Ouvre un dialog pour créer ou éditer un terrain.
     * @param existing terrain existant (edit) ou null (création)
     * @return le terrain rempli (sans passage en DB), ou null si cancel / invalide
     */
    public Terrain openTerrainForm(Terrain existing) {
        Dialog<Terrain> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Terrain" : "Edit Terrain");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Formulaire simple
        TextField nameField = new TextField();
        ComboBox<TerrainType> typeCombo = new ComboBox<>();
        typeCombo.getItems().setAll(TerrainType.values());

        TextField locationField = new TextField();
        TextField priceField = new TextField();
        TextField capacityField = new TextField();
        TextArea descriptionArea = new TextArea();

        if (existing != null) {
            nameField.setText(existing.getName());
            typeCombo.setValue(existing.getType());
            locationField.setText(existing.getLocation());
            priceField.setText(String.valueOf(existing.getPricePerHour()));
            capacityField.setText(String.valueOf(existing.getCapacity()));
            descriptionArea.setText(existing.getDescription());
        }

        VBox content = new VBox(8);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Type:"), typeCombo,
                new Label("Location:"), locationField,
                new Label("Price per hour:"), priceField,
                new Label("Capacity:"), capacityField,
                new Label("Description:"), descriptionArea
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                // Validation simple
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
                result.setAvailable(true); // par défaut
                // ownerId sera géré plus tard via la session

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
}
