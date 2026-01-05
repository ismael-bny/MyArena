package com.example.myarena.ui;

import com.example.myarena.domain.Terrain;
import com.example.myarena.domain.User;
import com.example.myarena.facade.ReservationFacade;
import com.example.myarena.facade.TerrainFacade;
import com.example.myarena.facade.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationFrame {

    @FXML private ComboBox<Terrain> terrainComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField participantsField;
    @FXML private TextField purposeField;
    @FXML private ListView<String> slotsListView;
    @FXML private Button createButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private ReservationController controller;
    private LocalTime selectedStartTime;

    public ReservationFrame() {
        this.controller = new ReservationController(this);
    }

    @FXML
    public void initialize() {
        // 1. Load Terrains
        loadTerrains();

        // 2. Add Listeners
        terrainComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateSlots());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateSlots());

        // 3. Handle Slot Selection
        if (slotsListView != null) {
            slotsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    try {
                        String startString = newVal.split(" - ")[0];
                        this.selectedStartTime = LocalTime.parse(startString);
                        statusLabel.setText("Selected: " + newVal);
                        statusLabel.setStyle("-fx-text-fill: #18181b;");
                    } catch (Exception e) {
                        System.out.println("Error parsing time: " + e.getMessage());
                    }
                }
            });
        }

        // 4. Buttons
        createButton.setOnAction(event -> handleCreateReservation());
        if(backButton != null) backButton.setOnAction(this::goBack);
    }

    private void updateSlots() {
        Terrain terrain = terrainComboBox.getValue();
        LocalDate date = datePicker.getValue();

        if (terrain != null && date != null) {
            List<String> slots = ReservationFacade.getInstance().getAvailableSlots(terrain.getId(), date);
            slotsListView.getItems().setAll(slots);

            if (slots.isEmpty()) {
                statusLabel.setText("No slots available.");
                statusLabel.setStyle("-fx-text-fill: #ef4444;");
            } else {
                statusLabel.setText("Select a time slot.");
                statusLabel.setStyle("-fx-text-fill: #71717a;");
            }
        }
    }

    private void handleCreateReservation() {
        try {
            // ✅ 1. Get User Safely
            User currentUser = UserSession.getInstance().getUser();

            // ✅ 2. Security Check: Is the User valid?
            if (currentUser == null || currentUser.getId() == null) {
                showError("Session error: User ID is NULL. Please Logout and Login again");
                return;
            }
            if (currentUser.getId() == 0) {
                // This explains the error to you
                showError("Database Error: User ID is missing. Check 'users' table.");
                System.err.println("User ID is NULL/0 for email: " + currentUser.getEmail());
                return;
            }
            Long userId = currentUser.getId();

            Terrain terrain = terrainComboBox.getValue();
            LocalDate date = datePicker.getValue();

            if (terrain == null || date == null || selectedStartTime == null) {
                showError("Please select a field, date, and time slot.");
                return;
            }

            // Dates
            Date start = new Date(Timestamp.valueOf(date.atTime(selectedStartTime)).getTime());
            Date end = new Date(Timestamp.valueOf(date.atTime(selectedStartTime.plusHours(1))).getTime());

            String partText = participantsField.getText();
            if (partText.isEmpty()) { showError("Participants required."); return; }
            int participants = Integer.parseInt(partText);

            controller.createReservation(userId, terrain.getId(), start, end, participants, purposeField.getText());
            navigateToMyReservations();

            showSuccess("Reservation Confirmed!");
            updateSlots();

        } catch (NumberFormatException e) {
            showError("Participants must be a number.");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToMyReservations() {
        try {
            // Load the new FXML
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/myarena/my-reservations.fxml"));
            Stage stage = (Stage) createButton.getScene().getWindow();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Reservation created, but failed to load history page.");
        }
    }

    private void loadTerrains() {
        List<Terrain> terrains = TerrainFacade.getInstance().getAllTerrains();
        terrainComboBox.getItems().setAll(terrains);

        terrainComboBox.setConverter(new StringConverter<Terrain>() {
            @Override
            public String toString(Terrain t) {
                return (t == null) ? null : t.getName() + " ($" + t.getPricePerHour() + "/h)";
            }
            @Override
            public Terrain fromString(String string) { return null; }
        });
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSuccess(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #16a34a;");
    }

    public void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #ef4444;");
    }
}