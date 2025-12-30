package com.example.myarena.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
    private Button createButton;

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

            controller.createReservation(currentUserId, terrainId, start, end);
        } catch (Exception e){
            System.out.println("Invalid input: " + e.getMessage());
        }
    }
}
