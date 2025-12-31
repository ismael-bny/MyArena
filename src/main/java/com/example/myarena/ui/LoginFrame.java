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

    @FXML private TextField usernameField;
    @FXML private PasswordField pwdField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;

    private LoginController controller;

    public LoginFrame() {
        // This line causes the error if LoginController doesn't have the constructor saved/compiled
        this.controller = new LoginController(this);
    }

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        if (controller != null) {
            controller.login();
        }
    }

    // --- Getters for the Logic Controller ---
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return pwdField.getText();
    }

    public void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        if (isSuccess) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        } else {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        }
    }

    public void navigateToMainMenu() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            // Ensure this path matches your FXML file location exactly
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 800, 600);

            // Add CSS if available
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