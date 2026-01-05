package com.example.myarena.ui;

import com.example.myarena.facade.SessionFacade;
import com.example.myarena.domain.User;

public class ProfileController {
    private ProfileFrame view;
    private SessionFacade sessionFacade;

    public ProfileController(ProfileFrame view) {
        this.view = view;
        this.sessionFacade = SessionFacade.getInstance();
    }

    public void loadCurrentUser() {
        User currentUser = sessionFacade.getCurrentUser();
        if (currentUser != null) {
            view.displayUserInfo(currentUser);
        } else {
            view.showMessage("No user logged in", false);
        }
    }

    public void updateProfile() {
        String name = view.getName();
        String email = view.getEmail();
        String phone = view.getPhone();

        String error = sessionFacade.updateProfile(name, email, phone);

        if (error == null) {
            view.showMessage("Profile updated successfully!", true);
            loadCurrentUser(); // Refresh display
        } else {
            view.showMessage(error, false);
        }
    }

    public void changePassword() {
        String currentPassword = view.getCurrentPassword();
        String newPassword = view.getNewPassword();
        String confirmPassword = view.getConfirmPassword();

        String error = sessionFacade.changePassword(currentPassword, newPassword, confirmPassword);

        if (error == null) {
            view.showMessage("Password changed successfully!", true);
            view.clearPasswordFields();
        } else {
            view.showMessage(error, false);
        }
    }
}