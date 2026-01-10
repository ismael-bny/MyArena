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
import javafx.scene.Node;
import java.io.IOException;

public class LoginFrame {

    @FXML private TextField usernameField;
    @FXML private PasswordField pwdField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;

    private LoginController controller;

    public LoginFrame() {
        this.controller = new LoginController(this);
    }

    @FXML
    public void initialize() {
        if (loginButton != null) {
            loginButton.setOnAction(event -> handleLogin());
        }
    }

    private void handleLogin() {
        if (controller != null) {
            controller.login();
        }
    }

    // --- Getters for the Logic Controller ---
    public String getUsername() {
        return (usernameField != null) ? usernameField.getText() : "";
    }

    public String getPassword() {
        return (pwdField != null) ? pwdField.getText() : "";
    }

    // --- UI Feedback Methods ---
    public void showMessage(String message, boolean isSuccess) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            if (isSuccess) {
                messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
            } else {
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            }
        }
    }

    // --- Navigation ---
    public void navigateToMainMenu() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Load Main Menu FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);

            // Apply CSS safely
            var cssUrl = getClass().getResource("/com/example/myarena/styles/myarena-styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                // Fallback to old path if the new one doesn't exist yet
                var oldCssUrl = getClass().getResource("/com/example/myarena/application.css");
                if(oldCssUrl != null) scene.getStylesheets().add(oldCssUrl.toExternalForm());
            }

            stage.setTitle("MyArena - Dashboard");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error loading Main Menu: " + e.getMessage(), false);
        }
    }
}