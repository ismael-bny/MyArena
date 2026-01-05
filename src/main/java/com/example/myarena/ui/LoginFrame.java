package com.example.myarena.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        if (controller != null) controller.login();
    }

    public String getUsername() { return usernameField.getText(); }
    public String getPassword() { return pwdField.getText(); }

    public void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.setStyle(isSuccess ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    public void navigateToMainMenu() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Parent root = loader.load();
            stage.setTitle("MyArena - Dashboard");
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            System.err.println("Failed to load Main Menu: " + e.getMessage());
        }
    }

    public void closeWindow() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        if (stage != null) stage.close();
    }
}