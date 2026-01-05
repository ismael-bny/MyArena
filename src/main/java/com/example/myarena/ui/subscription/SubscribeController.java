package com.example.myarena.ui.subscription;

import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.facade.SessionFacade;

public class SubscribeController {

    private final SubscribeFrame view;
    private final SessionFacade sessionFacade;

    public SubscribeController(SubscribeFrame view) {
        this.view = view;
        this.sessionFacade = SessionFacade.getInstance();
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

            boolean success = sessionFacade.subscribeToPlan(plan.getId());

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
