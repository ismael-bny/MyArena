package com.example.myarena.ui;

import com.example.myarena.domain.Notification;
import com.example.myarena.domain.NotificationStatus;
import com.example.myarena.domain.NotificationType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationCenterFrame {

    @FXML private Label subtitleLabel;
    @FXML private Label messageLabel;

    @FXML private Button backButton;
    @FXML private Button allButton;
    @FXML private Button unreadButton;

    @FXML private VBox cardsContainer;

    private final NotificationCenterController controller;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy, HH:mm");

    public NotificationCenterFrame() {
        this.controller = new NotificationCenterController(this);
    }

    @FXML
    public void initialize() {
        allButton.setOnAction(e -> controller.onFilterAll());
        unreadButton.setOnAction(e -> controller.onFilterUnread());

        controller.loadNotifications();
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================== API utilisée par le controller ==================

    public void showMessage(String message, boolean success) {
        messageLabel.setText(message == null ? "" : message);
        messageLabel.setStyle(
                success
                        ? "-fx-text-fill: #16a34a;"  // vert
                        : "-fx-text-fill: #dc2626;"  // rouge
        );
    }

    public void updateUnreadCount(int unreadCount) {
        subtitleLabel.setText("You have " + unreadCount + " unread notifications");
        unreadButton.setText("Unread (" + unreadCount + ")");
    }

    /** Met à jour le style des filtres : All actif ou Unread actif. */
    public void setActiveFilterAll(boolean isAllActive) {
        if (isAllActive) {
            allButton.setStyle("-fx-background-color: #329aff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 10;");
            unreadButton.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #111827; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 10;");
        } else {
            allButton.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #111827; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 10;");
            unreadButton.setStyle("-fx-background-color: #329aff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 10;");
        }
    }

    /** Affiche les cards. */
    public void showNotifications(List<Notification> notifications) {
        cardsContainer.getChildren().clear();

        if (notifications == null || notifications.isEmpty()) {
            cardsContainer.getChildren().add(buildEmptyState());
            return;
        }

        for (Notification n : notifications) {
            cardsContainer.getChildren().add(buildCard(n));
        }
    }

    public void openNotificationDetails(Notification n) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Notification Details");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        Label title = new Label(n.getTitle());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label meta = new Label(buildMetaText(n));
        meta.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        TextArea msg = new TextArea(n.getMessage() == null ? "" : n.getMessage());
        msg.setEditable(false);
        msg.setWrapText(true);
        msg.setPrefRowCount(6);
        msg.setStyle("-fx-control-inner-background: #f9fafb; -fx-background-radius: 12;");

        VBox content = new VBox(10, title, meta, msg);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }


    // ================== UI builders ==================

    private Node buildEmptyState() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(16));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 16;" +
                        "-fx-background-radius: 16;"
        );

        Label t = new Label("No notifications");
        t.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label s = new Label("You're all caught up.");
        s.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        box.getChildren().addAll(t, s);
        return box;
    }

    private VBox buildCard(Notification n) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 16;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 12, 0, 0, 4);"
        );

        // petit accent à gauche si unread
        if (n.getStatus() == NotificationStatus.PENDING) {
            card.setStyle(card.getStyle() + "-fx-border-color: #ff8c00; -fx-border-width: 0 0 0 4;");
        }

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label typePill = buildTypePill(n.getType());
        Label newPill = buildNewPill(n.getStatus());

        Label date = new Label(n.getCreatedAt() != null ? dtf.format(n.getCreatedAt()) : "-");
        date.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        HBox left = new HBox(8, typePill);
        if (newPill != null) left.getChildren().add(newPill);
        left.getChildren().add(date);
        left.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewBtn = new Button("👁  View");
        viewBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #329aff; -fx-font-weight: bold;");
        viewBtn.setOnAction(e -> controller.onViewNotification(n));

        top.getChildren().addAll(left, spacer, viewBtn);

        // Title + preview (message)
        Label title = new Label(n.getTitle() == null ? "-" : n.getTitle());
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label preview = new Label(preview(n.getMessage(), 120));
        preview.setWrapText(true);
        preview.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        card.getChildren().addAll(top, title, preview);
        return card;
    }

    private Label buildTypePill(NotificationType type) {
        String txt = (type == null) ? "Other" : niceType(type);
        String[] colors = typeColors(type);

        Label l = new Label(txt);
        l.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-text-fill: " + colors[0] + ";" +
                        "-fx-background-color: " + colors[1] + ";" +
                        "-fx-padding: 3 10;" +
                        "-fx-background-radius: 999;"
        );
        return l;
    }

    private Label buildNewPill(NotificationStatus status) {
        if (status != NotificationStatus.PENDING) return null;
        Label l = new Label("New");
        l.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-text-fill: #b45309;" +
                        "-fx-background-color: #ffedd5;" +
                        "-fx-padding: 3 10;" +
                        "-fx-background-radius: 999;"
        );
        return l;
    }

    private String buildMetaText(Notification n) {
        String type = n.getType() == null ? "Other" : niceType(n.getType());
        String status = n.getStatus() == null ? "-" : n.getStatus().name();
        String date = n.getCreatedAt() == null ? "-" : dtf.format(n.getCreatedAt());
        return type + " • " + status + " • " + date;
    }

    private String preview(String msg, int max) {
        if (msg == null) return "";
        String s = msg.trim();
        if (s.length() <= max) return s;
        return s.substring(0, max) + "…";
    }

    private String niceType(NotificationType t) {
        return switch (t) {
            case RESERVATION -> "Reservation";
            case CANCELLATION -> "Cancellation";
            case PAYMENT_VALIDATION -> "Payment";
            case TOURNAMENT_UPDATE -> "Tournament";
            case SUBSCRIPTION_CHANGE -> "Subscription";
            case ACCOUNT_UPDATE -> "";
            case OTHER -> "System";
        };
    }

    private String[] typeColors(NotificationType t) {
        if (t == null) return new String[]{"#374151", "#f3f4f6"};
        return switch (t) {
            case PAYMENT_VALIDATION -> new String[]{"#166534", "#dcfce7"};     // vert doux
            case RESERVATION -> new String[]{"#1d4ed8", "#dbeafe"};           // bleu doux
            case SUBSCRIPTION_CHANGE -> new String[]{"#6d28d9", "#ede9fe"};   // violet doux
            case CANCELLATION -> new String[]{"#b91c1c", "#fee2e2"};          // rouge doux
            case TOURNAMENT_UPDATE -> new String[]{"#0f766e", "#ccfbf1"};     // teal
            case ACCOUNT_UPDATE -> new String[]{"#b45309", "#ffedd5"};       // orange doux
            case OTHER -> new String[]{"#374151", "#f3f4f6"};                 // gris
        };
    }
}
