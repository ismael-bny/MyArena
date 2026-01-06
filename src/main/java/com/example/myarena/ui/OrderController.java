package com.example.myarena.ui;

import com.example.myarena.domain.Order;
import com.example.myarena.domain.OrderStatus;
import com.example.myarena.domain.User;
import com.example.myarena.domain.UserRole;
import com.example.myarena.facade.OrderFacade;
import com.example.myarena.facade.SessionFacade;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.stream.Collectors;

public class OrderController {

    private final OrderFrame frame;
    private final OrderFacade orderFacade;
    private final SessionFacade sessionFacade;
    private final User currentUser;
    private List<Order> allOrders;

    public OrderController(OrderFrame frame) {
        this.frame = frame;
        this.orderFacade = OrderFacade.getInstance();
        this.sessionFacade = SessionFacade.getInstance();
        this.currentUser = sessionFacade.getCurrentUser();
    }

    public void loadOrders() {
        if (currentUser == null) {
            frame.showError("User not logged in");
            return;
        }

        if (currentUser.getRole() == UserRole.OWNER || currentUser.getRole() == UserRole.ADMIN) {
            allOrders = orderFacade.getAllOrders();
        } else {
            allOrders = orderFacade.getOrdersByUserId(currentUser.getId());
        }

        frame.updateOrders(allOrders);
    }

    public void filterOrders(String status) {
        if (allOrders == null) return;

        List<Order> filtered;
        if (status.equals("All")) {
            filtered = allOrders;
        } else {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            filtered = allOrders.stream()
                    .filter(order -> order.getStatus() == orderStatus)
                    .collect(Collectors.toList());
        }
        frame.updateOrders(filtered);
    }

    public String getUserName(Long userId) {
        // TODO: Get from UserManager when implemented
        return "User #" + userId;
    }

    /** Get items count for an order */
    public int getItemsCount(Order order) {
        // TODO: Get from CartItems when implemented
        return 1;
    }

    /** Handle view details action */
    public void handleViewDetails(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            frame.showInfo("This order has already been processed");
            return;
        }

        // Create dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Order Details");
        dialog.setHeaderText("Order: " + order.getReferenceNumber());

        // Content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        Label customerLabel = new Label("Customer: " + getUserName(order.getUserId()));
        Label dateLabel = new Label("Date: " + order.getOrderDate());
        Label amountLabel = new Label("Amount: $" + String.format("%.2f", order.getAmount()));
        Label statusLabel = new Label("Status: " + order.getStatus());

        content.getChildren().addAll(customerLabel, dateLabel, amountLabel, statusLabel);

        dialog.getDialogPane().setContent(content);

        // Buttons
        ButtonType validateBtn = new ButtonType("Validate Payment", ButtonBar.ButtonData.OK_DONE);
        ButtonType rejectBtn = new ButtonType("Reject Order", ButtonBar.ButtonData.OTHER);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(validateBtn, rejectBtn, cancelBtn);

        // Handle response
        dialog.showAndWait().ifPresent(response -> {
            if (response == validateBtn) {
                validateOrder(order);
            } else if (response == rejectBtn) {
                rejectOrder(order);
            }
        });
    }

    /** Validate order */
    private void validateOrder(Order order) {
        try {
            orderFacade.validateOrder(order);
            frame.showSuccess("Order validated successfully");
            loadOrders(); // Refresh list
        } catch (Exception e) {
            frame.showError("Failed to validate order: " + e.getMessage());
        }
    }

    /** Reject order */
    private void rejectOrder(Order order) {
        try {
            orderFacade.rejectOrder(order);
            frame.showSuccess("Order rejected successfully");
            loadOrders(); // Refresh list
        } catch (Exception e) {
            frame.showError("Failed to reject order: " + e.getMessage());
        }
    }
}
