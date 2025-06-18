package com.fasttrack.dao;

import com.fasttrack.models.DeliveryAssignment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.fasttrack.models.Shipment;
import com.fasttrack.models.DeliveryPersonnel;

public class DeliveryAssignmentDAO {
    private Connection connection;

    public DeliveryAssignmentDAO(Connection connection) {
        this.connection = connection;
    }

    // Assign driver to shipment
    public boolean assignDriver(DeliveryAssignment assignment) throws SQLException {
        String query = "INSERT INTO delivery_assignments (personnel_id, shipment_id, assignment_date, status, notes) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, assignment.getPersonnelId());
            stmt.setInt(2, assignment.getShipmentId());
            stmt.setString(3, assignment.getStatus());
            stmt.setString(4, assignment.getNotes());

            return stmt.executeUpdate() > 0;
        }
    }

    // Get all assignments
    public List<DeliveryAssignment> getAllAssignments() throws SQLException {
        List<DeliveryAssignment> assignments = new ArrayList<>();

        String query = "SELECT da.*, dp.first_name, dp.last_name, dp.vehicle_type, s.tracking_number " +
                "FROM delivery_assignments da " +
                "JOIN delivery_personnel dp ON da.personnel_id = dp.personnel_id " +
                "JOIN shipments s ON da.shipment_id = s.shipment_id " +
                "ORDER BY da.assignment_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                DeliveryAssignment assignment = new DeliveryAssignment();
                assignment.setAssignmentId(rs.getInt("assignment_id"));
                assignment.setPersonnelId(rs.getInt("personnel_id"));
                assignment.setShipmentId(rs.getInt("shipment_id"));
                assignment.setTrackingNumber(rs.getString("tracking_number"));
                assignment.setDriverName(rs.getString("first_name") + " " + rs.getString("last_name"));
                assignment.setVehicleType(rs.getString("vehicle_type"));
                assignment.setAssignmentDate(rs.getTimestamp("assignment_date"));
                assignment.setCompletionDate(rs.getTimestamp("completion_date"));
                assignment.setStatus(rs.getString("status"));
                assignment.setNotes(rs.getString("notes"));

                assignments.add(assignment);
            }
        }
        return assignments;
    }

    // Get available drivers
    public List<DeliveryPersonnel> getAvailableDrivers() throws SQLException {
        List<DeliveryPersonnel> personnelList  = new ArrayList<>();

        String query = "SELECT * FROM delivery_personnel WHERE status = 'Available'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                DeliveryPersonnel personnel = new DeliveryPersonnel();
                personnel.setPersonnelId(rs.getInt("personnel_id"));
                personnel.setFirstName(rs.getString("first_name"));
                personnel.setLastName(rs.getString("last_name"));
                personnel.setPhone(rs.getString("phone"));
                personnel.setEmail(rs.getString("email"));
                personnel.setVehicleType(rs.getString("vehicle_type"));
                personnel.setLicenseNumber(rs.getString("license_number"));
                personnel.setStatus(rs.getString("status"));

                personnelList .add(personnel);
            }
        }
        return personnelList ;
    }

    // ✅ FIXED: Get unassigned shipments
    // Get unassigned shipments
    public List<Shipment> getUnassignedShipments() throws SQLException {
        List<Shipment> shipments = new ArrayList<>();

        String query = "SELECT * FROM shipments s " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 FROM delivery_assignments da " +
                "    WHERE da.shipment_id = s.shipment_id " +
                ") AND LOWER(s.status) = 'pending'"; // Case-insensitive check

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
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

                shipments.add(shipment);
            }
        }
        return shipments;
    }


    // Update assignment status
    public boolean updateAssignmentStatus(int assignmentId, String status) throws SQLException {
        String query = "UPDATE delivery_assignments SET status = ?, " +
                "completion_date = CASE WHEN ? = 'Completed' THEN CURRENT_TIMESTAMP ELSE NULL END " +
                "WHERE assignment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, status);
            stmt.setInt(3, assignmentId);

            return stmt.executeUpdate() > 0;
        }
    }

    // Update driver status
    public boolean updateDriverStatus(int personnelId, String status) throws SQLException {
        String query = "UPDATE delivery_personnel SET status = ? WHERE personnel_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, personnelId);

            return stmt.executeUpdate() > 0;
        }
    }
}
