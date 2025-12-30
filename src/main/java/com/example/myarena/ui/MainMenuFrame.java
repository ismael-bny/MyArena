package com.example.myarena.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuFrame {

    @FXML
    private Button reservationButton;

    @FXML
    private Button logoutButton;

    @FXML
    public void initialize() {
        // Navigate to Reservation Page
        reservationButton.setOnAction(event -> navigateTo("/com/example/myarena/reservation-page.fxml", "MyArena - Reservation"));

        // Logout (Navigate back to Login)
        logoutButton.setOnAction(event -> navigateTo("/com/example/myarena/login-page.fxml", "MyArena - Login"));
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            Stage stage = (Stage) reservationButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 800, 600);

            // Ensure this path matches the location in resources
            if (getClass().getResource("/com/example/myarena/application.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/com/example/myarena/application.css").toExternalForm());
            } else {
                System.out.println("⚠️ CSS NOT FOUND: Check /src/main/resources/com/example/myarena/application.css");
            }

            stage.setTitle(title);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}