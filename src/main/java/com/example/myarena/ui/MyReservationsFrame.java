package com.example.myarena.ui;

import com.example.myarena.domain.Reservation;
import com.example.myarena.domain.User;
import com.example.myarena.facade.ReservationFacade;
import com.example.myarena.facade.UserSession;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public class MyReservationsFrame {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Long> idColumn;
    @FXML private TableColumn<Reservation, Long> terrainColumn;
    @FXML private TableColumn<Reservation, String> dateColumn;
    @FXML private TableColumn<Reservation, String> timeColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, String> priceColumn;
    @FXML private TableColumn<Reservation, Void> actionsColumn;
    @FXML private Button backButton;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
        if (backButton != null) backButton.setOnAction(this::goBack);
    }

    private void setupColumns() {
        // ✅ Fix: Use Lambdas instead of PropertyValueFactory (Avoids "Blank Cell" errors)
        idColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        terrainColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTerrainId()));
        statusColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus().toString()));

        // Date Formatting
        dateColumn.setCellValueFactory(cell -> {
            if (cell.getValue().getStartDate() != null) {
                return new SimpleStringProperty(dateFormat.format(cell.getValue().getStartDate()));
            }
            return new SimpleStringProperty("");
        });

        // Time Formatting
        timeColumn.setCellValueFactory(cell -> {
            if (cell.getValue().getStartDate() != null && cell.getValue().getEndDate() != null) {
                String start = timeFormat.format(cell.getValue().getStartDate());
                String end = timeFormat.format(cell.getValue().getEndDate());
                return new SimpleStringProperty(start + " - " + end);
            }
            return new SimpleStringProperty("");
        });

        // Price Formatting
        priceColumn.setCellValueFactory(cell ->
                new SimpleStringProperty("$" + cell.getValue().getTotalPrice())
        );

        // Actions Column
        actionsColumn.setCellFactory(param -> new TableCell<Reservation, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox hbox = new HBox(5);

            {
                editButton.setStyle("-fx-padding: 5px 10px; -fx-font-size: 12;");
                deleteButton.setStyle("-fx-padding: 5px 10px; -fx-font-size: 12;");
                deleteButton.setStyle(deleteButton.getStyle() + "; -fx-text-fill: white; -fx-background-color: #d4183d;");

                hbox.setStyle("-fx-alignment: center;");
                hbox.getChildren().addAll(editButton, deleteButton);

                editButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    openEditDialog(reservation.getId());
                });

                deleteButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    deleteReservation(reservation.getId(), getIndex());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });
    }

    private void loadData() {
        try {
            User user = UserSession.getInstance().getUser();
            if (user != null) {
                System.out.println("Fetching reservations for User ID: " + user.getId()); // DEBUG PRINT

                List<Reservation> list = ReservationFacade.getInstance().getUserReservations(user.getId());
                System.out.println("Found " + list.size() + " reservations."); // DEBUG PRINT

                reservationTable.getItems().setAll(list);

                if (list.isEmpty()) {
                    reservationTable.setPlaceholder(new Label("No reservations found in database."));
                }
            } else {
                System.out.println("Error: User is null in session.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openEditDialog(Long reservationId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/edit-reservation.fxml"));
            Parent root = loader.load();
            EditReservationController controller = loader.getController();
            controller.loadReservation(reservationId);

            Stage stage = new Stage();
            stage.setTitle("Edit Reservation");
            stage.setScene(new Scene(root));
            stage.setOnHidden(event -> loadData()); // Refresh table after closing
            stage.show();
        } catch (Exception e) {
            showError("Error opening edit dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteReservation(Long reservationId, int rowIndex) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this reservation?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (ReservationFacade.getInstance().deleteReservation(reservationId)) {
                    reservationTable.getItems().remove(rowIndex);
                    showSuccess("Reservation deleted successfully");
                } else {
                    showError("Failed to delete reservation");
                }
            } catch (Exception e) {
                showError("Error deleting reservation: " + e.getMessage());
                e.printStackTrace();
            }
        }
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

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}