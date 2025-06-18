package com.fasttrack.dao;

import com.fasttrack.models.DeliveryPersonnel;
import com.fasttrack.dao.DatabaseConnection;
import com.fasttrack.models.Shipment;
import com.fasttrack.models.DeliveryAssignment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPersonnelDAO {
    private Connection connection;

    public DeliveryPersonnelDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add new personnel
    public boolean addPersonnel(DeliveryPersonnel personnel) {
        String sql = "INSERT INTO delivery_personnel (first_name, last_name, phone, email, " +
                "vehicle_type, license_number, hire_date, schedule, route_area, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPersonnelStatementValues(stmt, personnel);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        personnel.setPersonnelId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update personnel
    public boolean updatePersonnel(DeliveryPersonnel personnel) {
        String sql = "UPDATE delivery_personnel SET first_name=?, last_name=?, phone=?, " +
                "email=?, vehicle_type=?, license_number=?, hire_date=?, schedule=?, " +
                "route_area=?, status=? WHERE personnel_id=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setPersonnelStatementValues(stmt, personnel);
            stmt.setInt(11, personnel.getPersonnelId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete personnel
    public boolean deletePersonnel(int personnelId) {
        String sql = "DELETE FROM delivery_personnel WHERE personnel_id=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, personnelId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all personnel
    public List<DeliveryPersonnel> getAllPersonnel() {
        List<DeliveryPersonnel> personnelList = new ArrayList<>();
        String sql = "SELECT * FROM delivery_personnel ORDER BY last_name, first_name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                personnelList.add(extractPersonnelFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personnelList;
    }

    // Get personnel by ID
    public DeliveryPersonnel getPersonnelById(int personnelId) {
        String sql = "SELECT * FROM delivery_personnel WHERE personnel_id=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, personnelId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractPersonnelFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get delivery history for a personnel
    public List<DeliveryAssignment> getDeliveryHistory(int personnelId) {
        List<DeliveryAssignment> history = new ArrayList<>();
        String sql = "SELECT a.*, s.tracking_number, s.receiver_name, s.receiver_address " +
                "FROM delivery_assignments a " +
                "JOIN shipments s ON a.shipment_id = s.shipment_id " +
                "WHERE a.personnel_id = ? " +
                "ORDER BY a.assignment_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, personnelId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DeliveryAssignment assignment = new DeliveryAssignment();
                    assignment.setAssignmentId(rs.getInt("assignment_id"));
                    assignment.setShipmentId(rs.getInt("shipment_id"));
                    assignment.setAssignmentDate(rs.getTimestamp("assignment_date"));
                    assignment.setCompletionDate(rs.getTimestamp("completion_date"));
                    assignment.setStatus(rs.getString("status"));

                    Shipment shipment = new Shipment();
                    shipment.setTrackingNumber(rs.getString("tracking_number"));
                    shipment.setReceiverName(rs.getString("receiver_name"));
                    shipment.setReceiverAddress(rs.getString("receiver_address"));
                    assignment.setShipment(shipment);

                    history.add(assignment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    // Helper methods
    private DeliveryPersonnel extractPersonnelFromResultSet(ResultSet rs) throws SQLException {
        DeliveryPersonnel personnel = new DeliveryPersonnel();
        personnel.setPersonnelId(rs.getInt("personnel_id"));
        personnel.setFirstName(rs.getString("first_name"));
        personnel.setLastName(rs.getString("last_name"));
        personnel.setPhone(rs.getString("phone"));
        personnel.setEmail(rs.getString("email"));
        personnel.setVehicleType(rs.getString("vehicle_type"));
        personnel.setLicenseNumber(rs.getString("license_number"));
        personnel.setHireDate(rs.getDate("hire_date"));
        personnel.setSchedule(rs.getString("schedule"));
        personnel.setRouteArea(rs.getString("route_area"));
        personnel.setStatus(rs.getString("status"));
        return personnel;
    }

    private void setPersonnelStatementValues(PreparedStatement stmt, DeliveryPersonnel personnel)
            throws SQLException {
        stmt.setString(1, personnel.getFirstName());
        stmt.setString(2, personnel.getLastName());
        stmt.setString(3, personnel.getPhone());
        stmt.setString(4, personnel.getEmail());
        stmt.setString(5, personnel.getVehicleType());
        stmt.setString(6, personnel.getLicenseNumber());
        stmt.setDate(7, new java.sql.Date(personnel.getHireDate().getTime()));
        stmt.setString(8, personnel.getSchedule());
        stmt.setString(9, personnel.getRouteArea());
        stmt.setString(10, personnel.getStatus());
    }

    public boolean updateDriverStatus(int personnelId, String status) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE delivery_personnel SET status = ? WHERE personnel_id = ?")) {

            stmt.setString(1, status);
            stmt.setInt(2, personnelId);

            int updated = stmt.executeUpdate();
            return updated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}