package com.example.myarena.ui;

import com.example.myarena.domain.UserRole;
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
    @FXML private Button subscriptionButton; // New FXML injection
    @FXML private Button logoutButton;
    @FXML private Label welcomeLabel;
    @FXML private Button myReservationsButton;

    @FXML
    public void initialize() {
        // Personalize welcome message
        String username = UserSession.getInstance().getUser().getName();
        welcomeLabel.setText("Welcome back, " + username);

        // Bind Standard Actions
        reservationButton.setOnAction(e -> navigate(e, "/com/example/myarena/reservation-page.fxml"));
        if (myReservationsButton != null) {
            myReservationsButton.setOnAction(e -> navigate(e, "/com/example/myarena/my-reservations.fxml"));
        }

        // Role-based visibility for Terrain Management
        UserRole role = UserSession.getInstance().getUser().getRole();
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            terrainButton.setVisible(false);
            terrainButton.setManaged(false);
        } else {
            terrainButton.setOnAction(e -> navigate(e, "/com/example/myarena/terrain-management.fxml"));
        }

        // --- NEW: Subscription Navigation Logic ---
        if (subscriptionButton != null) {
            if (role == UserRole.ADMIN || role == UserRole.OWNER) {
                subscriptionButton.setText("💎 Manage Plans");
                subscriptionButton.setOnAction(e -> navigate(e, "/com/example/myarena/plan-management.fxml"));
            } else {
                subscriptionButton.setText("💎 View Plans");
                subscriptionButton.setOnAction(e -> navigate(e, "/com/example/myarena/subscription-plans.fxml"));
            }
        }

        logoutButton.setOnAction(this::handleLogout);
    }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            if (getClass().getResource("/com/example/myarena/application.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/com/example/myarena/application.css").toExternalForm());
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLogout(ActionEvent event) {
        UserSession.getInstance().cleanSession();
        navigate(event, "/com/example/myarena/login-page.fxml");
    }


}