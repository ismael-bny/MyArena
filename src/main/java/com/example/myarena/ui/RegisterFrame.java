package com.example.myarena.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterFrame {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Button backToLoginButton;
    @FXML private Label messageLabel;

    private RegisterController controller;

    public RegisterFrame() {
        this.controller = new RegisterController(this);
    }

    @FXML
    public void initialize() {
        registerButton.setOnAction(event -> handleRegister());
        backToLoginButton.setOnAction(event -> navigateToLogin());
    }

    private void handleRegister() {
        if (controller != null) {
            controller.register();
        }
    }

    // Getters for controller
    public String getName() {
        return nameField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getPhone() {
        return phoneField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public String getConfirmPassword() {
        return confirmPasswordField.getText();
    }

    public void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.setStyle(isSuccess
                ? "-fx-text-fill: #10b981; -fx-font-size: 14px; -fx-font-weight: bold;"
                : "-fx-text-fill: #ef4444; -fx-font-size: 14px; -fx-font-weight: bold;");
    }

    public void navigateToLogin() {
        try {
            Stage stage = (Stage) registerButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/login-page.fxml"));  // ✅ CORRIGÉ
            Parent root = loader.load();
            stage.setTitle("MyArena - Login");
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            System.err.println("Failed to load Login Page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}