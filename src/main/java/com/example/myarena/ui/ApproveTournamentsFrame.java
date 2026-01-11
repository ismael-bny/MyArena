package com.example.myarena.ui;

import com.example.myarena.domain.Tournament;
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

public class ApproveTournamentsFrame {

    @FXML private TableView<Tournament> tournamentTable;
    @FXML private TableColumn<Tournament, Long> colId;
    @FXML private TableColumn<Tournament, String> colName;
    @FXML private TableColumn<Tournament, String> colSport;
    @FXML private TableColumn<Tournament, Date> colDate;
    @FXML private TableColumn<Tournament, String> colLocation;
    @FXML private TableColumn<Tournament, Long> colOrganiser;
    @FXML private TableColumn<Tournament, Void> colActions;

    @FXML private Button btnRefresh;
    @FXML private Button btnBack;
    @FXML private Label lblCount;

    private ApproveTournamentsController controller;
    private ObservableList<Tournament> tournamentList;

    public ApproveTournamentsFrame() {
        this.controller = new ApproveTournamentsController(this);
    }

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSport.setCellValueFactory(new PropertyValueFactory<>("sport"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colOrganiser.setCellValueFactory(new PropertyValueFactory<>("organiserId"));

        // Formater la date
        colDate.setCellFactory(col -> new TableCell<Tournament, Date>() {
            private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

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

        // Colonne Actions avec boutons Approve/Reject
        addActionButtons();

        // Boutons
        if (btnRefresh != null) {
            btnRefresh.setOnAction(e -> loadPendingTournaments());
        }
        if (btnBack != null) {
            btnBack.setOnAction(this::navigateToMainMenu);
        }

        // Charger les données
        loadPendingTournaments();
    }

    private void addActionButtons() {
        Callback<TableColumn<Tournament, Void>, TableCell<Tournament, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<Tournament, Void> call(final TableColumn<Tournament, Void> param) {
                        final TableCell<Tournament, Void> cell = new TableCell<>() {

                            private final Button btnApprove = new Button("✓ Approve");
                            private final Button btnReject = new Button("✗ Reject");
                            private final Button btnDetails = new Button("Details");

                            {
                                btnApprove.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-min-width: 80px;");
                                btnReject.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-min-width: 70px;");
                                btnDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px; -fx-min-width: 70px;");

                                btnApprove.setOnAction(event -> {
                                    Tournament tournament = getTableView().getItems().get(getIndex());
                                    handleApprove(tournament);
                                });

                                btnReject.setOnAction(event -> {
                                    Tournament tournament = getTableView().getItems().get(getIndex());
                                    handleReject(tournament);
                                });

                                btnDetails.setOnAction(event -> {
                                    Tournament tournament = getTableView().getItems().get(getIndex());
                                    showTournamentDetails(tournament);
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    HBox buttons = new HBox(5);
                                    buttons.setAlignment(Pos.CENTER);
                                    buttons.getChildren().addAll(btnDetails, btnApprove, btnReject);
                                    setGraphic(buttons);
                                }
                            }
                        };
                        return cell;
                    }
                };

        colActions.setCellFactory(cellFactory);
    }

    private void loadPendingTournaments() {
        try {
            List<Tournament> tournaments = controller.getPendingTournaments();
            tournamentList = FXCollections.observableArrayList(tournaments);
            tournamentTable.setItems(tournamentList);

            if (lblCount != null) {
                lblCount.setText("Pending: " + tournaments.size() + " tournament(s)");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading pending tournaments: " + e.getMessage());
        }
    }

    private void handleApprove(Tournament tournament) {
        if (tournament == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Approve Tournament");
        confirm.setHeaderText("Approve " + tournament.getName());
        confirm.setContentText("This tournament will become visible and open for registrations.\n\nConfirm approval?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = controller.approveTournament(tournament.getId());
                if (success) {
                    showInfo("Tournament approved successfully!\nOrganizer has been notified.");
                    loadPendingTournaments(); // Refresh
                } else {
                    showError("Failed to approve tournament.");
                }
            }
        });
    }

    private void handleReject(Tournament tournament) {
        if (tournament == null) return;

        // Dialog pour demander la raison
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Tournament");
        dialog.setHeaderText("Reject " + tournament.getName());
        dialog.setContentText("Please provide a reason for rejection:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            if (reason != null && !reason.trim().isEmpty()) {
                boolean success = controller.rejectTournament(tournament.getId(), reason);
                if (success) {
                    showInfo("Tournament rejected.\nOrganizer has been notified with the reason.");
                    loadPendingTournaments(); // Refresh
                } else {
                    showError("Failed to reject tournament.");
                }
            } else {
                showError("A reason is required to reject a tournament.");
            }
        });
    }

    private void showTournamentDetails(Tournament tournament) {
        if (tournament == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tournament Details");
        alert.setHeaderText(tournament.getName() + " (ID: " + tournament.getId() + ")");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String details = String.format(
                "Organizer ID: %d\n" +
                        "Sport: %s\n" +
                        "Location: %s\n" +
                        "Start: %s\n" +
                        "End: %s\n" +
                        "Max Participants: %d\n" +
                        "Registration Fee: €%.2f\n" +
                        "Prize: %s\n" +
                        "Status: %s\n" +
                        "Created: %s\n\n" +
                        "Description:\n%s\n\n" +
                        "Rules:\n%s",
                tournament.getOrganiserId(),
                tournament.getSport(),
                tournament.getLocation(),
                sdf.format(tournament.getStartDate()),
                sdf.format(tournament.getEndDate()),
                tournament.getMaxParticipants(),
                tournament.getRegistrationFee(),
                tournament.getPrize() != null ? tournament.getPrize() : "N/A",
                tournament.getStatus(),
                sdf.format(tournament.getCreatedAt()),
                tournament.getDescription() != null ? tournament.getDescription() : "No description",
                tournament.getRules() != null ? tournament.getRules() : "No specific rules"
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