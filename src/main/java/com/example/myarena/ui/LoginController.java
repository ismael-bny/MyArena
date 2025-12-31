package com.example.myarena.ui;

import com.example.myarena.facade.SessionFacade;

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

        if (sessionFacade.login(username, password)) {
            System.out.println("Login Successful");
            view.navigateToMainMenu(); // Navigate on success
        } else {
            System.out.println("Login Failed");
            view.showMessage("Invalid Credentials", false); // Show error to user
        }
    }
}