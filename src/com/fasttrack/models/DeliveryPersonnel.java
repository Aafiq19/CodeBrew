package com.fasttrack.models;

import java.util.Date;
import java.util.List;

public class DeliveryPersonnel {
    private int personnelId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String vehicleType;
    private String licenseNumber;
    private Date hireDate;
    private String schedule;
    private String routeArea;
    private String status;
    private List<DeliveryAssignment> assignments;

    // Constructors
    public DeliveryPersonnel() {}

    public DeliveryPersonnel(String firstName, String lastName, String phone, String email,
                             String vehicleType, String licenseNumber, Date hireDate,
                             String schedule, String routeArea, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.vehicleType = vehicleType;
        this.licenseNumber = licenseNumber;
        this.hireDate = hireDate;
        this.schedule = schedule;
        this.routeArea = routeArea;
        this.status = status;
    }

    // Getters and setters
    public int getPersonnelId() { return personnelId; }
    public void setPersonnelId(int personnelId) { this.personnelId = personnelId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public String getRouteArea() { return routeArea; }
    public void setRouteArea(String routeArea) { this.routeArea = routeArea; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<DeliveryAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<DeliveryAssignment> assignments) { this.assignments = assignments; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
