package com.example.myarena.ui;

import com.example.myarena.facade.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuFrame {

    @FXML private Button reservationButton;
    @FXML private Button terrainButton;
    @FXML private Button logoutButton;
    @FXML private Label welcomeLabel;
    @FXML private Button myReservationsButton;

    @FXML
    public void initialize() {
        // Personalize welcome message
        String username = UserSession.getInstance().getUser().getName();
        welcomeLabel.setText("Welcome back, " + username);

        // Bind Actions
        reservationButton.setOnAction(e -> navigate(e, "/com/example/myarena/reservation-page.fxml"));
        if (myReservationsButton != null) {
            myReservationsButton.setOnAction(e -> navigate(e, "/com/example/myarena/my-reservations.fxml"));
        }
        terrainButton.setOnAction(e -> navigate(e, "/com/example/myarena/terrain-management.fxml"));
        logoutButton.setOnAction(this::handleLogout);
    }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogout(ActionEvent event) {
        UserSession.getInstance().cleanSession(); // Clear session
        navigate(event, "/com/example/myarena/login-page.fxml"); // Go back to login
    }
}