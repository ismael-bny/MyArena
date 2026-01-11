package com.example.myarena.ui;

import com.example.myarena.domain.UserRole;
import com.example.myarena.facade.SessionFacade;
import com.example.myarena.facade.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuFrame {

    @FXML private Button reservationButton;
    @FXML private Button terrainButton;
    @FXML private Button subscriptionButton;
    @FXML private Button cartButton;
    @FXML private Button ordersButton;
    @FXML private Button logoutButton;
    @FXML private Label welcomeLabel;
    @FXML private Button myReservationsButton;
    @FXML private Button profileButton;
    @FXML private Button notificationCenterButton;
    @FXML private Button equipmentButton;
    @FXML private Button btnUserManagement;
    @FXML private Button tournamentButton;

    @FXML
    public void initialize() {
        if (UserSession.getInstance().getUser() != null) {
            String username = UserSession.getInstance().getUser().getName();
            if (welcomeLabel != null) welcomeLabel.setText("Welcome back, " + username + "!");
        }

        //Bind Navigation Actions
        if(reservationButton != null) reservationButton.setOnAction(e -> navigate(e, "/com/example/myarena/reservation-page.fxml"));
        if(myReservationsButton != null) myReservationsButton.setOnAction(e -> navigate(e, "/com/example/myarena/my-reservations.fxml"));
        if(profileButton != null) profileButton.setOnAction(e -> navigate(e, "/com/example/myarena/profile-page.fxml"));
        if(cartButton != null) cartButton.setOnAction(e -> navigate(e, "/com/example/myarena/cart-view.fxml"));
        if(ordersButton != null) ordersButton.setOnAction(e -> navigate(e, "/com/example/myarena/order-validation-view.fxml"));

        //Role-Based Logic (Hiding buttons for non-owners)
        UserRole role = UserSession.getInstance().getUser().getRole();

        if (terrainButton != null) {
            if (role != UserRole.OWNER && role != UserRole.ADMIN) {
                // Hide and remove from layout
                terrainButton.setVisible(false);
                terrainButton.setManaged(false);
            } else {
                terrainButton.setOnAction(e -> navigate(e, "/com/example/myarena/terrain-management.fxml"));
            }
        }

        //Equipment Button Logic (Catalog vs Management)
        if (equipmentButton != null) {
            if (role == UserRole.ADMIN || role == UserRole.OWNER) {
                equipmentButton.setOnAction(e -> navigate(e, "/com/example/myarena/product-management.fxml"));
            } else {
                equipmentButton.setOnAction(e -> navigate(e, "/com/example/myarena/product-catalog.fxml"));
            }
        }

        //Subscription Logic
        if (subscriptionButton != null) {
            if (role == UserRole.ADMIN || role == UserRole.OWNER) {
                subscriptionButton.setOnAction(e -> navigate(e, "/com/example/myarena/plan-management.fxml"));
            } else {
                subscriptionButton.setOnAction(e -> navigate(e, "/com/example/myarena/subscription-plans.fxml"));
            }
        }

        //Notification Center
        if (notificationCenterButton != null) {
            notificationCenterButton.setOnAction(e -> navigate(e, "/com/example/myarena/notification-center.fxml"));
            updateNotificationBadge();
        }

        //User Management Logic
        if (btnUserManagement != null) {
            if (role == UserRole.ADMIN) {
                btnUserManagement.setOnAction(e -> navigate(e, "/com/example/myarena/user-management.fxml"));
            } else {
                // Hide for non-admins
                btnUserManagement.setVisible(false);
                btnUserManagement.setManaged(false);
            }
        }

        // Tournament Button
        if (tournamentButton != null) {
            tournamentButton.setOnAction(e -> navigate(e, "/com/example/myarena/tournament-list.fxml"));
        }

        if (logoutButton != null) logoutButton.setOnAction(this::handleLogout);
    }

    private void updateNotificationBadge() {
        try {
            int unread = 0;
            var notifs = SessionFacade.getInstance().getNotifications();
            for (var n : notifs) {
                if (n.getStatus() == com.example.myarena.domain.NotificationStatus.PENDING) {
                    unread++;
                }
            }
            if (unread > 0) {
                notificationCenterButton.setText("🔔 (" + unread + ")");
                notificationCenterButton.setStyle("-fx-text-fill: #d4183d; -fx-font-weight: bold;");
            }
        } catch (Exception ignored) {}
    }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error navigating to: " + fxmlPath);
        }
    }

    private void handleLogout(ActionEvent event) {
        UserSession.getInstance().cleanSession();
        navigate(event, "/com/example/myarena/login-page.fxml");
    }
}