package com.example.myarena.ui;

import com.example.myarena.domain.SubscriptionPlan;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SubscribeFrame {

    @FXML
    private Label planNameLabel;

    @FXML
    private Label planPriceLabel;

    @FXML
    private Label planDurationLabel;

    @FXML
    private Label durationDetailsLabel;

    @FXML
    private Label startDateLabel;

    @FXML
    private Label endDateLabel;

    @FXML
    private CheckBox autoRenewCheckBox;

    @FXML
    private Label statusLabel;

    private SubscribeController controller;
    private SubscriptionPlan plan;

    private static final String VERT_ACTIF = "#28A745";
    private static final String ROUGE_INACTIF = "#DC3545";

    public SubscribeFrame() {
    }

    @FXML
    public void initialize() {
        System.out.println("SubscribeFrame : initialize() appelé");
        this.controller = new SubscribeController(this);
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
        updateDisplay();
    }

    @FXML
    private void handleConfirm() {
        if (controller != null) {
            controller.confirmSubscription();
        }
    }

    @FXML
    private void handleCancel() {
        System.out.println("Annulation de la souscription");
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public boolean isAutoRenewSelected() {
        return autoRenewCheckBox.isSelected();
    }

    public void showMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        
        if (isSuccess) {
            statusLabel.setStyle("-fx-text-fill: " + VERT_ACTIF + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: " + ROUGE_INACTIF + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // MÉTHODES PRIVÉES pour mettre à jour l'affichage
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Met à jour l'affichage avec les détails du plan.
     */
    private void updateDisplay() {
        if (plan == null) {
            System.err.println("SubscribeFrame : Aucun plan défini");
            return;
        }

        // Nom du plan
        planNameLabel.setText(plan.getName());

        // Prix formaté
        DecimalFormat df = new DecimalFormat("0.00");
        planPriceLabel.setText(df.format(plan.getPrice()) + "€");

        // Durée
        String durationText = plan.getDurationMonths() == 1 ? "mois" : "mois";
        planDurationLabel.setText("/ " + durationText);
        durationDetailsLabel.setText(plan.getDurationMonths() + " mois");

        // Dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(plan.getDurationMonths());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDateLabel.setText(startDate.format(formatter));
        endDateLabel.setText(endDate.format(formatter));

        System.out.println("SubscribeFrame : Affichage du plan - " + plan.getName());
    }

    public static void show(SubscriptionPlan plan) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SubscribeFrame.class.getResource("/com/example/myarena/subscribe.fxml")
            );
            Parent root = loader.load();

            // Récupérer le controller et définir le plan
            SubscribeFrame controller = loader.getController();
            controller.setPlan(plan);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 700));
            stage.setTitle("Confirmer l'Abonnement - MYARENA");
            stage.show();

            System.out.println("SubscribeFrame : Fenêtre ouverte pour le plan - " + plan.getName());

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de subscribe.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
