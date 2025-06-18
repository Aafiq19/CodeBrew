package com.fasttrack.ui;

import com.fasttrack.dao.ShipmentTrackingDAO;
import com.fasttrack.models.Shipment;
import com.fasttrack.models.ShipmentTracking;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShipmentTrackingUI extends JPanel {
    private ShipmentTrackingDAO trackingDAO;
    private JTextField trackingNumberField;
    private JButton trackButton;
    private JButton updateStatusButton;
    private JPanel trackingDetailsPanel;
    private JPanel trackingHistoryPanel;
    private JComboBox<String> statusComboBox;
    private JTextField locationField;
    private JTextField estimatedDeliveryField;
    private JPanel updatePanel;

    public ShipmentTrackingUI(Connection connection) {
        this.trackingDAO = new ShipmentTrackingDAO(connection);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        JLabel titleLabel = new JLabel("TRACK SHIPMENT PROGRESS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Tracking input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBorder(new TitledBorder("Enter Tracking Number"));

        trackingNumberField = new JTextField(25);
        trackingNumberField.setFont(new Font("Arial", Font.PLAIN, 16));

        trackButton = new JButton("Track Shipment");
        styleButton(trackButton);

        inputPanel.add(new JLabel("Tracking Number:"));
        inputPanel.add(trackingNumberField);
        inputPanel.add(trackButton);

        add(inputPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Center panel for details and history
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Tracking details panel
        trackingDetailsPanel = new JPanel(new BorderLayout());
        trackingDetailsPanel.setBorder(new TitledBorder("Shipment Details"));
        trackingDetailsPanel.setPreferredSize(new Dimension(400, 500));

        // Tracking history panel
        trackingHistoryPanel = new JPanel(new BorderLayout());
        trackingHistoryPanel.setBorder(new TitledBorder("Tracking History"));

        centerPanel.add(trackingDetailsPanel);
        centerPanel.add(trackingHistoryPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Update status panel (hidden initially)
        updatePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        updatePanel.setBorder(new TitledBorder("Update Shipment Status"));

        statusComboBox = new JComboBox<>(new String[]{"In Transit", "Delivered", "Returned", "Cancelled"});
        locationField = new JTextField();
        estimatedDeliveryField = new JTextField();

        updatePanel.add(new JLabel("New Status:"));
        updatePanel.add(statusComboBox);
        updatePanel.add(new JLabel("Current Location:"));
        updatePanel.add(locationField);
        updatePanel.add(new JLabel("Estimated Delivery:"));
        updatePanel.add(estimatedDeliveryField);

        updateStatusButton = new JButton("Update Status");
        styleButton(updateStatusButton);
        updatePanel.add(new JLabel());
        updatePanel.add(updateStatusButton);

        updatePanel.setVisible(false);
        add(updatePanel, BorderLayout.SOUTH);

        // Add action listeners
        trackButton.addActionListener(e -> trackShipment());
        updateStatusButton.addActionListener(e -> updateShipmentStatus());

        // Set default estimated delivery format
        estimatedDeliveryField.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.BLACK); // Made text black as per your request
        button.setBackground(new Color(0, 102, 204));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    private void trackShipment() {
        String trackingNumber = trackingNumberField.getText().trim();

        if (trackingNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a tracking number",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Shipment shipment = trackingDAO.getShipmentByTrackingNumber(trackingNumber);

            if (shipment == null) {
                JOptionPane.showMessageDialog(this, "Tracking number not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            displayShipmentDetails(shipment);
            displayTrackingHistory(trackingNumber);
            updatePanel.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayShipmentDetails(Shipment shipment) {
        trackingDetailsPanel.removeAll();

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        addDetailRow(detailsPanel, "Tracking Number:", shipment.getTrackingNumber());
        addDetailRow(detailsPanel, "Sender:", shipment.getSenderName());
        addDetailRow(detailsPanel, "From:", shipment.getSenderAddress());
        addDetailRow(detailsPanel, "Receiver:", shipment.getReceiverName());
        addDetailRow(detailsPanel, "To:", shipment.getReceiverAddress());
        addDetailRow(detailsPanel, "Shipment Date:",
                new SimpleDateFormat("yyyy-MM-dd HH:mm").format(shipment.getShipmentDate()));
        addDetailRow(detailsPanel, "Estimated Delivery:",
                new SimpleDateFormat("yyyy-MM-dd HH:mm").format(shipment.getEstimatedDeliveryDate()));
        addDetailRow(detailsPanel, "Current Status:", shipment.getStatus());

        trackingDetailsPanel.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        trackingDetailsPanel.revalidate();
        trackingDetailsPanel.repaint();
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelField = new JLabel(label);
        labelField.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(labelField);

        JLabel valueField = new JLabel(value != null ? value : "N/A");
        valueField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(valueField);
    }

    private void displayTrackingHistory(String trackingNumber) throws SQLException {
        trackingHistoryPanel.removeAll();

        List<ShipmentTracking> history = trackingDAO.getTrackingHistory(trackingNumber);

        String[] columnNames = {"Date/Time", "Location", "Status", "Est. Delivery"};
        Object[][] data = new Object[history.size()][4];

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (int i = 0; i < history.size(); i++) {
            ShipmentTracking track = history.get(i);
            data[i][0] = dateFormat.format(track.getUpdateTime());
            data[i][1] = track.getLocation();
            data[i][2] = track.getStatus();
            data[i][3] = track.getEstimatedDelivery();
        }

        JTable historyTable = new JTable(data, columnNames);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 14));
        historyTable.setRowHeight(30);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        trackingHistoryPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        trackingHistoryPanel.revalidate();
        trackingHistoryPanel.repaint();
    }

    private void updateShipmentStatus() {
        String trackingNumber = trackingNumberField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();
        String location = locationField.getText().trim();
        String estimatedDelivery = estimatedDeliveryField.getText().trim();

        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter current location",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Shipment shipment = trackingDAO.getShipmentByTrackingNumber(trackingNumber);

            if (shipment == null) {
                JOptionPane.showMessageDialog(this, "Shipment not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ShipmentTracking tracking = new ShipmentTracking();
            tracking.setShipmentId(shipment.getShipmentId());
            tracking.setTrackingNumber(trackingNumber);
            tracking.setLocation(location);
            tracking.setStatus(status);
            tracking.setEstimatedDelivery(estimatedDelivery);

            if (trackingDAO.addTrackingUpdate(tracking)) {
                trackingDAO.updateShipmentStatus(shipment.getShipmentId(), status);

                JOptionPane.showMessageDialog(this, "Status updated successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                trackShipment();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
