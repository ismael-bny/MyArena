package com.example.myarena.ui;

import com.example.myarena.domain.Reservation;
import com.example.myarena.facade.ReservationFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class EditReservationController {

    @FXML private TextField reservationIdField;
    @FXML private TextField terrainIdField;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private Spinner<Integer> participantsSpinner;
    @FXML private TextArea purposeTextArea;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Reservation currentReservation;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @FXML
    public void initialize() {
        setupSpinner();
        cancelButton.setOnAction(this::onCancel);
        saveButton.setOnAction(this::onSave);
    }

    private void setupSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        participantsSpinner.setValueFactory(valueFactory);
    }

    public void loadReservation(Long reservationId) {
        try {
            System.out.println("DEBUG: Loading reservation with ID: " + reservationId);
            currentReservation = ReservationFacade.getInstance().getReservationById(reservationId);
            
            if (currentReservation != null) {
                System.out.println("DEBUG: Reservation found");
                System.out.println("DEBUG: Start Date: " + currentReservation.getStartDate());
                System.out.println("DEBUG: End Date: " + currentReservation.getEndDate());
                populateFields();
            } else {
                System.out.println("ERROR: Reservation is null");
                showError("Reservation not found");
            }
        } catch (Exception e) {
            System.out.println("ERROR loading reservation: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading reservation: " + e.getMessage());
        }
    }

    private void populateFields() {
        try {
            if (currentReservation == null) {
                showError("Reservation data is null");
                return;
            }

            // Basic fields
            reservationIdField.setText(String.valueOf(currentReservation.getId()));
            terrainIdField.setText(String.valueOf(currentReservation.getTerrainId()));

            // Start date and time
            if (currentReservation.getStartDate() != null) {
                LocalDate startLocalDate = currentReservation.getStartDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                startDatePicker.setValue(startLocalDate);
                
                String startTimeStr = timeFormat.format(currentReservation.getStartDate());
                System.out.println("DEBUG: Start time formatted as: " + startTimeStr);
                startTimeField.setText(startTimeStr);
            } else {
                System.out.println("DEBUG: Start date is null");
            }

            // End date and time
            if (currentReservation.getEndDate() != null) {
                LocalDate endLocalDate = currentReservation.getEndDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                endDatePicker.setValue(endLocalDate);
                
                String endTimeStr = timeFormat.format(currentReservation.getEndDate());
                System.out.println("DEBUG: End time formatted as: " + endTimeStr);
                endTimeField.setText(endTimeStr);
            } else {
                System.out.println("DEBUG: End date is null");
            }

            // Participants and purpose
            if (currentReservation.getParticipants() > 0) {
                participantsSpinner.getValueFactory().setValue(currentReservation.getParticipants());
            }
            
            if (currentReservation.getPurpose() != null) {
                purposeTextArea.setText(currentReservation.getPurpose());
            }
            
            System.out.println("DEBUG: All fields populated successfully");
        } catch (Exception e) {
            System.out.println("ERROR in populateFields: " + e.getMessage());
            e.printStackTrace();
            showError("Error populating fields: " + e.getMessage());
        }
    }

    @FXML
    private void onSave(ActionEvent event) {
        try {
            // Validate inputs
            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                showError("Please select both start and end dates");
                return;
            }

            if (startTimeField.getText().isEmpty() || endTimeField.getText().isEmpty()) {
                showError("Please enter both start and end times");
                return;
            }

            // Parse times
            String[] startTimeParts = startTimeField.getText().split(":");
            String[] endTimeParts = endTimeField.getText().split(":");

            if (startTimeParts.length != 2 || endTimeParts.length != 2) {
                showError("Invalid time format. Use HH:mm");
                return;
            }

            int startHour = Integer.parseInt(startTimeParts[0]);
            int startMinute = Integer.parseInt(startTimeParts[1]);
            int endHour = Integer.parseInt(endTimeParts[0]);
            int endMinute = Integer.parseInt(endTimeParts[1]);

            // Create Date objects
            Calendar startCal = Calendar.getInstance();
            startCal.set(startDatePicker.getValue().getYear(),
                    startDatePicker.getValue().getMonthValue() - 1,
                    startDatePicker.getValue().getDayOfMonth(),
                    startHour, startMinute, 0);
            Date startDate = startCal.getTime();

            Calendar endCal = Calendar.getInstance();
            endCal.set(endDatePicker.getValue().getYear(),
                    endDatePicker.getValue().getMonthValue() - 1,
                    endDatePicker.getValue().getDayOfMonth(),
                    endHour, endMinute, 0);
            Date endDate = endCal.getTime();

            if (endDate.before(startDate)) {
                showError("End time must be after start time");
                return;
            }

            // Update reservation
            currentReservation.setStartDate(startDate);
            currentReservation.setEndDate(endDate);
            currentReservation.setParticipants(participantsSpinner.getValue());
            currentReservation.setPurpose(purposeTextArea.getText());

            // Save
            if (ReservationFacade.getInstance().updateReservation(currentReservation)) {
                showSuccess("Reservation updated successfully");
                closeWindow();
            } else {
                showError("Failed to update reservation. This time slot may be taken.");
            }

        } catch (NumberFormatException e) {
            showError("Invalid time format. Use HH:mm");
        } catch (Exception e) {
            showError("Error saving reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
