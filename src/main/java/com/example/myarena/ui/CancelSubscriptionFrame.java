package com.example.myarena.ui;

import com.example.myarena.domain.Subscription;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CancelSubscriptionFrame {

    @FXML
    private VBox subscriptionCard;

    @FXML
    private VBox noSubscriptionBox;

    @FXML
    private Label planNameLabel;

    @FXML
    private Label statusBadge;

    @FXML
    private Label priceLabel;

    @FXML
    private Label startDateLabel;

    @FXML
    private Label endDateLabel;

    @FXML
    private Label autoRenewLabel;

    @FXML
    private Label autoRenewIcon;

    @FXML
    private Label remainingDaysLabel;

    @FXML
    private Label statusLabel;

    private CancelSubscriptionController controller;
    private Subscription subscription;

    private static final String VERT_ACTIF = "#28A745";
    private static final String ROUGE_INACTIF = "#DC3545";

    public CancelSubscriptionFrame() {
    }

    @FXML
    public void initialize() {
        System.out.println("CancelSubscriptionFrame : initialize() appelé");
        this.controller = new CancelSubscriptionController(this);
        controller.loadActiveSubscription();
    }

    @FXML
    private void handleCancelSubscription() {
        if (controller != null) {
            controller.confirmCancellation();
        }
    }

    @FXML
    private void handleViewPlans() {
        System.out.println("Redirection vers SubscriptionPlansFrame");
        SubscriptionPlansFrame plansFrame = new SubscriptionPlansFrame();
        plansFrame.show();
    }

    @FXML
    private void handleClose() {
        System.out.println("Fermeture de CancelSubscriptionFrame");
    }

    public void displaySubscription(Subscription subscription) {
        this.subscription = subscription;

        if (subscription == null) {
            subscriptionCard.setVisible(false);
            subscriptionCard.setManaged(false);
            noSubscriptionBox.setVisible(true);
            noSubscriptionBox.setManaged(true);
            
            System.out.println("CancelSubscriptionFrame : Aucun abonnement actif");
            return;
        }
        subscriptionCard.setVisible(true);
        subscriptionCard.setManaged(true);
        noSubscriptionBox.setVisible(false);
        noSubscriptionBox.setManaged(false);

        updateDisplay();
    }

    public void showMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        
        if (isSuccess) {
            statusLabel.setStyle("-fx-text-fill: " + VERT_ACTIF + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: " + ROUGE_INACTIF + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        }
    }

    public Subscription getSubscription() {
        return subscription;
    }

    private void updateDisplay() {
        if (subscription == null || subscription.getPlan() == null) {
            System.err.println("CancelSubscriptionFrame : Subscription ou Plan null");
            return;
        }
        planNameLabel.setText(subscription.getPlan().getName());
        String status = subscription.getStatus().toString();
        statusBadge.setText(status);
        
        if ("ACTIVE".equals(status)) {
            statusBadge.setStyle("-fx-background-color: " + VERT_ACTIF + "; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            statusBadge.setStyle("-fx-background-color: " + ROUGE_INACTIF + "; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15; -fx-font-size: 11px; -fx-font-weight: bold;");
        }

        DecimalFormat df = new DecimalFormat("0.00");
        String durationText = subscription.getPlan().getDurationMonths() == 1 ? "mois" : "mois";
        priceLabel.setText(df.format(subscription.getPlan().getPrice()) + " € / " + durationText);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDateLabel.setText(subscription.getStartDate().format(formatter));
        endDateLabel.setText(subscription.getEndDate().format(formatter));

        if (subscription.isAutoRenew()) {
            autoRenewLabel.setText("Activé");
            autoRenewIcon.setText("✓");
            autoRenewIcon.setStyle("-fx-text-fill: " + VERT_ACTIF + "; -fx-font-weight: bold; -fx-font-size: 18px;");
        } else {
            autoRenewLabel.setText("Désactivé");
            autoRenewIcon.setText("✗");
            autoRenewIcon.setStyle("-fx-text-fill: " + ROUGE_INACTIF + "; -fx-font-weight: bold; -fx-font-size: 18px;");
        }

        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), subscription.getEndDate());
        remainingDaysLabel.setText(daysRemaining + " jour(s)");

        System.out.println("CancelSubscriptionFrame : Affichage de l'abonnement - " + subscription.getPlan().getName());
    }

    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CancelSubscriptionFrame.class.getResource("/com/example/myarena/cancel-subscription.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 700, 600));
            stage.setTitle("Annuler mon Abonnement - MYARENA");
            stage.show();

            System.out.println("CancelSubscriptionFrame : Fenêtre ouverte");

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de cancel-subscription.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
