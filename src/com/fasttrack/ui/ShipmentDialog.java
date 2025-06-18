package com.fasttrack.ui;

import com.fasttrack.models.Shipment;
import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShipmentDialog extends JDialog {
    private boolean okPressed = false;
    private Shipment shipment;

    private JTextField trackingNumberField;
    private JTextField senderNameField;
    private JTextField senderAddressField;
    private JTextField senderPhoneField;
    private JTextField receiverNameField;
    private JTextField receiverAddressField;
    private JTextField receiverPhoneField;
    private JTextField customerEmailField;
    private JTextArea packageContentsArea;
    private JTextField weightField;
    private JTextField dimensionsField;
    private JTextField shipmentDateField;
    private JTextField estimatedDeliveryField;
    private JComboBox<String> statusComboBox;
    private JTextArea notesArea;

    public ShipmentDialog(JFrame parent, String title, Shipment existingShipment) {
        super(parent, title, true);
        this.shipment = existingShipment != null ? existingShipment : new Shipment();

        setSize(800, 600);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Tracking Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tracking Number:"), gbc);

        gbc.gridx = 1;
        trackingNumberField = new JTextField(20);
        if (existingShipment != null) {
            trackingNumberField.setText(existingShipment.getTrackingNumber());
            trackingNumberField.setEditable(false);
        }
        formPanel.add(trackingNumberField, gbc);

        // Sender Name
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Sender Name:"), gbc);

        gbc.gridx = 1;
        senderNameField = new JTextField(20);
        if (existingShipment != null) senderNameField.setText(existingShipment.getSenderName());
        formPanel.add(senderNameField, gbc);

        // Sender Address
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Sender Address:"), gbc);

        gbc.gridx = 1;
        senderAddressField = new JTextField(20);
        if (existingShipment != null) senderAddressField.setText(existingShipment.getSenderAddress());
        formPanel.add(senderAddressField, gbc);

        // Sender Phone
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Sender Phone:"), gbc);

        gbc.gridx = 1;
        senderPhoneField = new JTextField(20);
        if (existingShipment != null) senderPhoneField.setText(existingShipment.getSenderPhone());
        formPanel.add(senderPhoneField, gbc);

        // Receiver Name
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Receiver Name:"), gbc);

        gbc.gridx = 1;
        receiverNameField = new JTextField(20);
        if (existingShipment != null) receiverNameField.setText(existingShipment.getReceiverName());
        formPanel.add(receiverNameField, gbc);

        // Receiver Address
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Receiver Address:"), gbc);

        gbc.gridx = 1;
        receiverAddressField = new JTextField(20);
        if (existingShipment != null) receiverAddressField.setText(existingShipment.getReceiverAddress());
        formPanel.add(receiverAddressField, gbc);

        // Receiver Phone
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Receiver Phone:"), gbc);

        gbc.gridx = 1;
        receiverPhoneField = new JTextField(20);
        if (existingShipment != null) receiverPhoneField.setText(existingShipment.getReceiverPhone());
        formPanel.add(receiverPhoneField, gbc);

        // Customer Email
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Customer Email:"), gbc);

        gbc.gridx = 1;
        customerEmailField = new JTextField(20);
        if (existingShipment != null) customerEmailField.setText(existingShipment.getCustomerEmail());
        formPanel.add(customerEmailField, gbc);

        // Package Contents
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Package Contents:"), gbc);

        gbc.gridx = 1;
        packageContentsArea = new JTextArea(3, 20);
        if (existingShipment != null) packageContentsArea.setText(existingShipment.getPackageContents());
        formPanel.add(new JScrollPane(packageContentsArea), gbc);

        // Weight
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Weight (kg):"), gbc);

        gbc.gridx = 1;
        weightField = new JTextField(20);
        if (existingShipment != null) weightField.setText(String.valueOf(existingShipment.getWeight()));
        formPanel.add(weightField, gbc);

        // Dimensions
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Dimensions (LxWxH):"), gbc);

        gbc.gridx = 1;
        dimensionsField = new JTextField(20);
        if (existingShipment != null) dimensionsField.setText(existingShipment.getDimensions());
        formPanel.add(dimensionsField, gbc);

        // Shipment Date
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Shipment Date:"), gbc);

        gbc.gridx = 1;
        shipmentDateField = new JTextField(20);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (existingShipment != null) shipmentDateField.setText(dateFormat.format(existingShipment.getShipmentDate()));
        else shipmentDateField.setText(dateFormat.format(new Date()));
        formPanel.add(shipmentDateField, gbc);

        // Estimated Delivery
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Estimated Delivery:"), gbc);

        gbc.gridx = 1;
        estimatedDeliveryField = new JTextField(20);
        if (existingShipment != null) estimatedDeliveryField.setText(dateFormat.format(existingShipment.getEstimatedDeliveryDate()));
        else estimatedDeliveryField.setText(dateFormat.format(new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000)));
        formPanel.add(estimatedDeliveryField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        String[] statusOptions = {"Pending", "In Transit", "Delivered", "Returned", "Cancelled"};
        statusComboBox = new JComboBox<>(statusOptions);
        if (existingShipment != null) statusComboBox.setSelectedItem(existingShipment.getStatus());
        formPanel.add(statusComboBox, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Notes:"), gbc);

        gbc.gridx = 1;
        notesArea = new JTextArea(3, 20);
        if (existingShipment != null) notesArea.setText(existingShipment.getNotes());
        formPanel.add(new JScrollPane(notesArea), gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        // ✅ Bold black font for buttons
        Font boldFont = new Font("SansSerif", Font.BOLD, 12);
        okButton.setFont(boldFont);
        okButton.setForeground(Color.BLACK);
        cancelButton.setFont(boldFont);
        cancelButton.setForeground(Color.BLACK);

        okButton.addActionListener(e -> {
            if (validateInput()) {
                okPressed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private boolean validateInput() {
        try {
            if (trackingNumberField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tracking number is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (senderNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sender name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (customerEmailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Customer email is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);

            Date shipmentDate = dateFormat.parse(shipmentDateField.getText());
            Date estimatedDelivery = dateFormat.parse(estimatedDeliveryField.getText());

            if (estimatedDelivery.before(shipmentDate)) {
                JOptionPane.showMessageDialog(this, "Estimated delivery date cannot be before shipment date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            double weight = Double.parseDouble(weightField.getText());
            if (weight <= 0) {
                JOptionPane.showMessageDialog(this, "Weight must be greater than 0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Set all values
            shipment.setTrackingNumber(trackingNumberField.getText().trim());
            shipment.setSenderName(senderNameField.getText().trim());
            shipment.setSenderAddress(senderAddressField.getText().trim());
            shipment.setSenderPhone(senderPhoneField.getText().trim());
            shipment.setReceiverName(receiverNameField.getText().trim());
            shipment.setReceiverAddress(receiverAddressField.getText().trim());
            shipment.setReceiverPhone(receiverPhoneField.getText().trim());
            shipment.setCustomerEmail(customerEmailField.getText().trim());
            shipment.setPackageContents(packageContentsArea.getText().trim());
            shipment.setWeight(weight);
            shipment.setDimensions(dimensionsField.getText().trim());
            shipment.setShipmentDate(shipmentDate);
            shipment.setEstimatedDeliveryDate(estimatedDelivery);
            shipment.setStatus((String) statusComboBox.getSelectedItem());
            shipment.setNotes(notesArea.getText().trim());

            return true;
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Weight must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    public Shipment getShipment() {
        return shipment;
    }
}
