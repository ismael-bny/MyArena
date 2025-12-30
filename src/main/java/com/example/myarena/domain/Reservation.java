package com.example.myarena.domain;

import java.math.BigDecimal;
import java.util.Date;

public class Reservation {
    private Long id;
    private Long userId;
    private Long terrainId;
    private Date startDate;
    private Date endDate;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private Date createdAt;

    // New fields
//    private int participants;
//    private String purpose;

    public Reservation() {}

    public Reservation(Long userId, Long terrainId, Date startDate, Date endDate, BigDecimal totalPrice) {
        this.userId = userId;
        this.terrainId = terrainId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
//        this.participants = participants;
//        this.purpose = purpose;
        this.status = ReservationStatus.Pending;
        this.createdAt = new Date();
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTerrainId() { return terrainId; }
    public void setTerrainId(Long terrainId) { this.terrainId = terrainId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

//    public int getParticipants() { return participants; }
//    public void setParticipants(int participants) { this.participants = participants; }
//
//    public String getPurpose() { return purpose; }
//    public void setPurpose(String purpose) { this.purpose = purpose; }
}