package com.example.myarena.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Représente un tournoi créé par un organisateur
 */
public class Tournament {
    private Long id;
    private Long organiserId;
    private String name;
    private String sport;                // Catégorie sportive (football, basketball, etc.)
    private String description;
    private String rules;
    private Date startDate;
    private Date endDate;
    private String location;
    private Integer maxParticipants;
    private BigDecimal registrationFee;
    private String prize;                // Prix/récompense
    private Long terrainId;              // Terrain associé (optionnel)
    private TournamentStatus status;
    private Date createdAt;
    private Integer currentParticipants; // Nombre actuel d'inscrits

    // Constructeur par défaut
    public Tournament() {
        this.status = TournamentStatus.AwaitingApproval;
        this.createdAt = new Date();
        this.currentParticipants = 0;
        this.registrationFee = BigDecimal.ZERO;
    }

    // Constructeur avec paramètres essentiels
    public Tournament(Long organiserId, String name, String sport, String description,
                      String rules, Date startDate, Date endDate, String location,
                      Integer maxParticipants, BigDecimal registrationFee, String prize) {
        this.organiserId = organiserId;
        this.name = name;
        this.sport = sport;
        this.description = description;
        this.rules = rules;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.registrationFee = registrationFee != null ? registrationFee : BigDecimal.ZERO;
        this.prize = prize;
        this.status = TournamentStatus.AwaitingApproval;
        this.createdAt = new Date();
        this.currentParticipants = 0;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganiserId() {
        return organiserId;
    }

    public void setOrganiserId(Long organiserId) {
        this.organiserId = organiserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public BigDecimal getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(BigDecimal registrationFee) {
        this.registrationFee = registrationFee;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public Long getTerrainId() {
        return terrainId;
    }

    public void setTerrainId(Long terrainId) {
        this.terrainId = terrainId;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    // Méthodes utilitaires
    public boolean isFull() {
        return currentParticipants != null && maxParticipants != null
                && currentParticipants >= maxParticipants;
    }

    public boolean isRegistrationOpen() {
        return status == TournamentStatus.Open && !isFull();
    }

    public Integer getRemainingSlots() {
        if (maxParticipants == null || currentParticipants == null) {
            return 0;
        }
        return Math.max(0, maxParticipants - currentParticipants);
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sport='" + sport + '\'' +
                ", location='" + location + '\'' +
                ", startDate=" + startDate +
                ", status=" + status +
                ", currentParticipants=" + currentParticipants +
                "/" + maxParticipants +
                '}';
    }
}