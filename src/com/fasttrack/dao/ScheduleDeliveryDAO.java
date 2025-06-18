package com.fasttrack.dao;

import com.fasttrack.models.ScheduleDelivery;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDeliveryDAO {
    private Connection connection;

    public ScheduleDeliveryDAO(Connection connection) {
        this.connection = connection;
    }

    // Check if tracking number exists in shipments table
    public boolean trackingNumberExists(String trackingNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM shipments WHERE tracking_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trackingNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Get shipment details by tracking number
    public ScheduleDelivery getShipmentDetails(String trackingNumber) throws SQLException {
        String query = "SELECT tracking_number, receiver_name, receiver_phone, customer_email " +
                "FROM shipments WHERE tracking_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trackingNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ScheduleDelivery delivery = new ScheduleDelivery();
                delivery.setTrackingNumber(rs.getString("tracking_number"));
                delivery.setCustomerEmail(rs.getString("customer_email"));
                return delivery;
            }
        }
        return null;
    }

    // Schedule a delivery
    public boolean scheduleDelivery(ScheduleDelivery delivery) throws SQLException {
        String query = "UPDATE shipments SET delivery_slot_start = ?, delivery_slot_end = ?, " +
                "special_instructions = ?, status = 'Scheduled', updated_at = CURRENT_TIMESTAMP " +
                "WHERE tracking_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(delivery.getDeliverySlotStart()));
            stmt.setTimestamp(2, Timestamp.valueOf(delivery.getDeliverySlotEnd()));
            stmt.setString(3, delivery.getSpecialInstructions());
            stmt.setString(4, delivery.getTrackingNumber());

            return stmt.executeUpdate() > 0;
        }
    }

    // Get all scheduled deliveries
    public List<ScheduleDelivery> getAllScheduledDeliveries() throws SQLException {
        List<ScheduleDelivery> deliveries = new ArrayList<>();
        String query = "SELECT tracking_number, delivery_slot_start, delivery_slot_end, " +
                "special_instructions, customer_email, status FROM shipments " +
                "WHERE status = 'Scheduled' ORDER BY delivery_slot_start";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ScheduleDelivery delivery = new ScheduleDelivery();
                delivery.setTrackingNumber(rs.getString("tracking_number"));
                delivery.setDeliverySlotStart(rs.getTimestamp("delivery_slot_start").toLocalDateTime());
                delivery.setDeliverySlotEnd(rs.getTimestamp("delivery_slot_end").toLocalDateTime());
                delivery.setSpecialInstructions(rs.getString("special_instructions"));
                delivery.setCustomerEmail(rs.getString("customer_email"));
                delivery.setStatus(rs.getString("status"));
                deliveries.add(delivery);
            }
        }
        return deliveries;
    }

    // Delete a scheduled delivery
    public boolean deleteScheduledDelivery(String trackingNumber) throws SQLException {
        String query = "UPDATE shipments SET delivery_slot_start = NULL, delivery_slot_end = NULL, " +
                "special_instructions = NULL, status = 'Pending', updated_at = CURRENT_TIMESTAMP " +
                "WHERE tracking_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trackingNumber);
            return stmt.executeUpdate() > 0;
        }
    }
}