package com.example.myarena.ui;

import com.example.myarena.domain.Order;
import com.example.myarena.domain.OrderStatus;
import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;
import com.example.myarena.facade.SessionFacade;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/** Frame for order validation view - contains @FXML elements and UI construction */
public class OrderFrame {

    @FXML
    private VBox ordersContainer;

    @FXML
    private ComboBox<String> statusFilter;

    private OrderController controller;
    private User currentUser;

    /** Constructor - creates controller and passes itself */
    public OrderFrame() {
        this.controller = new OrderController(this);
        SessionFacade sessionFacade = SessionFacade.getInstance();
        this.currentUser = sessionFacade.getCurrentUser();
    }

    /** Initialize method called after FXML loading */
    @FXML
    public void initialize() {
        System.out.println("OrderFrame initialized");
        
        // Setup filters and load data
        if (controller != null) {
            setupFilters();
            controller.loadOrders();
        }
    }

    /** Setup filter options */
    private void setupFilters() {
        statusFilter.getItems().addAll("All", "PENDING", "PAID", "CANCELLED");
        statusFilter.setValue("All");
    }

    /** Handle filter change */
    @FXML
    public void handleFilterChange() {
        if (controller != null) {
            controller.filterOrders(statusFilter.getValue());
        }
    }

    /** Handle back to menu */
    @FXML
    public void handleBackToMenu() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/com/example/myarena/main-menu.fxml")
            );
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ordersContainer.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur navigation vers menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Display orders in the UI */
    public void updateOrders(List<Order> orders) {
        ordersContainer.getChildren().clear();

        if (orders == null || orders.isEmpty()) {
            Label emptyLabel = new Label("No orders found");
            emptyLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14px;");
            ordersContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Order order : orders) {
            ordersContainer.getChildren().add(createOrderCard(order));
        }
    }

    /** Create an order card matching the mockup */
    private VBox createOrderCard(Order order) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #E5E7EB; -fx-border-radius: 8; -fx-border-width: 1;");

        // Header row with order reference and status badges
        HBox headerRow = new HBox(15);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label orderRef = new Label(order.getReferenceNumber());
        orderRef.setFont(Font.font("System", FontWeight.BOLD, 14));
        orderRef.setStyle("-fx-text-fill: #111827;");

        // Status badge
        Label statusBadge = createStatusBadge(order.getStatus());

        headerRow.getChildren().addAll(orderRef, statusBadge);

        // Details row
        HBox detailsRow = new HBox(40);
        detailsRow.setAlignment(Pos.CENTER_LEFT);
        detailsRow.setPadding(new Insets(10, 0, 0, 0));

        // Customer info
        VBox customerBox = new VBox(5);
        Label customerLabel = new Label("Customer:");
        customerLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        Label customerName = new Label(controller.getUserName(order.getUserId()));
        customerName.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px;");
        customerBox.getChildren().addAll(customerLabel, customerName);

        // Date info
        VBox dateBox = new VBox(5);
        Label dateLabel = new Label("Date:");
        dateLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        Label dateValue = new Label(order.getOrderDate());
        dateValue.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px;");
        dateBox.getChildren().addAll(dateLabel, dateValue);

        // Items info
        VBox itemsBox = new VBox(5);
        Label itemsLabel = new Label("Items:");
        itemsLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        Label itemsValue = new Label(controller.getItemsCount(order) + " item(s)");
        itemsValue.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px;");
        itemsBox.getChildren().addAll(itemsLabel, itemsValue);

        // Total info
        VBox totalBox = new VBox(5);
        Label totalLabel = new Label("Total:");
        totalLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        Label totalValue = new Label(String.format("$%.2f", order.getAmount()));
        totalValue.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 13px; -fx-font-weight: bold;");
        totalBox.getChildren().addAll(totalLabel, totalValue);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // View Details button (only for Owner/Admin)
        if (currentUser != null && (currentUser.getRole() == UserRole.OWNER || currentUser.getRole() == UserRole.ADMIN)) {
            Button viewDetailsBtn = new Button("View Details");
            viewDetailsBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 16; -fx-font-size: 13px; -fx-cursor: hand;");
            viewDetailsBtn.setOnAction(e -> controller.handleViewDetails(order));
            detailsRow.getChildren().addAll(customerBox, dateBox, itemsBox, totalBox, spacer, viewDetailsBtn);
        } else {
            detailsRow.getChildren().addAll(customerBox, dateBox, itemsBox, totalBox);
        }

        card.getChildren().addAll(headerRow, detailsRow);
        return card;
    }

    /** Create status badge */
    private Label createStatusBadge(OrderStatus status) {
        Label badge = new Label(status.toString());
        badge.setPadding(new Insets(4, 12, 4, 12));
        badge.setStyle("-fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;");

        switch (status) {
            case PENDING:
                badge.setStyle(badge.getStyle() + "-fx-background-color: #FEF3C7; -fx-text-fill: #92400E;");
                break;
            case PAID:
                badge.setStyle(badge.getStyle() + "-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF;");
                break;
            case CANCELLED:
                badge.setStyle(badge.getStyle() + "-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;");
                break;
            case SHIPPED:
                badge.setStyle(badge.getStyle() + "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;");
                break;
        }

        return badge;
    }

    /** Show success message */
    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Show error message */
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Show info message */
    public void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
