package com.example.myarena.services;

import com.example.myarena.domain.Notification;
import com.example.myarena.domain.NotificationStatus;
import com.example.myarena.domain.NotificationType;
import com.example.myarena.persistance.dao.NotificationDAO;
import com.example.myarena.persistance.factory.AbstractFactory;
import com.example.myarena.persistance.factory.PostgresFactory;

import java.util.List;

public class NotificationManager {

    private final NotificationDAO notificationDAO;

    public NotificationManager() {
        PostgresFactory factory = new PostgresFactory();
        this.notificationDAO = factory.createNotificationDAO();
    }

    // Création (appelé par ReservationManager, SubscriptionManager, etc.)
    public Notification createNotification(Long userId, NotificationType type, String title, String message) {
        Notification n = new Notification(userId, title, message, type);
        return notificationDAO.save(n);
    }

    // Consultation
    public List<Notification> getNotifications(Long userId) {
        return notificationDAO.findByUserId(userId);
    }

    public Notification getNotificationById(Long id) {
        return notificationDAO.getById(id);
    }

    public Notification updateStatus(Long id, NotificationStatus status) {
        return notificationDAO.updateStatus(id, status);
    }
}

