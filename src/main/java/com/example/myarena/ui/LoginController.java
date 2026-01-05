package com.example.myarena.ui;

import com.example.myarena.domain.UserRole;
import com.example.myarena.facade.SessionFacade;

import com.example.myarena.ui.subscription.PlanManagementFrame;
import com.example.myarena.ui.subscription.SubscriptionPlansFrame;

public class LoginController {
    private LoginFrame view;
    private SessionFacade sessionFacade;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.sessionFacade = SessionFacade.getInstance();
    }

    public void login() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (sessionFacade.login(username, password)) {
            view.showMessage("Login successful", true);
        // Delegate to Facade
        boolean success = sessionFacade.login(username, password);

        if (success) {
            view.showMessage("Login successful for user: " + username, true);
            System.out.println("Login successful for user: " + username);

            // Redirect based on role
            UserRole role = sessionFacade.getCurrentUser().getRole();
            if (role == UserRole.ADMIN || role == UserRole.OWNER) {
                // Admins/Owners go to management
                view.navigateToMainMenu();
                System.out.println("Admin Logged In");
            } else {
                // Clients see plans or terrain list
                view.navigateToMainMenu();
            }
        if (SessionFacade.getInstance().login(username, password)) {
            System.out.println("Login Successful - Redirecting ...");
            view.navigateToMainMenu(); // Navigate on success
        } else {
            view.showMessage("Invalid Credentials", false);
        }
    }
}