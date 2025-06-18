package com.fasttrack.models;

public class Report {
    private String period;
    private int total;
    private int completed;
    private int onTime;
    private double successRate;

    // Constructor for monthly reports
    public Report(String period, int total, int completed, int onTime) {
        this.period = period;
        this.total = total;
        this.completed = completed;
        this.onTime = onTime;
        this.successRate = (total > 0) ? (100.0 * completed / total) : 0;
    }

    // Getters
    public String getPeriod() {
        return period;
    }

    public int getTotal() {
        return total;
    }

    public int getCompleted() {
        return completed;
    }

    public int getOnTime() {
        return onTime;
    }

    public double getSuccessRate() {
        return successRate;
    }

    // Setters (optional for flexibility)
    public void setPeriod(String period) {
        this.period = period;
    }

    public void setTotal(int total) {
        this.total = total;
        updateSuccessRate();
    }

    public void setCompleted(int completed) {
        this.completed = completed;
        updateSuccessRate();
    }

    public void setOnTime(int onTime) {
        this.onTime = onTime;
    }

    // Private method to update success rate
    private void updateSuccessRate() {
        this.successRate = (total > 0) ? (100.0 * completed / total) : 0;
    }

    // For debugging/logging
    @Override
    public String toString() {
        return "Report{" +
                "period='" + period + '\'' +
                ", total=" + total +
                ", completed=" + completed +
                ", onTime=" + onTime +
                ", successRate=" + successRate +
                '}';
    }
}
