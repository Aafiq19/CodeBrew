package com.fasttrack.models;

import java.sql.Timestamp;

public class DeliveryAssignment {
    private int assignmentId;
    private int personnelId;
    private int shipmentId;
    private String trackingNumber;
    private String driverName;
    private String vehicleType;
    private Timestamp assignmentDate;
    private Timestamp completionDate;
    private String status;
    private String notes;

    // Shipment object
    private Shipment shipment;

    // ✅ Added fields for UI
    private String receiverName;
    private String receiverAddress;

    // Constructors
    public DeliveryAssignment() {}

    public DeliveryAssignment(int personnelId, int shipmentId, String status, String notes) {
        this.personnelId = personnelId;
        this.shipmentId = shipmentId;
        this.status = status;
        this.notes = notes;
    }

    // Getters and setters
    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(int personnelId) {
        this.personnelId = personnelId;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Timestamp getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(Timestamp assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Timestamp getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Timestamp completionDate) {
        this.completionDate = completionDate;
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

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
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

    @Override
    public String toString() {
        return "DeliveryAssignment [assignmentId=" + assignmentId + ", personnelId=" + personnelId
                + ", shipmentId=" + shipmentId + ", trackingNumber=" + trackingNumber
                + ", driverName=" + driverName + ", vehicleType=" + vehicleType
                + ", assignmentDate=" + assignmentDate + ", completionDate=" + completionDate
                + ", status=" + status + ", notes=" + notes
                + ", receiverName=" + receiverName + ", receiverAddress=" + receiverAddress
                + ", shipment=" + shipment + "]";
    }
}
