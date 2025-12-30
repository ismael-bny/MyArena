package com.example.myarena.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFrame {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField pwdField;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    private LoginController controller;

    public LoginFrame() {
        this.controller = new LoginController(this);
    }

    @FXML
    public void initialize() {
        // JavaFX initialization if needed
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        if (controller != null) {
            controller.login();
        }
    }

    // Getters for the controller to access data
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return pwdField.getText();
    }

    public void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);

        // Change la couleur selon succès ou échec
        if (isSuccess) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        } else {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        }
    }

    public void navigateToMainMenu() {
        try {
            // Get current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Load Main Menu
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myarena/main-menu-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 800, 600);

            // Add CSS
            if (getClass().getResource("/com/example/myarena/application.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/com/example/myarena/application.css").toExternalForm());
            }

            stage.setTitle("MyArena - Dashboard");
            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load Main Menu");
        }
    }
}
