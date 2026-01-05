package com.example.myarena.ui;

import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.facade.SubscriptionFacade; // Import the correct Facade
import javafx.application.Platform;

public class SubscribeController {

    private final SubscribeFrame view;
    private final SubscriptionFacade subscriptionFacade; // Use SubscriptionFacade

    public SubscribeController(SubscribeFrame view) {
        this.view = view;
        this.subscriptionFacade = SubscriptionFacade.getInstance(); // Initialize SubscriptionFacade
    }

    public void confirmSubscription() {
        try {
            SubscriptionPlan plan = view.getPlan();

            if (plan == null) {
                view.showMessage("Erreur : Aucun plan sélectionné", false);
                System.err.println("SubscribeController : Plan null");
                return;
            }

            System.out.println("SubscribeController : Tentative de souscription au plan - " + plan.getName());
            System.out.println("  → Plan ID : " + plan.getId());
            System.out.println("  → Renouvellement auto : " + view.isAutoRenewSelected());

            // Call subscribeToPlan on subscriptionFacade instead of sessionFacade
            boolean success = subscriptionFacade.subscribeToPlan(plan.getId());

            if (success) {
                view.showMessage("Abonnement activé avec succès !", true);
                System.out.println("SubscribeController : Abonnement créé avec succès");
            } else {
                view.showMessage("Erreur lors de la souscription. Veuillez réessayer.", false);
                System.err.println("SubscribeController : Échec de la souscription");
            }

        } catch (IllegalStateException e) {
            view.showMessage(e.getMessage(), false);
            System.err.println("SubscribeController : Erreur - " + e.getMessage());

        } catch (Exception e) {
            view.showMessage("Erreur inattendue : " + e.getMessage(), false);
            System.err.println("SubscribeController : Erreur inattendue - " + e.getMessage());
            e.printStackTrace();
        }
    }
}