package com.example.myarena.ui;

import com.example.myarena.domain.Subscription;
import com.example.myarena.facade.SubscriptionFacade;
import com.example.myarena.ui.CancelSubscriptionFrame;

public class CancelSubscriptionController {
    private final CancelSubscriptionFrame view;
    private final SubscriptionFacade subscriptionFacade;

    public CancelSubscriptionController(CancelSubscriptionFrame view) {
        this.view = view;
        this.subscriptionFacade = SubscriptionFacade.getInstance();
    }

    public void loadActiveSubscription() {
        try {
            Subscription subscription = subscriptionFacade.getActiveSubscription();
            view.displaySubscription(subscription);

            if (subscription == null) {
                view.showMessage("Aucun abonnement actif trouvé", false);
            }
        } catch (Exception e) {
            view.showMessage("❌ Erreur lors du chargement", false);
        }
    }

    public void confirmCancellation() {
        try {
            boolean success = subscriptionFacade.cancelSubscription();

            if (success) {
                view.showMessage("✅ Abonnement annulé avec succès !", true);
                loadActiveSubscription();
            } else {
                view.showMessage("❌ Aucun abonnement actif à annuler", false);
            }
        } catch (Exception e) {
            view.showMessage("❌ Erreur inattendue : " + e.getMessage(), false);
        }
    }
}