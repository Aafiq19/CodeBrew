package com.fasttrack.dao;

import com.fasttrack.models.DriverReport;
import com.fasttrack.models.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    private Connection connection;

    public ReportDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Report> getMonthlyReports() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM monthly_performance ORDER BY month DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reports.add(new Report(
                        rs.getString("month"),
                        rs.getInt("total_shipments"),
                        rs.getInt("delivered"),
                        rs.getInt("on_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    public List<DriverReport> getDriverReports() {
        List<DriverReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM driver_performance ORDER BY successful_deliveries DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reports.add(new DriverReport(
                        rs.getString("driver_name"),
                        rs.getInt("total_assignments"),
                        rs.getInt("successful_deliveries"),
                        rs.getDouble("avg_hours")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }
}
