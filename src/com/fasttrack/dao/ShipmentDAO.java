package com.fasttrack.dao;

import com.fasttrack.models.Shipment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShipmentDAO {

    private Connection conn;

    // Constructor with Connection
    public ShipmentDAO(Connection conn) {
        this.conn = conn;
    }

    // ✅ Fixed: Default constructor now initializes the connection
    public ShipmentDAO() {
        try {
            this.conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Shipment> getAllShipments() {
        List<Shipment> shipments = new ArrayList<>();
        String sql = "SELECT * FROM shipments";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                shipments.add(mapResultSetToShipment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shipments;
    }

    public Shipment getShipmentByTrackingNumber(String trackingNumber) {
        String sql = "SELECT * FROM shipments WHERE tracking_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trackingNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToShipment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Shipment getShipmentById(int shipmentId) {
        String sql = "SELECT * FROM shipments WHERE shipment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToShipment(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addShipment(Shipment shipment) {
        String sql = "INSERT INTO shipments (tracking_number, sender_name, sender_address, sender_phone, receiver_name, receiver_address, receiver_phone, customer_email, package_contents, weight, dimensions, shipment_date, estimated_delivery_date, actual_delivery_date, status, notes, delivery_slot_start, delivery_slot_end, special_instructions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shipment.getTrackingNumber());
            stmt.setString(2, shipment.getSenderName());
            stmt.setString(3, shipment.getSenderAddress());
            stmt.setString(4, shipment.getSenderPhone());
            stmt.setString(5, shipment.getReceiverName());
            stmt.setString(6, shipment.getReceiverAddress());
            stmt.setString(7, shipment.getReceiverPhone());
            stmt.setString(8, shipment.getCustomerEmail());
            stmt.setString(9, shipment.getPackageContents());
            stmt.setDouble(10, shipment.getWeight());
            stmt.setString(11, shipment.getDimensions());
            stmt.setTimestamp(12, shipment.getShipmentDate() != null ? new Timestamp(shipment.getShipmentDate().getTime()) : null);
            stmt.setTimestamp(13, shipment.getEstimatedDeliveryDate() != null ? new Timestamp(shipment.getEstimatedDeliveryDate().getTime()) : null);
            stmt.setTimestamp(14, shipment.getActualDeliveryDate() != null ? new Timestamp(shipment.getActualDeliveryDate().getTime()) : null);
            stmt.setString(15, shipment.getStatus());
            stmt.setString(16, shipment.getNotes());
            stmt.setTime(17, shipment.getDeliverySlotStart());
            stmt.setTime(18, shipment.getDeliverySlotEnd());
            stmt.setString(19, shipment.getSpecialInstructions());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateShipment(Shipment shipment) {
        String sql = "UPDATE shipments SET sender_name = ?, sender_address = ?, sender_phone = ?, receiver_name = ?, receiver_address = ?, receiver_phone = ?, customer_email = ?, package_contents = ?, weight = ?, dimensions = ?, shipment_date = ?, estimated_delivery_date = ?, actual_delivery_date = ?, status = ?, notes = ?, delivery_slot_start = ?, delivery_slot_end = ?, special_instructions = ? WHERE shipment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shipment.getSenderName());
            stmt.setString(2, shipment.getSenderAddress());
            stmt.setString(3, shipment.getSenderPhone());
            stmt.setString(4, shipment.getReceiverName());
            stmt.setString(5, shipment.getReceiverAddress());
            stmt.setString(6, shipment.getReceiverPhone());
            stmt.setString(7, shipment.getCustomerEmail());
            stmt.setString(8, shipment.getPackageContents());
            stmt.setDouble(9, shipment.getWeight());
            stmt.setString(10, shipment.getDimensions());
            stmt.setTimestamp(11, shipment.getShipmentDate() != null ? new Timestamp(shipment.getShipmentDate().getTime()) : null);
            stmt.setTimestamp(12, shipment.getEstimatedDeliveryDate() != null ? new Timestamp(shipment.getEstimatedDeliveryDate().getTime()) : null);
            stmt.setTimestamp(13, shipment.getActualDeliveryDate() != null ? new Timestamp(shipment.getActualDeliveryDate().getTime()) : null);
            stmt.setString(14, shipment.getStatus());
            stmt.setString(15, shipment.getNotes());
            stmt.setTime(16, shipment.getDeliverySlotStart());
            stmt.setTime(17, shipment.getDeliverySlotEnd());
            stmt.setString(18, shipment.getSpecialInstructions());
            stmt.setInt(19, shipment.getShipmentId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteShipment(String trackingNumber) {
        String sql = "DELETE FROM shipments WHERE tracking_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trackingNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Shipment> getShipmentsWithoutDrivers() {
        List<Shipment> shipments = new ArrayList<>();
        String sql = "SELECT s.* FROM shipments s " +
                "LEFT JOIN delivery_assignments da ON s.shipment_id = da.shipment_id " +
                "WHERE da.personnel_id IS NULL AND s.status != 'Cancelled'";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                shipments.add(mapResultSetToShipment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shipments;
    }

    public String getDriverName(int shipmentId) {
        String sql = "SELECT p.name FROM personnel p " +
                "JOIN delivery_assignments da ON p.personnel_id = da.personnel_id " +
                "WHERE da.shipment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shipmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Shipment> getShipmentsWithoutSlots() {
        List<Shipment> shipments = new ArrayList<>();
        String sql = "SELECT * FROM shipments WHERE delivery_slot_start IS NULL OR delivery_slot_end IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                shipments.add(mapResultSetToShipment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shipments;
    }

    public boolean updateShipmentDeliverySlots(String trackingNumber, Time deliverySlotStart, Time deliverySlotEnd) {
        String sql = "UPDATE shipments SET delivery_slot_start = ?, delivery_slot_end = ? WHERE tracking_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTime(1, deliverySlotStart);
            stmt.setTime(2, deliverySlotEnd);
            stmt.setString(3, trackingNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Shipment mapResultSetToShipment(ResultSet rs) throws SQLException {
        Shipment shipment = new Shipment();
        shipment.setShipmentId(rs.getInt("shipment_id"));
        shipment.setTrackingNumber(rs.getString("tracking_number"));
        shipment.setSenderName(rs.getString("sender_name"));
        shipment.setSenderAddress(rs.getString("sender_address"));
        shipment.setSenderPhone(rs.getString("sender_phone"));
        shipment.setReceiverName(rs.getString("receiver_name"));
        shipment.setReceiverAddress(rs.getString("receiver_address"));
        shipment.setReceiverPhone(rs.getString("receiver_phone"));
        shipment.setCustomerEmail(rs.getString("customer_email"));
        shipment.setPackageContents(rs.getString("package_contents"));
        shipment.setWeight(rs.getDouble("weight"));
        shipment.setDimensions(rs.getString("dimensions"));
        shipment.setShipmentDate(rs.getTimestamp("shipment_date"));
        shipment.setEstimatedDeliveryDate(rs.getTimestamp("estimated_delivery_date"));
        shipment.setActualDeliveryDate(rs.getTimestamp("actual_delivery_date"));
        shipment.setStatus(rs.getString("status"));
        shipment.setNotes(rs.getString("notes"));
        shipment.setDeliverySlotStart(rs.getTime("delivery_slot_start"));
        shipment.setDeliverySlotEnd(rs.getTime("delivery_slot_end"));
        shipment.setSpecialInstructions(rs.getString("special_instructions"));
        return shipment;
    }
}
