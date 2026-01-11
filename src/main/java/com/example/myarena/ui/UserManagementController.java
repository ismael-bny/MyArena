package com.example.myarena.ui;

import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;
import com.example.myarena.facade.UserSession;
import com.example.myarena.services.UserManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class UserManagementController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Long> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colStatus;

    @FXML private ComboBox<UserRole> roleComboBox;
    @FXML private Button btnUpdateRole;
    @FXML private Button btnBack;
    @FXML private Label lblStatus;

    private final UserManager userManager = new UserManager();
    private User selectedUser;

    @FXML
    public void initialize() {
        setupTable();
        loadUsers();

        // Populate Role Combo
        roleComboBox.setItems(FXCollections.observableArrayList(UserRole.values()));

        // Listen for selection
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedUser = newVal;
            if (newVal != null) {
                roleComboBox.setValue(newVal.getRole());
                btnUpdateRole.setDisable(false);
            } else {
                btnUpdateRole.setDisable(true);
            }
        });

        btnUpdateRole.setOnAction(e -> handleUpdateRole());
        btnBack.setOnAction(e -> goBack());
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status")); // Ensure getter exists in User: getStatus() or getUserStatus()
    }

    private void loadUsers() {
        userTable.setItems(FXCollections.observableArrayList(userManager.getAllUsers()));
    }

    private void handleUpdateRole() {
        if (selectedUser == null) return;

        User currentUser = UserSession.getInstance().getUser();
        UserRole newRole = roleComboBox.getValue();

        if (newRole == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a role.");
            return;
        }

        try {
            userManager.changeUserRole(currentUser, selectedUser, newRole);

            // Refresh UI
            loadUsers();
            lblStatus.setText("Success: Role updated to " + newRole);
            lblStatus.setStyle("-fx-text-fill: green;");

            showAlert(Alert.AlertType.INFORMATION, "User role updated successfully.");

        } catch (Exception e) {
            lblStatus.setText("Error: " + e.getMessage());
            lblStatus.setStyle("-fx-text-fill: red;");
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}