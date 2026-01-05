package com.example.myarena.ui;

import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.facade.SubscriptionFacade;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class SubscriptionPlansController {

    private final SubscriptionPlansFrame view;
    private final SubscriptionFacade subscriptionFacade;

    public SubscriptionPlansController(SubscriptionPlansFrame view) {
        this.view = view;
        this.subscriptionFacade = SubscriptionFacade.getInstance();
    }

    public void loadPlans() {
        try {
            List<SubscriptionPlan> plans = subscriptionFacade.getSubscriptionPlans(); //
            view.updateCards(plans);
            view.showMessage(plans.size() + " plan(s) chargé(s)", true);
        } catch (Exception e) {
            view.showMessage("Erreur lors du chargement des plans", false);
        }
    }

    public void handleSubscribe(SubscriptionPlan plan) {
        if (plan == null || !plan.isActive()) {
            view.showMessage("Ce plan n'est pas disponible", false);
            return;
        }

        Alert confirmationDialog = createSubscribeConfirmationDialog(plan);
        Optional<ButtonType> result = confirmationDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            subscribeToPlan(plan);
        }
    }

    public void handleCancel(SubscriptionPlan plan) {
        try {
            boolean success = subscriptionFacade.cancelSubscription();

            if (success) {
                view.showMessage("Abonnement annulé avec succès", true);
                loadPlans();
            } else {
                view.showMessage("Erreur lors de l'annulation", false);
            }
        } catch (Exception e) {
            view.showMessage("Erreur : " + e.getMessage(), false);
        }
    }

    private void subscribeToPlan(SubscriptionPlan plan) {
        try {
            // Facade handles the logged-in user ID internally
            boolean success = subscriptionFacade.subscribeToPlan(plan.getId());

            if (success) {
                view.showMessage("Inscription réussie au plan : " + plan.getName(), true);
                loadPlans();
            } else {
                view.showMessage("Erreur lors de l'inscription", false);
            }
        } catch (Exception e) {
            view.showMessage("Erreur : " + e.getMessage(), false);
        }
    }

    private Alert createSubscribeConfirmationDialog(SubscriptionPlan plan) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Confirmer l'inscription");
        dialog.setHeaderText(null);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10, 0, 0, 0));
        content.getChildren().addAll(
                new Label("Voulez-vous vous inscrire au plan :"),
                new Label(plan.getName())
        );
        dialog.getDialogPane().setContent(content);

        return dialog;
    }
}