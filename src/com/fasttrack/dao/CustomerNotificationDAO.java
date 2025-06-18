package com.fasttrack.dao;

import com.fasttrack.models.CustomerNotification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerNotificationDAO {
    private final Connection connection;

    public CustomerNotificationDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean saveNotification(CustomerNotification notification) throws SQLException {
        String sql = "INSERT INTO customer_notifications (tracking_number, customer_email, subject, message) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, notification.getTrackingNumber());
            stmt.setString(2, notification.getCustomerEmail());
            stmt.setString(3, notification.getSubject());
            stmt.setString(4, notification.getMessage());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<CustomerNotification> getRecentNotifications(int limit) throws SQLException {
        String sql = "SELECT * FROM customer_notifications ORDER BY sent_time DESC LIMIT ?";
        List<CustomerNotification> notifications = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CustomerNotification notification = new CustomerNotification();
                notification.setNotificationId(rs.getInt("notification_id"));
                notification.setTrackingNumber(rs.getString("tracking_number"));
                notification.setCustomerEmail(rs.getString("customer_email"));
                notification.setSubject(rs.getString("subject"));
                notification.setMessage(rs.getString("message"));
                notification.setSentTime(rs.getTimestamp("sent_time"));
                notifications.add(notification);
            }
        }

        return notifications;
    }
}
