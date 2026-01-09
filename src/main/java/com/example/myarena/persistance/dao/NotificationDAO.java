package com.example.myarena.persistance.dao;

import com.example.myarena.domain.Notification;
import com.example.myarena.domain.NotificationStatus;

import java.util.List;

public interface NotificationDAO {

    Notification save(Notification notification);

    List<Notification> findByUserId(Long userId);

    Notification getById(Long notificationId);

    Notification updateStatus(Long notificationId, NotificationStatus status);
}

