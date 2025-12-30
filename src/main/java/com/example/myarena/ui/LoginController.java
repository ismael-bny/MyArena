package com.example.myarena.ui;

import com.example.myarena.facade.SessionFacade;

import javax.net.ssl.SSLSession;

public class LoginController {

    private LoginFrame view;
    private SessionFacade sessionFacade;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.sessionFacade = SessionFacade.getInstance();
    }

//    public void login() {
//        String username = view.getUsername();
//        String password = view.getPassword();
//
//        // Delegate to Facade
//        boolean success = sessionFacade.login(username, password);
//
//        if (success) {
//            view.showMessage("Login successful for user: " + username, true);
//            System.out.println("Login successful for user: " + username);
//        } else {
//            view.showMessage("Login failed. Check your credentials.", false);
//            System.out.println("Login failed.");
//        }
//    }

    public void login() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (sessionFacade.login(username, password)) {
            System.out.println("Login Successful");
            // TRIGGER NAVIGATION
            view.navigateToMainMenu();
        } else {
            System.out.println("Login Failed");
            // Optional: view.showError("Invalid Credentials");
        }
    }
}
