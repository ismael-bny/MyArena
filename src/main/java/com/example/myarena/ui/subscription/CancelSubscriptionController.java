package com.example.myarena.ui.subscription;

import com.example.myarena.domain.Subscription;
import com.example.myarena.domain.User;
import com.example.myarena.facade.SessionFacade;

public class CancelSubscriptionController {

    private final CancelSubscriptionFrame view;
    private final SessionFacade sessionFacade;

    public CancelSubscriptionController(CancelSubscriptionFrame view) {
        this.view = view;
        this.sessionFacade = SessionFacade.getInstance();
    }

    public void loadActiveSubscription() {
        try {
            System.out.println("CancelSubscriptionController : Chargement de l'abonnement actif...");

            User currentUser = sessionFacade.getCurrentUser();
            if (currentUser == null) {
                view.showMessage("❌ Aucun utilisateur connecté", false);
                view.displaySubscription(null);
                return;
            }

            Subscription subscription = sessionFacade.getActiveSubscription(currentUser.getId());
            view.displaySubscription(subscription);

            if (subscription == null) {
                System.out.println("CancelSubscriptionController : Aucun abonnement actif trouvé");
            } else {
                System.out.println("CancelSubscriptionController : Abonnement chargé - " + subscription.getPlan().getName());
            }

        } catch (Exception e) {
            view.showMessage("❌ Erreur lors du chargement", false);
            System.err.println("CancelSubscriptionController : Erreur - " + e.getMessage());
        }
    }

    public void confirmCancellation() {
        try {
            User currentUser = sessionFacade.getCurrentUser();
            
            if (currentUser == null) {
                view.showMessage("❌ Aucun utilisateur connecté", false);
                System.err.println("CancelSubscriptionController : Utilisateur null");
                return;
            }

            Subscription subscription = view.getSubscription();
            if (subscription == null) {
                view.showMessage("❌ Aucun abonnement à annuler", false);
                System.err.println("CancelSubscriptionController : Aucun abonnement actif");
                return;
            }

            System.out.println("CancelSubscriptionController : Tentative d'annulation pour l'utilisateur ID : " + currentUser.getId());

            boolean success = sessionFacade.cancelSubscription(currentUser.getId());

            if (success) {
                view.showMessage("✅ Abonnement annulé avec succès !", true);
                System.out.println("CancelSubscriptionController : Abonnement annulé");                
                loadActiveSubscription();
                
            } else {
                view.showMessage("❌ Aucun abonnement actif à annuler", false);
                System.out.println("CancelSubscriptionController : Aucun abonnement actif trouvé");
            }
        } catch (Exception e) {
            view.showMessage("❌ Erreur inattendue : " + e.getMessage(), false);
            System.err.println("CancelSubscriptionController : Erreur - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
