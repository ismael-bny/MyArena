package com.example.myarena.facade;

import com.example.myarena.domain.Notification;
import com.example.myarena.domain.NotificationStatus;
import com.example.myarena.domain.User;
import com.example.myarena.services.NotificationManager;
import com.example.myarena.services.UserManager;

import java.util.List;

public class SessionFacade {
    private static SessionFacade instance;
    private final UserManager userManager;
    private final NotificationManager notificationManager;

    private SessionFacade() {
        this.userManager = new UserManager();
        this.notificationManager = new NotificationManager();
    }

    public static SessionFacade getInstance() {
        if (instance == null) {
            instance = new SessionFacade();
        }
        return instance;
    }

    /* ======================
       AUTH / SESSION
       ====================== */

    public boolean login(String email, String pwd) {
        User user = userManager.login(email, pwd);
        if (user != null) {
            UserSession.getInstance().setUser(user);
            return true;
        }
        return false;
    }

    public void logout() {
        UserSession.getInstance().cleanSession();
    }

    public User getCurrentUser() {
        return UserSession.getInstance().getUser();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    /* ======================
       NOTIFICATIONS
       ====================== */

    public List<Notification> getNotifications() {
        Long userId = getCurrentUser().getId();
        return notificationManager.getNotifications(userId);
    }

    public Notification getNotificationDetails(Long notificationId) {
        return notificationManager.getNotificationById(notificationId);
    }

    public boolean updateNotificationStatus(Long notificationId,
                                            NotificationStatus status) {
        notificationManager.updateStatus(notificationId, status);
        return true;
    }
}
