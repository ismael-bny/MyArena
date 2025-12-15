package com.example.myarena.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
}
