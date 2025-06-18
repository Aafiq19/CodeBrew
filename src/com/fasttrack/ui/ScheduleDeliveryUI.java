package com.fasttrack.ui;

import com.fasttrack.dao.ScheduleDeliveryDAO;
import com.fasttrack.dao.ShipmentDAO;
import com.fasttrack.models.ScheduleDelivery;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ScheduleDeliveryUI extends JFrame {
    private ScheduleDeliveryDAO scheduleDeliveryDAO;
    private ShipmentDAO shipmentDAO;
    private JTable deliveryTable;
    private DefaultTableModel tableModel;

    public ScheduleDeliveryUI(ScheduleDeliveryDAO scheduleDeliveryDAO, ShipmentDAO shipmentDAO) {
        this.scheduleDeliveryDAO = scheduleDeliveryDAO;
        this.shipmentDAO = shipmentDAO;

        applyDialogStyle();
        initializeUI();
        loadScheduledDeliveries();
    }

    private void applyDialogStyle() {
        Font dialogFont = new Font("Arial", Font.BOLD, 12);
        UIManager.put("OptionPane.messageFont", dialogFont);
        UIManager.put("OptionPane.buttonFont", dialogFont);
        UIManager.put("Button.foreground", Color.BLACK);
    }

    private void initializeUI() {
        setTitle("Schedule Delivery Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        // Styled main heading
        JLabel titleLabel = new JLabel("SCHEDULE DELIVERY", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(45, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Schedule New Delivery"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tracking Number:"), gbc);
        JTextField trackingNumberField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(trackingNumberField, gbc);

        JButton verifyButton = new JButton("Verify Tracking Number");
        styleButton(verifyButton);
        gbc.gridx = 2;
        formPanel.add(verifyButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Customer Email:"), gbc);
        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Delivery Date:"), gbc);
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        gbc.gridx = 1;
        formPanel.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Time Slot:"), gbc);
        String[] timeSlots = {
                "09:00 AM - 11:00 AM",
                "11:00 AM - 01:00 PM",
                "01:00 PM - 03:00 PM",
                "03:00 PM - 05:00 PM",
                "05:00 PM - 07:00 PM"
        };
        JComboBox<String> timeSlotCombo = new JComboBox<>(timeSlots);
        gbc.gridx = 1;
        formPanel.add(timeSlotCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Special Instructions:"), gbc);
        JTextArea instructionsArea = new JTextArea(3, 20);
        instructionsArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(instructionsArea);
        gbc.gridx = 1;
        formPanel.add(scrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton scheduleButton = new JButton("Schedule Delivery");
        styleButton(scheduleButton);
        buttonPanel.add(scheduleButton);

        JButton clearButton = new JButton("Clear Form");
        styleButton(clearButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Scheduled Deliveries"));

        String[] columnNames = {"Tracking Number", "Customer Email", "Delivery Date", "Time Slot", "Special Instructions", "Status", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        deliveryTable = new JTable(tableModel);
        deliveryTable.setRowHeight(30);
        deliveryTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        deliveryTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane tableScrollPane = new JScrollPane(deliveryTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        verifyButton.addActionListener(e -> {
            String trackingNumber = trackingNumberField.getText().trim();
            if (trackingNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a tracking number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                if (scheduleDeliveryDAO.trackingNumberExists(trackingNumber)) {
                    ScheduleDelivery shipment = scheduleDeliveryDAO.getShipmentDetails(trackingNumber);
                    if (shipment != null) {
                        emailField.setText(shipment.getCustomerEmail());
                        JOptionPane.showMessageDialog(this, "Tracking number verified", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Tracking number not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        scheduleButton.addActionListener(e -> {
            String trackingNumber = trackingNumberField.getText().trim();
            String customerEmail = emailField.getText().trim();
            LocalDate deliveryDate = ((java.util.Date) dateSpinner.getValue()).toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            String timeSlot = (String) timeSlotCombo.getSelectedItem();
            String specialInstructions = instructionsArea.getText().trim();

            if (trackingNumber.isEmpty() || customerEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tracking number and customer email are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] times = timeSlot.split(" - ");
            LocalTime startTime = LocalTime.parse(times[0].replace(" AM", "").replace(" PM", ""));
            LocalTime endTime = LocalTime.parse(times[1].replace(" AM", "").replace(" PM", ""));

            if (timeSlot.contains("PM") && !times[0].contains("12")) startTime = startTime.plusHours(12);
            if (timeSlot.contains("PM") && !times[1].contains("12")) endTime = endTime.plusHours(12);

            LocalDateTime slotStart = LocalDateTime.of(deliveryDate, startTime);
            LocalDateTime slotEnd = LocalDateTime.of(deliveryDate, endTime);

            ScheduleDelivery delivery = new ScheduleDelivery(trackingNumber, slotStart, slotEnd, specialInstructions, customerEmail);

            try {
                if (scheduleDeliveryDAO.scheduleDelivery(delivery)) {
                    JOptionPane.showMessageDialog(this, "Delivery scheduled successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadScheduledDeliveries();
                    clearForm(trackingNumberField, emailField, dateSpinner, timeSlotCombo, instructionsArea);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to schedule delivery", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearForm(trackingNumberField, emailField, dateSpinner, timeSlotCombo, instructionsArea));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(220, 220, 220));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    private void clearForm(JTextField trackingField, JTextField emailField, JSpinner dateSpinner,
                           JComboBox<String> timeSlotCombo, JTextArea instructionsArea) {
        trackingField.setText("");
        emailField.setText("");
        dateSpinner.setValue(new java.util.Date());
        timeSlotCombo.setSelectedIndex(0);
        instructionsArea.setText("");
    }

    private void loadScheduledDeliveries() {
        try {
            List<ScheduleDelivery> deliveries = scheduleDeliveryDAO.getAllScheduledDeliveries();
            tableModel.setRowCount(0);
            for (ScheduleDelivery delivery : deliveries) {
                String date = delivery.getDeliverySlotStart().toLocalDate().toString();
                String timeSlot = formatTimeSlot(delivery.getDeliverySlotStart(), delivery.getDeliverySlotEnd());

                Object[] rowData = {
                        delivery.getTrackingNumber(),
                        delivery.getCustomerEmail(),
                        date,
                        timeSlot,
                        delivery.getSpecialInstructions(),
                        delivery.getStatus(),
                        "Delete"
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading scheduled deliveries: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatTimeSlot(LocalDateTime start, LocalDateTime end) {
        return start.toLocalTime().toString() + " - " + end.toLocalTime().toString();
    }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Arial", Font.BOLD, 12));
            setForeground(Color.BLACK);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setForeground(Color.BLACK);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            this.row = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int response = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete this scheduled delivery?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    String trackingNumber = (String) tableModel.getValueAt(row, 0);
                    try {
                        scheduleDeliveryDAO.deleteScheduledDelivery(trackingNumber);
                        loadScheduledDeliveries();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error deleting delivery: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }
}
