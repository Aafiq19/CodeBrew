package com.fasttrack.models;

import java.sql.Timestamp;

public class ShipmentTracking {
    private int trackingId;
    private int shipmentId;
    private String trackingNumber;
    private String location;
    private Timestamp updateTime;
    private String status;
    private String estimatedDelivery;

    // Constructors
    public ShipmentTracking() {}

    public ShipmentTracking(int shipmentId, String trackingNumber, String location,
                            String status, String estimatedDelivery) {
        this.shipmentId = shipmentId;
        this.trackingNumber = trackingNumber;
        this.location = location;
        this.status = status;
        this.estimatedDelivery = estimatedDelivery;
    }

    // Getters and Setters
    public int getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(int trackingId) {
        this.trackingId = trackingId;
    }

    public int getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(int shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    @Override
    public String toString() {
        return "ShipmentTracking [trackingId=" + trackingId + ", shipmentId=" + shipmentId
                + ", trackingNumber=" + trackingNumber + ", location=" + location
                + ", updateTime=" + updateTime + ", status=" + status
                + ", estimatedDelivery=" + estimatedDelivery + "]";
    }
}