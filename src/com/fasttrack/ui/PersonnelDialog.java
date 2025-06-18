package com.fasttrack.ui;

import com.fasttrack.models.DeliveryPersonnel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class PersonnelDialog extends JDialog {
    private boolean okPressed = false;
    private DeliveryPersonnel personnel;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> vehicleTypeCombo;
    private JTextField licenseField;
    private JTextField hireDateField;
    private JTextField scheduleField;
    private JTextField routeAreaField;
    private JComboBox<String> statusCombo;

    public PersonnelDialog(JFrame parent, String title, DeliveryPersonnel existingPersonnel) {
        super(parent, title, true);
        this.personnel = existingPersonnel != null ? existingPersonnel : new DeliveryPersonnel();

        setSize(600, 500);
        setLocationRelativeTo(parent);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);

        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        if (existingPersonnel != null) firstNameField.setText(existingPersonnel.getFirstName());
        formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Last Name:"), gbc);

        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        if (existingPersonnel != null) lastNameField.setText(existingPersonnel.getLastName());
        formPanel.add(lastNameField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        if (existingPersonnel != null) phoneField.setText(existingPersonnel.getPhone());
        formPanel.add(phoneField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        if (existingPersonnel != null) emailField.setText(existingPersonnel.getEmail());
        formPanel.add(emailField, gbc);

        // Vehicle Type
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Vehicle Type:"), gbc);

        gbc.gridx = 1;
        String[] vehicleTypes = {"Motorcycle", "Car", "Van", "Truck", "Bicycle"};
        vehicleTypeCombo = new JComboBox<>(vehicleTypes);
        if (existingPersonnel != null) vehicleTypeCombo.setSelectedItem(existingPersonnel.getVehicleType());
        formPanel.add(vehicleTypeCombo, gbc);

        // License Number
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("License Number:"), gbc);

        gbc.gridx = 1;
        licenseField = new JTextField(20);
        if (existingPersonnel != null) licenseField.setText(existingPersonnel.getLicenseNumber());
        formPanel.add(licenseField, gbc);

        // Hire Date
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Hire Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        hireDateField = new JTextField(20);
        if (existingPersonnel != null) {
            hireDateField.setText(new java.text.SimpleDateFormat("yyyy-MM-dd")
                    .format(existingPersonnel.getHireDate()));
        } else {
            hireDateField.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
        formPanel.add(hireDateField, gbc);

        // Schedule
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Schedule:"), gbc);

        gbc.gridx = 1;
        scheduleField = new JTextField(20);
        if (existingPersonnel != null) scheduleField.setText(existingPersonnel.getSchedule());
        formPanel.add(scheduleField, gbc);

        // Route Area
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Route Area:"), gbc);

        gbc.gridx = 1;
        routeAreaField = new JTextField(20);
        if (existingPersonnel != null) routeAreaField.setText(existingPersonnel.getRouteArea());
        formPanel.add(routeAreaField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        String[] statusOptions = {"Available", "On Delivery", "On Leave", "Terminated"};
        statusCombo = new JComboBox<>(statusOptions);
        if (existingPersonnel != null) statusCombo.setSelectedItem(existingPersonnel.getStatus());
        else statusCombo.setSelectedItem("Available");
        formPanel.add(statusCombo, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            if (validateInput()) {
                okPressed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private boolean validateInput() {
        // Validate required fields
        if (firstNameField.getText().trim().isEmpty()) {
            showValidationError("First name is required.");
            return false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            showValidationError("Last name is required.");
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showValidationError("Phone number is required.");
            return false;
        }

        if (licenseField.getText().trim().isEmpty()) {
            showValidationError("License number is required.");
            return false;
        }

        // Validate hire date format
        try {
            Date hireDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(hireDateField.getText());
            personnel.setHireDate(hireDate);
        } catch (Exception e) {
            showValidationError("Invalid hire date format. Please use YYYY-MM-DD.");
            return false;
        }

        // Update personnel object
        personnel.setFirstName(firstNameField.getText().trim());
        personnel.setLastName(lastNameField.getText().trim());
        personnel.setPhone(phoneField.getText().trim());
        personnel.setEmail(emailField.getText().trim());
        personnel.setVehicleType((String) vehicleTypeCombo.getSelectedItem());
        personnel.setLicenseNumber(licenseField.getText().trim());
        personnel.setSchedule(scheduleField.getText().trim());
        personnel.setRouteArea(routeAreaField.getText().trim());
        personnel.setStatus((String) statusCombo.getSelectedItem());

        return true;
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    public DeliveryPersonnel getPersonnel() {
        return personnel;
    }
}