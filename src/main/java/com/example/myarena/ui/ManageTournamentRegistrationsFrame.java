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
import java.util.Optional;

public class ManageTournamentRegistrationsFrame {

    @FXML private ComboBox<Tournament> cmbTournaments;
    @FXML private TableView<TournamentRegistration> registrationTable;
    @FXML private TableColumn<TournamentRegistration, Long> colRegistrationId;
    @FXML private TableColumn<TournamentRegistration, String> colUserId;
    @FXML private TableColumn<TournamentRegistration, String> colStatus;
    @FXML private TableColumn<TournamentRegistration, Date> colRegisteredAt;
    @FXML private TableColumn<TournamentRegistration, Void> colActions;

    @FXML private Button btnRefresh;
    @FXML private Button btnBack;
    @FXML private Label lblCount;
    @FXML private Label lblPending;

    private ManageTournamentRegistrationsController controller;
    private ObservableList<TournamentRegistration> registrationList;

    public ManageTournamentRegistrationsFrame() {
        this.controller = new ManageTournamentRegistrationsController(this);
    }

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colRegistrationId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colRegisteredAt.setCellValueFactory(new PropertyValueFactory<>("registeredAt"));

        // Colonne User - afficher le nom au lieu de l'ID
        colUserId.setCellValueFactory(cellData -> {
            Long userId = cellData.getValue().getUserId();
            String userName = getUserName(userId);
            return new javafx.beans.property.SimpleStringProperty(userName);
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

        // Charger les tournois de l'organisateur
        loadMyTournaments();

        // Listener sur sélection de tournoi
        cmbTournaments.setOnAction(e -> {
            Tournament selected = cmbTournaments.getValue();
            if (selected != null) {
                loadRegistrations(selected.getId());
            }
        });

        // Boutons
        if (btnRefresh != null) {
            btnRefresh.setOnAction(e -> {
                Tournament selected = cmbTournaments.getValue();
                if (selected != null) {
                    loadRegistrations(selected.getId());
                }
            });
        }
        if (btnBack != null) {
            btnBack.setOnAction(this::navigateToMainMenu);
        }
    }

    private void addActionButtons() {
        Callback<TableColumn<TournamentRegistration, Void>, TableCell<TournamentRegistration, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<TournamentRegistration, Void> call(final TableColumn<TournamentRegistration, Void> param) {
                        final TableCell<TournamentRegistration, Void> cell = new TableCell<>() {

                            private final Button btnValidate = new Button("✓ Validate");
                            private final Button btnReject = new Button("✗ Reject");

                            {
                                btnValidate.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-min-width: 80px;");
                                btnReject.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-min-width: 70px;");

                                btnValidate.setOnAction(event -> {
                                    TournamentRegistration registration = getTableView().getItems().get(getIndex());
                                    handleValidate(registration);
                                });

                                btnReject.setOnAction(event -> {
                                    TournamentRegistration registration = getTableView().getItems().get(getIndex());
                                    handleReject(registration);
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

                                    // Afficher les boutons seulement si status = PendingValidation
                                    if (registration.getStatus().name().equals("PendingValidation")) {
                                        buttons.getChildren().addAll(btnValidate, btnReject);
                                    } else {
                                        Label lblStatus = new Label(registration.getStatus().name());
                                        lblStatus.setStyle("-fx-font-weight: bold;");
                                        buttons.getChildren().add(lblStatus);
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

    private void loadMyTournaments() {
        try {
            List<Tournament> tournaments = controller.getMyTournaments();
            cmbTournaments.setItems(FXCollections.observableArrayList(tournaments));

            // Converter pour afficher le nom du tournoi
            cmbTournaments.setConverter(new javafx.util.StringConverter<Tournament>() {
                @Override
                public String toString(Tournament tournament) {
                    return tournament != null ? tournament.getName() + " (ID: " + tournament.getId() + ")" : "";
                }

                @Override
                public Tournament fromString(String string) {
                    return null;
                }
            });

            // Sélectionner le premier si disponible
            if (!tournaments.isEmpty()) {
                cmbTournaments.setValue(tournaments.get(0));
                loadRegistrations(tournaments.get(0).getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading tournaments: " + e.getMessage());
        }
    }

    private void loadRegistrations(Long tournamentId) {
        try {
            List<TournamentRegistration> registrations = controller.getTournamentRegistrations(tournamentId);
            registrationList = FXCollections.observableArrayList(registrations);
            registrationTable.setItems(registrationList);

            // Compter les pending
            long pendingCount = registrations.stream()
                    .filter(r -> r.getStatus().name().equals("PendingValidation"))
                    .count();

            if (lblCount != null) {
                lblCount.setText("Total: " + registrations.size() + " registration(s)");
            }
            if (lblPending != null) {
                lblPending.setText("Pending: " + pendingCount);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading registrations: " + e.getMessage());
        }
    }

    private void handleValidate(TournamentRegistration registration) {
        if (registration == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Validate Registration");
        confirm.setHeaderText("Validate Registration #" + registration.getId());
        confirm.setContentText("User ID: " + registration.getUserId() + "\n\nConfirm validation?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = controller.validateRegistration(registration.getId());
                if (success) {
                    showInfo("Registration validated successfully!\nUser has been notified.");
                    loadRegistrations(registration.getTournamentId()); // Refresh
                } else {
                    showError("Failed to validate registration.");
                }
            }
        });
    }

    private void handleReject(TournamentRegistration registration) {
        if (registration == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Registration");
        dialog.setHeaderText("Reject Registration #" + registration.getId());
        dialog.setContentText("Please provide a reason for rejection:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            if (reason != null && !reason.trim().isEmpty()) {
                boolean success = controller.rejectRegistration(registration.getId(), reason);
                if (success) {
                    showInfo("Registration rejected.\nUser has been notified with the reason.");
                    loadRegistrations(registration.getTournamentId()); // Refresh
                } else {
                    showError("Failed to reject registration.");
                }
            } else {
                showError("A reason is required to reject a registration.");
            }
        });
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

    private String getUserName(Long userId) {
        return controller.getUserName(userId);
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