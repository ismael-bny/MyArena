package com.example.myarena.ui;

import com.example.myarena.facade.UserSession; // Import Session
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Date;

public class ReservationFrame {

    @FXML private TextField terrainIdField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField participantsField;
    @FXML private TextField purposeField;
    @FXML private Button createButton;
    @FXML private Button backButton; // NEW
    @FXML private Label statusLabel;

    private ReservationController controller;

    public ReservationFrame() {
        this.controller = new ReservationController(this);
    }

    @FXML
    public void initialize() {
        createButton.setOnAction(event -> handleCreateReservation());
        // Bind Back Button (Ensure you add this button in your FXML!)
        if(backButton != null) {
            backButton.setOnAction(this::goBack);
        }
    }

    private void handleCreateReservation() {
        try {
            // GET USER FROM SESSION
            Long userId = UserSession.getInstance().getUser().getId();

            Long terrainId = Long.parseLong(terrainIdField.getText());
            Date start = Date.valueOf(startDatePicker.getValue());
            Date end = Date.valueOf(endDatePicker.getValue());
            int participants = Integer.parseInt(participantsField.getText());
            String purpose = purposeField.getText();

            controller.createReservation(userId, terrainId, start, end, participants, purpose);

            statusLabel.setText("Reservation Sent!");
            statusLabel.setStyle("-fx-text-fill: green;");
        } catch (Exception e){
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ... helper methods (showSuccess, showError) ...
    public void showSuccess(String msg) { statusLabel.setText(msg); }
    public void showError(String msg) { statusLabel.setText(msg); }
}