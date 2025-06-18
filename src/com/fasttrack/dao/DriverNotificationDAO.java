package com.fasttrack.dao;

import com.fasttrack.models.DriverNotification;
import com.fasttrack.models.DeliveryAssignment;
import com.fasttrack.models.DeliveryPersonnel;
import com.fasttrack.services.EmailService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverNotificationDAO {
    private Connection connection;

    public DriverNotificationDAO(Connection connection) {
        this.connection = connection;
    }

    // Create notification table if not exists
    public void createNotificationTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS driver_notifications (" +
                "notification_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "personnel_id INT NOT NULL, " +
                "notification_type VARCHAR(50) NOT NULL, " +
                "message TEXT NOT NULL, " +
                "sent_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "is_read BOOLEAN DEFAULT FALSE, " +
                "FOREIGN KEY (personnel_id) REFERENCES delivery_personnel(personnel_id)" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
        }
    }

    // Send notification to delivery personnel
    public boolean sendNotification(DriverNotification notification) throws SQLException {
        String query = "INSERT INTO driver_notifications (personnel_id, notification_type, message) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, notification.getPersonnelId());
            stmt.setString(2, notification.getNotificationType());
            stmt.setString(3, notification.getMessage());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                DeliveryPersonnel personnel = getPersonnelDetails(notification.getPersonnelId());
                if (personnel != null) {
                    EmailService.sendEmail(
                            personnel.getEmail(),
                            "New Delivery Notification: " + notification.getNotificationType(),
                            notification.getMessage()
                    );
                    return true;
                }
            }
        }
        return false;
    }

    // Get personnel details
    private DeliveryPersonnel getPersonnelDetails(int personnelId) throws SQLException {
        String query = "SELECT first_name, last_name, email, phone FROM delivery_personnel WHERE personnel_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, personnelId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                DeliveryPersonnel personnel = new DeliveryPersonnel();
                personnel.setFirstName(rs.getString("first_name"));
                personnel.setLastName(rs.getString("last_name"));
                personnel.setEmail(rs.getString("email"));
                personnel.setPhone(rs.getString("phone"));
                return personnel;
            }
        }
        return null;
    }

    // Get notifications for a delivery personnel
    public List<DriverNotification> getDriverNotifications(int personnelId) throws SQLException {
        List<DriverNotification> notifications = new ArrayList<>();

        String query = "SELECT dn.*, dp.first_name, dp.last_name, dp.email, dp.phone " +
                "FROM driver_notifications dn " +
                "JOIN delivery_personnel dp ON dn.personnel_id = dp.personnel_id " +
                "WHERE dn.personnel_id = ? ORDER BY dn.sent_time DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, personnelId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DriverNotification notification = new DriverNotification();
                notification.setNotificationId(rs.getInt("notification_id"));
                notification.setPersonnelId(rs.getInt("personnel_id"));
                notification.setDriverName(rs.getString("first_name") + " " + rs.getString("last_name"));
                notification.setNotificationType(rs.getString("notification_type"));
                notification.setMessage(rs.getString("message"));
                notification.setSentTime(rs.getTimestamp("sent_time"));
                notification.setRead(rs.getBoolean("is_read"));
                notification.setEmail(rs.getString("email"));
                notification.setPhone(rs.getString("phone"));
                notifications.add(notification);
            }
        }
        return notifications;
    }

    // Mark notification as read
    public boolean markAsRead(int notificationId) throws SQLException {
        String query = "UPDATE driver_notifications SET is_read = TRUE WHERE notification_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Get active delivery personnel based on assignments
    public List<DeliveryPersonnel> getActiveDeliveryPersonnel() throws SQLException {
        List<DeliveryPersonnel> personnelList = new ArrayList<>();

        String query = "SELECT DISTINCT dp.personnel_id, dp.first_name, dp.last_name, dp.email, dp.phone " +
                "FROM delivery_assignments da " +
                "JOIN delivery_personnel dp ON da.personnel_id = dp.personnel_id " +
                "WHERE da.status IN ('Assigned', 'In Progress')";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                DeliveryPersonnel personnel = new DeliveryPersonnel();
                personnel.setPersonnelId(rs.getInt("personnel_id"));
                personnel.setFirstName(rs.getString("first_name"));
                personnel.setLastName(rs.getString("last_name"));
                personnel.setEmail(rs.getString("email"));
                personnel.setPhone(rs.getString("phone"));
                personnelList.add(personnel);
            }
        }
        return personnelList;
    }

    // ✅ FIXED METHOD: Get active assignments
    public List<DeliveryAssignment> getActiveAssignments() throws SQLException {
        List<DeliveryAssignment> assignments = new ArrayList<>();
        String query = "SELECT da.assignment_id, da.personnel_id, da.status, " +
                "dp.first_name AS driver_first_name, dp.last_name AS driver_last_name, " +
                "s.tracking_number, s.receiver_name, s.receiver_address " +
                "FROM delivery_assignments da " +
                "JOIN delivery_personnel dp ON da.personnel_id = dp.personnel_id " +
                "JOIN shipments s ON da.shipment_id = s.shipment_id " +
                "WHERE da.status IN ('Assigned', 'In Progress')";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DeliveryAssignment assignment = new DeliveryAssignment();
                assignment.setAssignmentId(rs.getInt("assignment_id"));
                assignment.setPersonnelId(rs.getInt("personnel_id"));
                assignment.setStatus(rs.getString("status"));
                assignment.setTrackingNumber(rs.getString("tracking_number"));
                assignment.setDriverName(rs.getString("driver_first_name") + " " + rs.getString("driver_last_name"));
                assignment.setReceiverName(rs.getString("receiver_name"));
                assignment.setReceiverAddress(rs.getString("receiver_address"));
                assignments.add(assignment);
            }
        }
        return assignments;
    }
}
