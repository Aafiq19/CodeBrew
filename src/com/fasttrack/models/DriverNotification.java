package com.fasttrack.models;

import java.sql.Timestamp;

public class DriverNotification {
    private int notificationId;
    private int personnelId;
    private String driverName;
    private String notificationType;
    private String message;
    private Timestamp sentTime;
    private boolean isRead;
    private String email;
    private String phone;

    // Constructors
    public DriverNotification() {}

    public DriverNotification(int personnelId, String notificationType, String message) {
        this.personnelId = personnelId;
        this.notificationType = notificationType;
        this.message = message;
    }

    // Getters and Setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(int personnelId) {
        this.personnelId = personnelId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getSentTime() {
        return sentTime;
    }

    public void setSentTime(Timestamp sentTime) {
        this.sentTime = sentTime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "DriverNotification{" +
                "notificationId=" + notificationId +
                ", personnelId=" + personnelId +
                ", driverName='" + driverName + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", message='" + message + '\'' +
                ", sentTime=" + sentTime +
                ", isRead=" + isRead +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}