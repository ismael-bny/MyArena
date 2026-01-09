package com.example.myarena.ui;

import com.example.myarena.domain.Notification;
import com.example.myarena.domain.NotificationStatus;
import com.example.myarena.facade.SessionFacade;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationCenterController {

    private final NotificationCenterFrame view;
    private final SessionFacade sessionFacade;

    // Cache local (toujours MUTABLE)
    private List<Notification> cached = new ArrayList<>();

    public NotificationCenterController(NotificationCenterFrame view) {
        this.view = view;
        this.sessionFacade = SessionFacade.getInstance();
    }

    /** Appelé dans initialize() de la Frame. */
    public void loadNotifications() {
        if (!sessionFacade.isUserLoggedIn()) {
            view.showMessage("You must be logged in.", false);
            view.showNotifications(List.of());
            view.updateUnreadCount(0);
            return;
        }

        try {
            cached = new ArrayList<>(sessionFacade.getNotifications());

            cached.sort(Comparator.comparing(
                    Notification::getCreatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ).reversed());

            int unread = countUnread(cached);
            view.updateUnreadCount(unread);

            // par défaut : All
            view.setActiveFilterAll(true);
            view.showNotifications(cached);

            view.showMessage("", true); // clear message
        } catch (Exception e) {
            view.showMessage("Failed to load notifications: " + e.getMessage(), false);
            view.showNotifications(List.of());
            view.updateUnreadCount(0);
        }
    }

    /** Filtre All */
    public void onFilterAll() {
        // cached est mutable => .sort OK
        cached.sort(Comparator.comparing(
                Notification::getCreatedAt,
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed());

        view.setActiveFilterAll(true);
        view.showNotifications(cached);
        view.updateUnreadCount(countUnread(cached));
    }

    /** Filtre Unread */
    public void onFilterUnread() {
        List<Notification> unreadOnly = cached.stream()
                .filter(n -> n.getStatus() == NotificationStatus.PENDING)
                .sorted(Comparator.comparing(
                        Notification::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed())
                .collect(Collectors.toList());

        view.setActiveFilterAll(false);
        view.showNotifications(unreadOnly);
        view.updateUnreadCount(countUnread(cached));
    }

    /**
     * Clic sur "View" d'une notif (dans une card).
     * - marque READ si besoin
     * - ouvre la modale / details via la view
     * - recharge la liste pour mettre à jour badges / compteur
     */
    public void onViewNotification(Notification n) {
        if (n == null || n.getId() == null) {
            view.showMessage("Invalid notification.", false);
            return;
        }

        try {
            // Si unread -> READ
            if (n.getStatus() == NotificationStatus.PENDING) {
                sessionFacade.updateNotificationStatus(n.getId(), NotificationStatus.READ);
            }

            Notification details = sessionFacade.getNotificationDetails(n.getId());
            if (details == null) {
                view.showMessage("Notification not found.", false);
                return;
            }

            // ouvre les détails (Dialog / modal)
            view.openNotificationDetails(details);

            // reload complet pour refléter "READ" + compteur
            loadNotifications();

        } catch (Exception e) {
            view.showMessage("Failed to open notification: " + e.getMessage(), false);
        }
    }

    /** Optionnel : bouton "Mark as handled" dans les détails. */
    public void onMarkAsHandled(Notification n) {
        if (n == null || n.getId() == null) {
            view.showMessage("Invalid notification.", false);
            return;
        }

        try {
            sessionFacade.updateNotificationStatus(n.getId(), NotificationStatus.HANDLED);
            view.showMessage("Notification marked as handled.", true);
            loadNotifications();
        } catch (Exception e) {
            view.showMessage("Failed to update status: " + e.getMessage(), false);
        }
    }

    /** Optionnel : refresh manuel */
    public void onRefresh() {
        loadNotifications();
    }

    private int countUnread(List<Notification> list) {
        int c = 0;
        for (Notification n : list) {
            if (n.getStatus() == NotificationStatus.PENDING) c++;
        }
        return c;
    }
}
