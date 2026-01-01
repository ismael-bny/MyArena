package com.example.myarena.domain;

import java.time.LocalDate;

public class Subscription {

    private Long id;
    private Long userId;              // ID de l'utilisateur qui possède cet abonnement
    private LocalDate startDate;      // Date de début de l'abonnement
    private LocalDate endDate;        // Date de fin de l'abonnement
    private boolean autoRenew;        // true = renouvellement automatique activé
    private SubscriptionStatus status; // ACTIVE, CANCELLED ou EXPIRED
    private SubscriptionPlan plan;    // Le plan d'abonnement associé

    /**
     * Constructeur par défaut (nécessaire pour JDBC et JavaFX)
     */
    public Subscription() {
    }

    /**
     * Constructeur avec tous les paramètres
     */
    public Subscription(Long id, Long userId, LocalDate startDate, LocalDate endDate,
                        boolean autoRenew, SubscriptionStatus status, SubscriptionPlan plan) {
        this.id = id;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.autoRenew = autoRenew;
        this.status = status;
        this.plan = plan;
    }

    // === GETTERS ET SETTERS ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
    }

    @Override
    public String toString() {
        return "Subscription{" + "id=" + id + ", userId=" + userId + ", startDate=" + startDate + ", endDate=" + endDate + ", autoRenew=" + autoRenew + ", status=" + status + ", plan=" + plan + '}';
    }
}
