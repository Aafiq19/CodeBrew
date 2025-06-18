package com.fasttrack.models;

import java.util.Date;
import java.sql.Time;

public class Shipment {
    private int shipmentId;
    private String trackingNumber;
    private String senderName;
    private String senderAddress;
    private String senderPhone;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private String customerEmail;
    private String packageContents;
    private double weight;
    private String dimensions;
    private Date shipmentDate;
    private Date estimatedDeliveryDate;
    private Date actualDeliveryDate;
    private String status;
    private String notes;

    // Updated to use java.sql.Time
    private Time deliverySlotStart;
    private Time deliverySlotEnd;

    private String specialInstructions;

    // New field for driver name
    private String driverName;

    // Getters and setters

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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getPackageContents() {
        return packageContents;
    }

    public void setPackageContents(String packageContents) {
        this.packageContents = packageContents;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public Date getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Time getDeliverySlotStart() {
        return deliverySlotStart;
    }

    public void setDeliverySlotStart(Time deliverySlotStart) {
        this.deliverySlotStart = deliverySlotStart;
    }

    public Time getDeliverySlotEnd() {
        return deliverySlotEnd;
    }

    public void setDeliverySlotEnd(Time deliverySlotEnd) {
        this.deliverySlotEnd = deliverySlotEnd;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
