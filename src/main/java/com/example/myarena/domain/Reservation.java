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

    public Reservation() {}

    public Reservation(Long userId, Long terrainId, Date startDate, Date endDate, BigDecimal totalPrice) {
        this.id = id;
        this.userId = userId;
        this.terrainId = terrainId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

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

    public Long getTerrainId() {
        return terrainId;
    }

    public void setTerrainId(Long terrainId) {
        this.terrainId = terrainId;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Reservation Summary: " +
                "\nID: " + (id != null ? id : "NEW") +
                "\nTerrain ID: " + terrainId +
                "\nStart: " + startDate +
                "\nEnd: " + endDate +
                "\nStatus: " + status;
    }
}

