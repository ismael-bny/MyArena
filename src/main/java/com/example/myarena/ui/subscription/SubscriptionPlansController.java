package com.example.myarena.ui.subscription;

import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.facade.SessionFacade;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class SubscriptionPlansController {

    private final SubscriptionPlansFrame view;
    private final SessionFacade sessionFacade;

    public SubscriptionPlansController(SubscriptionPlansFrame view) {
        this.view = view;
        this.sessionFacade = SessionFacade.getInstance();
    }

    /**
     * Charge la liste des plans depuis la base de données
     */
    public void loadPlans() {
        try {
            System.out.println("SubscriptionPlansController : Chargement des plans...");
            
            List<SubscriptionPlan> plans = sessionFacade.getSubscriptionPlans();
            
            // Demander à la vue de mettre à jour l'affichage
            view.updateCards(plans);
            view.showMessage(plans.size() + " plan(s) chargé(s)", true);
            
            System.out.println("Plans chargés : " + plans.size());
        } catch (Exception e) {
            view.showMessage("Erreur lors du chargement des plans", false);
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gère le clic sur le bouton "S'inscrire"
     * Affiche une confirmation avant d'inscrire l'utilisateur
     */
    public void handleSubscribe(SubscriptionPlan plan) {
        // Vérifications de base
        if (plan == null) {
            System.err.println("SubscriptionPlansController : Plan null");
            return;
        }

        if (!plan.isActive()) {
            view.showMessage("Ce plan n'est pas actif", false);
            System.err.println("SubscriptionPlansController : Plan inactif - " + plan.getName());
            return;
        }

        System.out.println("SubscriptionPlansController : Demande de souscription au plan - " + plan.getName());
        
        Alert confirmationDialog = createSubscribeConfirmationDialog(plan);
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            subscribeToPlan(plan);
        }
    }
    
    /**
     * Gère le clic sur le bouton "Annuler"
     * Annule l'abonnement actif de l'utilisateur
     */
    public void handleCancel(SubscriptionPlan plan) {
        System.out.println("SubscriptionPlansController : Annulation de l'abonnement au plan - " + plan.getName());
        
        try {
            Long userId = sessionFacade.getCurrentUser().getId();
            boolean success = sessionFacade.cancelSubscription(userId);
            
            if (success) {
                view.showMessage("Abonnement annulé avec succès", true);
                loadPlans(); // Recharger pour changer les boutons "Annuler" → "S'inscrire"
            } else {
                view.showMessage("Erreur lors de l'annulation", false);
            }
        } catch (Exception e) {
            view.showMessage("Erreur : " + e.getMessage(), false);
            System.err.println("Erreur : " + e.getMessage());
        }
    }
    
    /**
     * Crée une popup de confirmation stylisée
     * Utilise les classes CSS pour le style
     */
    private Alert createSubscribeConfirmationDialog(SubscriptionPlan plan) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Confirmer l'inscription");
        dialog.setHeaderText(null);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10, 0, 0, 0));
        
        Label messageLabel = new Label("Voulez-vous vous inscrire au plan :");
        messageLabel.getStyleClass().add("dialog-message");
        
        Label planNameLabel = new Label(plan.getName());
        planNameLabel.getStyleClass().add("dialog-plan-name");
        
        Label priceLabel = new Label("Prix : " + plan.getPrice() + " € / " + getDurationText(plan.getDurationMonths()));
        priceLabel.getStyleClass().add("dialog-message");
        
        content.getChildren().addAll(messageLabel, planNameLabel, priceLabel);
        dialog.getDialogPane().setContent(content);
        
        String cssPath = getClass().getResource("/com/example/myarena/styles/myarena-styles.css").toExternalForm();
        dialog.getDialogPane().getStylesheets().add(cssPath);
        dialog.getDialogPane().getStyleClass().add("confirmation-dialog");
        
        javafx.scene.control.Button confirmBtn = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        confirmBtn.setText("S'inscrire");
        confirmBtn.getStyleClass().clear();
        confirmBtn.getStyleClass().add("btn-primary");
        
        javafx.scene.control.Button cancelBtn = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelBtn.setText("Annuler");
        cancelBtn.getStyleClass().clear();
        cancelBtn.getStyleClass().add("cancel-button");
        
        return dialog;
    }
    
    /**
     * Effectue l'inscription au plan dans la base de données
     * Méthode privée car c'est de la logique interne
     */
    private void subscribeToPlan(SubscriptionPlan plan) {
        try {
            boolean success = sessionFacade.subscribeToPlan(plan.getId());
            
            if (success) {
                view.showMessage("Inscription réussie au plan : " + plan.getName(), true);
                loadPlans(); 
            } else {
                view.showMessage("Erreur lors de l'inscription", false);
            }
        } catch (Exception e) {
            view.showMessage("Erreur : " + e.getMessage(), false);
            System.err.println("Erreur : " + e.getMessage());
        }
    }
    
    /**
     * Convertit la durée en mois en texte lisible
     */
    private String getDurationText(int months) {
        switch (months) {
            case 1: return "mois";
            case 3: return "trimestre";
            case 6: return "semestre";
            case 12: return "an";
            default: return months + " mois";
        }
    }
}
