package com.fasttrack.dao;

import com.fasttrack.models.Shipment;
import com.fasttrack.models.ShipmentTracking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShipmentTrackingDAO {
    private Connection connection;

    // Default constructor (kept for backward compatibility)
    public ShipmentTrackingDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ New constructor accepting external connection
    public ShipmentTrackingDAO(Connection connection) {
        this.connection = connection;
    }

    // Add tracking update
    public boolean addTrackingUpdate(ShipmentTracking tracking) throws SQLException {
        String query = "INSERT INTO shipment_tracking (shipment_id, location, status, estimated_delivery) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, tracking.getShipmentId());
            stmt.setString(2, tracking.getLocation());
            stmt.setString(3, tracking.getStatus());
            stmt.setString(4, tracking.getEstimatedDelivery());

            return stmt.executeUpdate() > 0;
        }
    }

    // Get all tracking updates for a shipment
    public List<ShipmentTracking> getTrackingHistory(String trackingNumber) throws SQLException {
        List<ShipmentTracking> trackingHistory = new ArrayList<>();

        String query = "SELECT st.* FROM shipment_tracking st " +
                "JOIN shipments s ON st.shipment_id = s.shipment_id " +
                "WHERE s.tracking_number = ? ORDER BY st.update_time DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trackingNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ShipmentTracking tracking = new ShipmentTracking();
                tracking.setTrackingId(rs.getInt("tracking_id"));
                tracking.setShipmentId(rs.getInt("shipment_id"));
                tracking.setLocation(rs.getString("location"));
                tracking.setUpdateTime(rs.getTimestamp("update_time"));
                tracking.setStatus(rs.getString("status"));
                tracking.setEstimatedDelivery(rs.getString("estimated_delivery"));
                tracking.setTrackingNumber(trackingNumber);

                trackingHistory.add(tracking);
            }
        }
        return trackingHistory;
    }

    // Get shipment details by tracking number
    public Shipment getShipmentByTrackingNumber(String trackingNumber) throws SQLException {
        String query = "SELECT * FROM shipments WHERE tracking_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trackingNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Shipment shipment = new Shipment();
                shipment.setShipmentId(rs.getInt("shipment_id"));
                shipment.setTrackingNumber(rs.getString("tracking_number"));
                shipment.setSenderName(rs.getString("sender_name"));
                shipment.setSenderAddress(rs.getString("sender_address"));
                shipment.setReceiverName(rs.getString("receiver_name"));
                shipment.setReceiverAddress(rs.getString("receiver_address"));
                shipment.setShipmentDate(rs.getTimestamp("shipment_date"));
                shipment.setEstimatedDeliveryDate(rs.getTimestamp("estimated_delivery_date"));
                shipment.setStatus(rs.getString("status"));

                return shipment;
            }
        }
        return null;
    }

    // Update shipment status
    public boolean updateShipmentStatus(int shipmentId, String status) throws SQLException {
        String query = "UPDATE shipments SET status = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE shipment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, shipmentId);

            return stmt.executeUpdate() > 0;
        }
    }
}
