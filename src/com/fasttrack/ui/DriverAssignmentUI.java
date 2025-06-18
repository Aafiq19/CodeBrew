package com.fasttrack.ui;

import com.fasttrack.dao.DeliveryAssignmentDAO;
import com.fasttrack.models.DeliveryAssignment;
import com.fasttrack.models.DeliveryPersonnel;
import com.fasttrack.models.Shipment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class DriverAssignmentUI extends JFrame {
    private DeliveryAssignmentDAO assignmentDAO;
    private JTable assignmentsTable;
    private JComboBox<DeliveryPersonnel> driverComboBox;
    private JComboBox<Shipment> shipmentComboBox;
    private JTextArea notesArea;
    private JButton assignButton;
    private JButton updateStatusButton;
    private Connection connection;

    public DriverAssignmentUI(Connection connection) {
        this.connection = connection;
        if (connection == null) {
            JOptionPane.showMessageDialog(null, "Database connection is not established.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        this.assignmentDAO = new DeliveryAssignmentDAO(connection);
        initializeUI();
        loadAssignments();
        loadAvailableDrivers();
        loadUnassignedShipments();
    }

    private void initializeUI() {
        setTitle("Driver Assignment System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        // Updated header panel with new style
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 50, 80));
        JLabel titleLabel = new JLabel("DRIVER ASSIGNMENT MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Assign Driver to Shipment"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Driver ComboBox
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Driver:"), gbc);
        driverComboBox = new JComboBox<>(new DefaultComboBoxModel<>());
        driverComboBox.setRenderer(new DriverListRenderer());
        gbc.gridx = 1;
        formPanel.add(driverComboBox, gbc);

        // Shipment ComboBox
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Select Shipment:"), gbc);
        shipmentComboBox = new JComboBox<>(new DefaultComboBoxModel<>());
        shipmentComboBox.setRenderer(new ShipmentListRenderer());
        gbc.gridx = 1;
        formPanel.add(shipmentComboBox, gbc);

        // Notes TextArea
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Assignment Notes:"), gbc);
        notesArea = new JTextArea(3, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        gbc.gridx = 1;
        formPanel.add(notesScroll, gbc);

        // Assign Button
        assignButton = new JButton("Assign Driver");
        styleButton(assignButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(assignButton, gbc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Assignments Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new TitledBorder("Current Assignments"));

        String[] columnNames = {"Assignment ID", "Driver", "Vehicle", "Tracking #", "Assigned On", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only 'Action' button column editable
            }
        };
        assignmentsTable = new JTable(model);
        assignmentsTable.setRowHeight(30);
        assignmentsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        assignmentsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane tableScroll = new JScrollPane(assignmentsTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        updateStatusButton = new JButton("Update Driver Status");
        styleButton(updateStatusButton);
        statusPanel.add(updateStatusButton);
        tablePanel.add(statusPanel, BorderLayout.SOUTH);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Event handlers
        assignButton.addActionListener(e -> assignDriver());
        updateStatusButton.addActionListener(e -> updateDriverStatus());
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.BLACK); // Changed to black text
        button.setBackground(new Color(0, 102, 204));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    private void loadAssignments() {
        try {
            List<DeliveryAssignment> assignments = assignmentDAO.getAllAssignments();
            DefaultTableModel model = (DefaultTableModel) assignmentsTable.getModel();
            model.setRowCount(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (DeliveryAssignment assignment : assignments) {
                Object[] row = {
                        assignment.getAssignmentId(),
                        assignment.getDriverName(),
                        assignment.getVehicleType(),
                        assignment.getTrackingNumber(),
                        assignment.getAssignmentDate() != null ? dateFormat.format(assignment.getAssignmentDate()) : "",
                        assignment.getStatus(),
                        "Update Status"
                };
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading assignments: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAvailableDrivers() {
        try {
            List<DeliveryPersonnel> drivers = assignmentDAO.getAvailableDrivers();
            DefaultComboBoxModel<DeliveryPersonnel> model = (DefaultComboBoxModel<DeliveryPersonnel>) driverComboBox.getModel();
            model.removeAllElements();

            for (DeliveryPersonnel driver : drivers) {
                model.addElement(driver);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading drivers: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUnassignedShipments() {
        try {
            List<Shipment> shipments = assignmentDAO.getUnassignedShipments();
            DefaultComboBoxModel<Shipment> model = (DefaultComboBoxModel<Shipment>) shipmentComboBox.getModel();
            model.removeAllElements();

            for (Shipment shipment : shipments) {
                model.addElement(shipment);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading shipments: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignDriver() {
        DeliveryPersonnel selectedDriver = (DeliveryPersonnel) driverComboBox.getSelectedItem();
        Shipment selectedShipment = (Shipment) shipmentComboBox.getSelectedItem();
        String notes = notesArea.getText().trim();

        if (selectedDriver == null || selectedShipment == null) {
            JOptionPane.showMessageDialog(this, "Please select both a driver and a shipment",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DeliveryAssignment assignment = new DeliveryAssignment();
            assignment.setPersonnelId(selectedDriver.getPersonnelId());
            assignment.setShipmentId(selectedShipment.getShipmentId());
            assignment.setStatus("Assigned");
            assignment.setNotes(notes);

            if (assignmentDAO.assignDriver(assignment)) {
                assignmentDAO.updateDriverStatus(selectedDriver.getPersonnelId(), "On Delivery");
                JOptionPane.showMessageDialog(this, "Driver assigned successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAssignments();
                loadAvailableDrivers();
                loadUnassignedShipments();
                notesArea.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error assigning driver: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDriverStatus() {
        int selectedRow = assignmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an assignment to update",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int assignmentId = (int) assignmentsTable.getValueAt(selectedRow, 0);
        String currentStatus = (String) assignmentsTable.getValueAt(selectedRow, 5);

        String[] options;
        if ("Assigned".equals(currentStatus)) {
            options = new String[]{"In Progress", "Completed", "Cancelled"};
        } else if ("In Progress".equals(currentStatus)) {
            options = new String[]{"Completed", "Cancelled"};
        } else {
            JOptionPane.showMessageDialog(this, "This assignment cannot be updated further",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Select new status:", "Update Assignment Status",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (newStatus != null) {
            try {
                if (assignmentDAO.updateAssignmentStatus(assignmentId, newStatus)) {
                    if ("Completed".equals(newStatus) || "Cancelled".equals(newStatus)) {
                        int personnelId = getPersonnelIdFromAssignment(assignmentId);
                        if (personnelId != -1) {
                            assignmentDAO.updateDriverStatus(personnelId, "Available");
                        }
                    }

                    JOptionPane.showMessageDialog(this, "Status updated successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAssignments();
                    loadAvailableDrivers();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getPersonnelIdFromAssignment(int assignmentId) throws SQLException {
        String query = "SELECT personnel_id FROM delivery_assignments WHERE assignment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, assignmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("personnel_id");
            }
        }
        return -1;
    }

    // Renderer for Driver ComboBox
    class DriverListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof DeliveryPersonnel) {
                DeliveryPersonnel driver = (DeliveryPersonnel) value;
                setText(driver.getFirstName() + " " + driver.getLastName() + " (" + driver.getVehicleType() + ")");
            }
            return this;
        }
    }

    // Renderer for Shipment ComboBox
    class ShipmentListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Shipment) {
                Shipment shipment = (Shipment) value;
                setText(shipment.getTrackingNumber() + " - " + shipment.getReceiverName());
            }
            return this;
        }
    }

    // Button Renderer for JTable action column
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Arial", Font.BOLD, 12));
            setForeground(Color.BLACK); // Changed to black text
            setBackground(new Color(0, 102, 204));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Button Editor for JTable action column
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
            button.setForeground(Color.BLACK); // Changed to black text
            button.setBackground(new Color(0, 102, 204));
            button.addActionListener((ActionEvent e) -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Select the row first, then call updateDriverStatus()
                assignmentsTable.setRowSelectionInterval(row, row);
                updateDriverStatus();
            }
            isPushed = false;
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Connection conn = null; // TODO: replace with actual database connection
                if (conn == null) {
                    JOptionPane.showMessageDialog(null, "Database connection not established.", "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
                DriverAssignmentUI frame = new DriverAssignmentUI(conn);
                frame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error launching UI: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}