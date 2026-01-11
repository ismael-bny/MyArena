package com.example.myarena.ui;

import com.example.myarena.domain.Tournament;
import com.example.myarena.domain.TournamentRegistration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyTournamentRegistrationsFrame {

    @FXML private TableView<TournamentRegistration> registrationTable;
    @FXML private TableColumn<TournamentRegistration, Long> colRegistrationId;
    @FXML private TableColumn<TournamentRegistration, Long> colTournamentId;
    @FXML private TableColumn<TournamentRegistration, String> colTournamentName;
    @FXML private TableColumn<TournamentRegistration, String> colStatus;
    @FXML private TableColumn<TournamentRegistration, Date> colRegisteredAt;
    @FXML private TableColumn<TournamentRegistration, Void> colActions;

    @FXML private Button btnRefresh;
    @FXML private Button btnBack;
    @FXML private Label lblCount;

    private MyTournamentRegistrationsController controller;
    private ObservableList<TournamentRegistration> registrationList;

    public MyTournamentRegistrationsFrame() {
        this.controller = new MyTournamentRegistrationsController(this);
    }

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colRegistrationId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTournamentId.setCellValueFactory(new PropertyValueFactory<>("tournamentId"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colRegisteredAt.setCellValueFactory(new PropertyValueFactory<>("registeredAt"));

        // Colonne Tournament Name (custom)
        colTournamentName.setCellValueFactory(cellData -> {
            Long tournamentId = cellData.getValue().getTournamentId();
            Tournament tournament = controller.getTournamentById(tournamentId);
            return new javafx.beans.property.SimpleStringProperty(
                    tournament != null ? tournament.getName() : "Unknown"
            );
        });

        // Formater la date
        colRegisteredAt.setCellFactory(col -> new TableCell<TournamentRegistration, Date>() {
            private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });

        // Colonne Actions
        addActionButtons();

        // Boutons
        if (btnRefresh != null) {
            btnRefresh.setOnAction(e -> loadRegistrations());
        }
        if (btnBack != null) {
            btnBack.setOnAction(this::navigateToMainMenu);
        }

        // Charger les données
        loadRegistrations();
    }

    private void addActionButtons() {
        Callback<TableColumn<TournamentRegistration, Void>, TableCell<TournamentRegistration, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<TournamentRegistration, Void> call(final TableColumn<TournamentRegistration, Void> param) {
                        final TableCell<TournamentRegistration, Void> cell = new TableCell<>() {

                            private final Button btnCancel = new Button("Cancel");
                            private final Button btnDetails = new Button("Details");

                            {
                                btnCancel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px;");
                                btnDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px;");

                                btnCancel.setOnAction(event -> {
                                    TournamentRegistration registration = getTableView().getItems().get(getIndex());
                                    handleCancel(registration);
                                });

                                btnDetails.setOnAction(event -> {
                                    TournamentRegistration registration = getTableView().getItems().get(getIndex());
                                    showRegistrationDetails(registration);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    TournamentRegistration registration = getTableView().getItems().get(getIndex());
                                    HBox buttons = new HBox(5);
                                    buttons.setAlignment(Pos.CENTER);

                                    buttons.getChildren().add(btnDetails);

                                    // Afficher le bouton Cancel seulement si status = PendingValidation ou Validated
                                    String status = registration.getStatus().name();
                                    if (status.equals("PendingValidation") || status.equals("Validated")) {
                                        buttons.getChildren().add(btnCancel);
                                    }

                                    setGraphic(buttons);
                                }
                            }
                        };
                        return cell;
                    }
                };

        colActions.setCellFactory(cellFactory);
    }

    private void loadRegistrations() {
        try {
            List<TournamentRegistration> registrations = controller.getMyRegistrations();
            registrationList = FXCollections.observableArrayList(registrations);
            registrationTable.setItems(registrationList);

            if (lblCount != null) {
                lblCount.setText("Total: " + registrations.size() + " registration(s)");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading registrations: " + e.getMessage());
        }
    }

    private void handleCancel(TournamentRegistration registration) {
        if (registration == null) return;

        Tournament tournament = controller.getTournamentById(registration.getTournamentId());

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Registration");
        confirm.setHeaderText("Cancel registration to " + (tournament != null ? tournament.getName() : "tournament"));
        confirm.setContentText("Are you sure you want to cancel this registration?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = controller.cancelRegistration(registration.getTournamentId());
                if (success) {
                    showInfo("Registration cancelled successfully.");
                    loadRegistrations(); // Refresh
                } else {
                    showError("Failed to cancel registration.");
                }
            }
        });
    }

    private void showRegistrationDetails(TournamentRegistration registration) {
        if (registration == null) return;

        Tournament tournament = controller.getTournamentById(registration.getTournamentId());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Details");
        alert.setHeaderText("Registration #" + registration.getId());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String details = String.format(
                "Tournament: %s\n" +
                        "Sport: %s\n" +
                        "Location: %s\n" +
                        "Start Date: %s\n" +
                        "Registration Status: %s\n" +
                        "Registered At: %s\n" +
                        "Notes: %s",
                tournament != null ? tournament.getName() : "Unknown",
                tournament != null ? tournament.getSport() : "N/A",
                tournament != null ? tournament.getLocation() : "N/A",
                tournament != null ? sdf.format(tournament.getStartDate()) : "N/A",
                registration.getStatus(),
                sdf.format(registration.getRegisteredAt()),
                registration.getNotes() != null ? registration.getNotes() : "No notes"
        );

        alert.setContentText(details);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToMainMenu(ActionEvent event) {
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