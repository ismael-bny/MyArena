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

    public void login() {
        String username = view.getUsername();
        String password = view.getPassword();

        // Delegate to Facade
        boolean success = sessionFacade.login(username, password);

        if (success) {
            System.out.println("Login successful for user: " + username);
        } else {
            System.out.println("Login failed.");
        }
    }
}
