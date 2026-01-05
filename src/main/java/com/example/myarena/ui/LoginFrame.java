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
    private Button createAccountButton;

    @FXML
    private Label messageLabel;

    private LoginController controller;

    public LoginFrame() {
        this.controller = new LoginController(this);
    }

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        createAccountButton.setOnAction(event -> navigateToRegister());
    }

    private void handleLogin() {
        if (controller != null) {
            controller.login();
        }
    }

    private void navigateToRegister() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/register-page.fxml"));
            Parent root = loader.load();
            stage.setTitle("MyArena - Register");
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            System.err.println("Failed to load Register Page: " + e.getMessage());
            e.printStackTrace();
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

        if (isSuccess) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        } else {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        }
    }

    public void navigateToMainMenu() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Parent root = loader.load();
            stage.setTitle("MyArena - Home");
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            System.err.println("Failed to load Main Menu: " + e.getMessage());
            e.printStackTrace();
            // Fallback: just show a success message
            showMessage("Login successful! Main menu not yet implemented.", true);
        }
    }

    public void navigateToProfile() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/profile-page.fxml"));
            Parent root = loader.load();
            stage.setTitle("MyArena - My Profile");
            stage.setScene(new Scene(root, 800, 700));
        } catch (IOException e) {
            System.err.println("Failed to load Profile Page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}