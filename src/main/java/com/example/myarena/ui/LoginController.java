package com.example.myarena.ui;

import com.example.myarena.domain.UserRole;
import com.example.myarena.facade.SessionFacade;
import com.example.myarena.ui.subscription.PlanManagementFrame;
import com.example.myarena.ui.subscription.SubscriptionPlansFrame;

public class LoginController {

    private LoginFrame view;
    private SessionFacade sessionFacade;

    // This constructor IS REQUIRED for 'new LoginController(this)' to work
    public LoginController(LoginFrame view) {
        this.view = view;
        this.sessionFacade = SessionFacade.getInstance();
    }

    public void login() {
        String username = view.getUsername();
        String password = view.getPassword();

        // Delegate to Facade
        boolean success = sessionFacade.login(username, password);

        if (success) {
            view.showMessage("Login successful for user: " + username, true);
            System.out.println("Login successful for user: " + username);

            view.closeWindow();

            UserRole role = sessionFacade.getCurrentUser().getRole();
            if (role == UserRole.ADMIN || role == UserRole.OWNER) {
                PlanManagementFrame.show();
            } else {
                SubscriptionPlansFrame.show();
            }
        if (SessionFacade.getInstance().login(username, password)) {
            System.out.println("Login Successful - Redirecting ...");
            view.navigateToMainMenu(); // Navigate on success
        } else {
            System.out.println("Login Failed");
            view.showMessage("Invalid Credentials", false); // Show error to user
        }
    }
}