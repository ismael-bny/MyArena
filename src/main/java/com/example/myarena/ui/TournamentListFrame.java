package com.example.myarena.ui;

import com.example.myarena.domain.Tournament;
import com.example.myarena.domain.UserRole;
import com.example.myarena.facade.UserSession;
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

public class TournamentListFrame {

    @FXML private TableView<Tournament> tournamentTable;
    @FXML private TableColumn<Tournament, Long> colId;
    @FXML private TableColumn<Tournament, String> colName;
    @FXML private TableColumn<Tournament, String> colSport;
    @FXML private TableColumn<Tournament, Date> colDate;
    @FXML private TableColumn<Tournament, String> colLocation;
    @FXML private TableColumn<Tournament, Integer> colParticipants;
    @FXML private TableColumn<Tournament, String> colStatus;
    @FXML private TableColumn<Tournament, Void> colActions;

    @FXML private Button btnRefresh;
    @FXML private Button btnBack;
    @FXML private Label lblTitle;

    private TournamentListController controller;
    private ObservableList<Tournament> tournamentList;

    public TournamentListFrame() {
        this.controller = new TournamentListController(this);
    }

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSport.setCellValueFactory(new PropertyValueFactory<>("sport"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Colonne participants avec format "X/Y"
        colParticipants.setCellValueFactory(new PropertyValueFactory<>("currentParticipants"));
        colParticipants.setCellFactory(col -> new TableCell<Tournament, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Tournament tournament = getTableRow().getItem();
                    setText(tournament.getCurrentParticipants() + "/" + tournament.getMaxParticipants());
                }
            }
        });

        // Formater la date
        colDate.setCellFactory(col -> new TableCell<Tournament, Date>() {
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

        // Colonne Actions avec boutons
        addActionButtons();

        // Boutons
        if (btnRefresh != null) {
            btnRefresh.setOnAction(e -> loadTournaments());
        }
        if (btnBack != null) {
            btnBack.setOnAction(this::navigateToMainMenu);
        }

        // Charger les données
        loadTournaments();
    }

    private void addActionButtons() {
        Callback<TableColumn<Tournament, Void>, TableCell<Tournament, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<Tournament, Void> call(final TableColumn<Tournament, Void> param) {
                        final TableCell<Tournament, Void> cell = new TableCell<>() {

                            private final Button btnRegister = new Button("Register");
                            private final Button btnDetails = new Button("Details");

                            {
                                btnRegister.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 11px;");
                                btnDetails.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px;");

                                btnRegister.setOnAction(event -> {
                                    Tournament tournament = getTableView().getItems().get(getIndex());
                                    handleRegister(tournament);
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
                                    Tournament tournament = getTableView().getItems().get(getIndex());
                                    HBox buttons = new HBox(5);
                                    buttons.setAlignment(Pos.CENTER);

                                    buttons.getChildren().add(btnDetails);

                                    // Afficher le bouton Register seulement si le tournoi est ouvert
                                    if (tournament.isRegistrationOpen()) {
                                        boolean alreadyRegistered = controller.isUserRegistered(tournament.getId());
                                        if (!alreadyRegistered) {
                                            buttons.getChildren().add(btnRegister);
                                        } else {
                                            Label lblRegistered = new Label("✓ Registered");
                                            lblRegistered.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                                            buttons.getChildren().add(lblRegistered);
                                        }
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

    private void loadTournaments() {
        try {
            UserRole role = UserSession.getInstance().getUser().getRole();
            List<Tournament> tournaments;

            // Admin et Organisateur voient tous les tournois
            if (role == UserRole.ADMIN || role == UserRole.ORGANIZER) {
                tournaments = controller.getAllTournaments();
                if (lblTitle != null) {
                    lblTitle.setText("All Tournaments");
                }
            } else {
                // Client voit seulement les tournois disponibles
                tournaments = controller.getAvailableTournaments();
                if (lblTitle != null) {
                    lblTitle.setText("Available Tournaments");
                }
            }

            tournamentList = FXCollections.observableArrayList(tournaments);
            tournamentTable.setItems(tournamentList);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading tournaments: " + e.getMessage());
        }
    }

    private void handleRegister(Tournament tournament) {
        if (tournament == null) return;

        // Confirmation dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Registration");
        confirm.setHeaderText("Register to " + tournament.getName());
        confirm.setContentText("Do you want to register to this tournament?\n\n" +
                "Fee: €" + tournament.getRegistrationFee() + "\n" +
                "Remaining slots: " + tournament.getRemainingSlots());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = controller.registerToTournament(tournament.getId());
                if (success) {
                    showInfo("Registration successful! Waiting for organizer validation.");
                    loadTournaments(); // Refresh
                } else {
                    showError("Registration failed. Please try again.");
                }
            }
        });
    }

    private void showTournamentDetails(Tournament tournament) {
        if (tournament == null) return;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tournament Details");
        alert.setHeaderText(tournament.getName());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String details = String.format(
                "Sport: %s\n" +
                        "Location: %s\n" +
                        "Start: %s\n" +
                        "End: %s\n" +
                        "Participants: %d/%d\n" +
                        "Fee: €%.2f\n" +
                        "Prize: %s\n" +
                        "Status: %s\n\n" +
                        "Description:\n%s\n\n" +
                        "Rules:\n%s",
                tournament.getSport(),
                tournament.getLocation(),
                sdf.format(tournament.getStartDate()),
                sdf.format(tournament.getEndDate()),
                tournament.getCurrentParticipants(),
                tournament.getMaxParticipants(),
                tournament.getRegistrationFee(),
                tournament.getPrize() != null ? tournament.getPrize() : "N/A",
                tournament.getStatus(),
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