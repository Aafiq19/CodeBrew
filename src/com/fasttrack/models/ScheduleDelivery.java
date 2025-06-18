package com.fasttrack.models;

import java.time.LocalDateTime;

public class ScheduleDelivery {
    private String trackingNumber;
    private LocalDateTime deliverySlotStart;
    private LocalDateTime deliverySlotEnd;
    private String specialInstructions;
    private String customerEmail;
    private String status;

    public ScheduleDelivery() {
    }

    public ScheduleDelivery(String trackingNumber, LocalDateTime deliverySlotStart, LocalDateTime deliverySlotEnd,
                            String specialInstructions, String customerEmail) {
        this.trackingNumber = trackingNumber;
        this.deliverySlotStart = deliverySlotStart;
        this.deliverySlotEnd = deliverySlotEnd;
        this.specialInstructions = specialInstructions;
        this.customerEmail = customerEmail;
        this.status = "Scheduled";
    }

    // Getters and Setters
    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public LocalDateTime getDeliverySlotStart() {
        return deliverySlotStart;
    }

    public void setDeliverySlotStart(LocalDateTime deliverySlotStart) {
        this.deliverySlotStart = deliverySlotStart;
    }

    public LocalDateTime getDeliverySlotEnd() {
        return deliverySlotEnd;
    }

    public void setDeliverySlotEnd(LocalDateTime deliverySlotEnd) {
        this.deliverySlotEnd = deliverySlotEnd;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ScheduleDelivery{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", deliverySlotStart=" + deliverySlotStart +
                ", deliverySlotEnd=" + deliverySlotEnd +
                ", specialInstructions='" + specialInstructions + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}