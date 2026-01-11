package com.example.myarena.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class CreateTournamentFrame {

    @FXML private TextField txtName;
    @FXML private TextField txtSport;
    @FXML private TextArea txtDescription;
    @FXML private TextArea txtRules;
    @FXML private DatePicker dateStart;
    @FXML private TextField txtStartTime;
    @FXML private DatePicker dateEnd;
    @FXML private TextField txtEndTime;
    @FXML private TextField txtLocation;
    @FXML private TextField txtMaxParticipants;
    @FXML private TextField txtRegistrationFee;
    @FXML private TextField txtPrize;
    @FXML private TextField txtTerrainId;
    @FXML private Button btnCreate;
    @FXML private Button btnCancel;
    @FXML private Label lblMessage;

    private CreateTournamentController controller;

    public CreateTournamentFrame() {
        this.controller = new CreateTournamentController(this);
    }

    @FXML
    public void initialize() {
        // Validation en temps réel
        txtMaxParticipants.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtMaxParticipants.setText(old);
            }
        });

        txtRegistrationFee.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                txtRegistrationFee.setText(old);
            }
        });

        txtTerrainId.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtTerrainId.setText(old);
            }
        });

        // Boutons
        if (btnCreate != null) {
            btnCreate.setOnAction(e -> handleCreate());
        }
        if (btnCancel != null) {
            btnCancel.setOnAction(this::navigateBack);
        }

        // Valeurs par défaut
        txtStartTime.setPromptText("HH:MM (e.g., 10:00)");
        txtEndTime.setPromptText("HH:MM (e.g., 18:00)");
        txtRegistrationFee.setText("0.00");
    }

    private void handleCreate() {
        try {
            // Récupérer les valeurs
            String name = txtName.getText();
            String sport = txtSport.getText();
            String description = txtDescription.getText();
            String rules = txtRules.getText();
            String location = txtLocation.getText();
            String prize = txtPrize.getText();

            // Parser les dates
            Date startDate = parseDateTimeFields(dateStart, txtStartTime);
            Date endDate = parseDateTimeFields(dateEnd, txtEndTime);

            // Parser les nombres
            Integer maxParticipants = null;
            if (txtMaxParticipants.getText() != null && !txtMaxParticipants.getText().trim().isEmpty()) {
                maxParticipants = Integer.parseInt(txtMaxParticipants.getText());
            }

            BigDecimal registrationFee = BigDecimal.ZERO;
            if (txtRegistrationFee.getText() != null && !txtRegistrationFee.getText().trim().isEmpty()) {
                registrationFee = new BigDecimal(txtRegistrationFee.getText());
            }

            Long terrainId = null;
            if (txtTerrainId.getText() != null && !txtTerrainId.getText().trim().isEmpty()) {
                terrainId = Long.parseLong(txtTerrainId.getText());
            }

            // Appeler le controller
            boolean success = controller.createTournament(
                    name, sport, description, rules,
                    startDate, endDate, location,
                    maxParticipants, registrationFee, prize, terrainId
            );

            if (success) {
                // Nettoyer le formulaire
                clearForm();
            }

        } catch (NumberFormatException e) {
            showError("Invalid number format. Please check your input.");
        } catch (Exception e) {
            showError("Error creating tournament: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Date parseDateTimeFields(DatePicker datePicker, TextField timeField) {
        if (datePicker.getValue() == null) {
            return null;
        }

        LocalDateTime dateTime;
        String timeStr = timeField.getText();

        if (timeStr != null && !timeStr.trim().isEmpty()) {
            try {
                String[] parts = timeStr.split(":");
                int hour = Integer.parseInt(parts[0]);
                int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                dateTime = datePicker.getValue().atTime(hour, minute);
            } catch (Exception e) {
                // Si le format de l'heure est invalide, utiliser 00:00
                dateTime = datePicker.getValue().atStartOfDay();
            }
        } else {
            dateTime = datePicker.getValue().atStartOfDay();
        }

        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    private void clearForm() {
        txtName.clear();
        txtSport.clear();
        txtDescription.clear();
        txtRules.clear();
        dateStart.setValue(null);
        txtStartTime.clear();
        dateEnd.setValue(null);
        txtEndTime.clear();
        txtLocation.clear();
        txtMaxParticipants.clear();
        txtRegistrationFee.setText("0.00");
        txtPrize.clear();
        txtTerrainId.clear();
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Tournament Created");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}