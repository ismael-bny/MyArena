package com.example.myarena.ui;

import com.example.myarena.domain.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class ProfileFrame {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Label roleLabel;
    @FXML private Label statusLabel;

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private Button updateProfileButton;
    @FXML private Button changePasswordButton;
    @FXML private Button backButton;
    @FXML private Label messageLabel;

    private ProfileController controller;

    public ProfileFrame() {
        this.controller = new ProfileController(this);
    }

    @FXML
    public void initialize() {
        updateProfileButton.setOnAction(event -> handleUpdateProfile());
        changePasswordButton.setOnAction(event -> handleChangePassword());
        backButton.setOnAction(event -> navigateToMainMenu());

        // Load current user info
        controller.loadCurrentUser();
    }

    private void handleUpdateProfile() {
        if (controller != null) {
            controller.updateProfile();
        }
    }

    private void handleChangePassword() {
        if (controller != null) {
            controller.changePassword();
        }
    }

    public void displayUserInfo(User user) {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
        roleLabel.setText("Role: " + user.getRole());
        statusLabel.setText("Status: " + user.getUserStatus());
    }

    // Getters for profile update
    public String getName() {
        return nameField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getPhone() {
        return phoneField.getText();
    }

    // Getters for password change
    public String getCurrentPassword() {
        return currentPasswordField.getText();
    }

    public String getNewPassword() {
        return newPasswordField.getText();
    }

    public String getConfirmPassword() {
        return confirmPasswordField.getText();
    }

    public void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    public void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.setStyle(isSuccess
                ? "-fx-text-fill: #10b981; -fx-font-size: 14px; -fx-font-weight: bold;"
                : "-fx-text-fill: #ef4444; -fx-font-size: 14px; -fx-font-weight: bold;");
    }

    public void navigateToMainMenu() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Parent root = loader.load();
            stage.setTitle("MyArena - Dashboard");
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            System.err.println("Failed to load Main Menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}