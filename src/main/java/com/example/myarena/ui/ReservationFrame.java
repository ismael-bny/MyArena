package com.example.myarena.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Date;

public class ReservationFrame {

    @FXML
    private TextField terrainIdField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField participantsField;

    @FXML
    private TextField purposeField;

    @FXML
    private Button createButton;

    @FXML
    private Label statusLabel;

    private ReservationController controller;

    private Long currentUserId = 1L;

    public ReservationFrame() {
        this.controller = new ReservationController(this);
    }

    @FXML
    public void initialize() {
        createButton.setOnAction(event -> handleCreateReservation());
    }

    private void handleCreateReservation() {
        try {
            Long terrainId = Long.parseLong(terrainIdField.getText());
            Date start = Date.valueOf(startDatePicker.getValue());
            Date end = Date.valueOf(startDatePicker.getValue());
            int participants = Integer.parseInt(participantsField.getText());
            String purpose = purposeField.getText();
            controller.createReservation(currentUserId, terrainId, start, end, participants, purpose);

            statusLabel.setText("Processing...");
            statusLabel.setStyle("-fx-text-fill: #6b7280;");

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid input: Please check numbers.");
            statusLabel.setStyle("-fx-text-fill: #ef4444;"); // Red color
        } catch (Exception e){
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    public void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #10b981;"); // Green
    }

    public void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #ef4444;"); // Red
    }
}
