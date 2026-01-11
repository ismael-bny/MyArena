package com.example.myarena.ui;

import com.example.myarena.facade.SessionFacade;

public class RegisterController {
    private final RegisterFrame view;
    private final SessionFacade sessionFacade;

    public RegisterController(RegisterFrame view) {
        this.view = view;
        this.sessionFacade = SessionFacade.getInstance();
    }

    public void register() {
        String name = view.getName();
        String email = view.getEmail();
        String password = view.getPassword();
        String confirmPassword = view.getConfirmPassword();
        String phone = view.getPhone();

        // Call facade to register
        String errorMessage = sessionFacade.register(name, email, password, confirmPassword, phone);

        if (errorMessage == null) {
            // Success
            view.showMessage("Registration successful! You can now login.", true);

            // Navigate to login after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    view.navigateToLogin();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            // Error
            view.showMessage("❌ " + errorMessage, false);
        }
    }
}