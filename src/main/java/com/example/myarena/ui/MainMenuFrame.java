package com.example.myarena.ui;

import com.example.myarena.domain.UserRole;
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

    @FXML
    public void initialize() {
        // Personalize welcome message
        String username = UserSession.getInstance().getUser().getName();
        welcomeLabel.setText("Welcome back, " + username);

        // Bind Standard Actions
        reservationButton.setOnAction(e -> navigate(e, "/com/example/myarena/reservation-page.fxml"));
        if (myReservationsButton != null) {
            myReservationsButton.setOnAction(e -> navigate(e, "/com/example/myarena/my-reservations.fxml"));
        }

        if (profileButton != null) {
            profileButton.setOnAction(e -> navigate(e, "/com/example/myarena/profile-page.fxml"));
        }

        // Role-based visibility for Terrain Management
        UserRole role = UserSession.getInstance().getUser().getRole();

        //Equipment Management + Catalog logic
        if (equipmentButton != null) {
            if (role == UserRole.ADMIN || role == UserRole.OWNER) {
                equipmentButton.setText("Manage Equipment");
                equipmentButton.setOnAction(e -> navigate(e, "/com/example/myarena/product-management.fxml"));
            } else {
                equipmentButton.setText("Browse Catalog");
                equipmentButton.setOnAction(e -> navigate(e, "/com/example/myarena/product-catalog.fxml"));
            }
        }


        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            terrainButton.setVisible(false);
            terrainButton.setManaged(false);
        } else {
            terrainButton.setOnAction(e -> navigate(e, "/com/example/myarena/terrain-management.fxml"));
        }

        //Subscription Navigation Logic
        if (subscriptionButton != null) {
            if (role == UserRole.ADMIN || role == UserRole.OWNER) {
                subscriptionButton.setText("💎 Manage Plans");
                subscriptionButton.setOnAction(e -> navigate(e, "/com/example/myarena/plan-management.fxml"));
            } else {
                subscriptionButton.setText("💎 View Plans");
                subscriptionButton.setOnAction(e -> navigate(e, "/com/example/myarena/subscription-plans.fxml"));
            }
        }

        //Bouton Panier
        if (cartButton != null) {
            cartButton.setOnAction(this::openCart);
        }

        //Bouton Handle Orders
        if (ordersButton != null) {
            if (role == UserRole.ADMIN || role == UserRole.OWNER) {
                ordersButton.setText("📦 Manage Orders");
            } else {
                ordersButton.setText("📦 My Orders");
            }
            ordersButton.setOnAction(e -> navigate(e, "/com/example/myarena/order-validation-view.fxml"));
        }

        // Notification Center
        if (notificationCenterButton != null) {
            notificationCenterButton.setOnAction(e ->
                    navigate(e, "/com/example/myarena/notification-center.fxml")
            );

            try {
                int unread = 0;
                var notifs = com.example.myarena.facade.SessionFacade.getInstance().getNotifications();
                for (var n : notifs) {
                    if (n.getStatus() == com.example.myarena.domain.NotificationStatus.PENDING) {
                        unread++;
                    }
                }

                notificationCenterButton.setText(
                        unread > 0
                                ? "🔔 Notification Center (" + unread + ")"
                                : "🔔 Notification Center"
                );
            } catch (Exception ignored) {
                notificationCenterButton.setText("🔔 Notification Center");
            }
        }

        logoutButton.setOnAction(this::handleLogout);
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
            System.err.println("Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLogout(ActionEvent event) {
        UserSession.getInstance().cleanSession();
        navigate(event, "/com/example/myarena/login-page.fxml");
    }

    private void openCart(ActionEvent event) {
        try {
            navigate(event, "/com/example/myarena/cart-view.fxml");
        } catch (Exception e) {
            System.err.println("Erreur ouverture panier: " + e.getMessage());
            e.printStackTrace();
        }
    }
}