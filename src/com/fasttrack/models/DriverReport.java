package com.fasttrack.models;

public class DriverReport {
    private String driverName;
    private int assignments;
    private int successes;
    private double avgDeliveryTime;

    public DriverReport(String driverName, int assignments, int successes, double avgHours) {
        this.driverName = driverName;
        this.assignments = assignments;
        this.successes = successes;
        this.avgDeliveryTime = avgHours;
    }

    // Getters
    public String getDriverName() { return driverName; }
    public int getAssignments() { return assignments; }
    public int getSuccesses() { return successes; }
    public double getAvgDeliveryTime() { return avgDeliveryTime; }
    public double getSuccessRate() {
        return (assignments > 0) ? (100.0 * successes / assignments) : 0;
    }
}