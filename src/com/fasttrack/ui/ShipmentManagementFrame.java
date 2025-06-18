package com.fasttrack.ui;

import com.fasttrack.dao.ShipmentDAO;
import com.fasttrack.models.Shipment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShipmentManagementFrame extends JFrame {
    private ShipmentDAO shipmentDAO;
    private JTable shipmentTable;
    private DefaultTableModel tableModel;

    // Updated constructor to receive Connection and initialize shipmentDAO properly
    public ShipmentManagementFrame(Connection conn) {
        shipmentDAO = new ShipmentDAO(conn);  // Pass connection here

        setTitle("Shipment Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Highlighted title panel (updated style)
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 50, 80));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        JLabel titleLabel = new JLabel("MANAGE SHIPMENTS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("Add Shipment");
        JButton editButton = new JButton("Edit Shipment");
        JButton deleteButton = new JButton("Delete Shipment");
        JButton refreshButton = new JButton("Refresh");

        // Button styling
        Color normalColor = new Color(220, 220, 220);      // Light Gray
        Color hoverColor = new Color(192, 192, 192);       // Darker Gray
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        for (JButton button : new JButton[]{addButton, editButton, deleteButton, refreshButton}) {
            button.setBackground(normalColor);
            button.setForeground(Color.BLACK);             // Make text dark
            button.setFont(buttonFont);
            button.setPreferredSize(new Dimension(150, 40));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    button.setBackground(hoverColor);
                }

                public void mouseExited(MouseEvent evt) {
                    button.setBackground(normalColor);
                }
            });

            buttonPanel.add(button);
        }

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Shipment List"));

        String[] columnNames = {
                "Tracking #", "Sender", "Receiver", "Contents", "Status",
                "Shipment Date", "Est. Delivery", "Actual Delivery"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        shipmentTable = new JTable(tableModel);
        shipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shipmentTable.getTableHeader().setReorderingAllowed(false);

        shipmentTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        shipmentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        shipmentTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        shipmentTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        shipmentTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        shipmentTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        shipmentTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        shipmentTable.getColumnModel().getColumn(7).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(shipmentTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);

        add(mainPanel);

        refreshShipmentTable();

        addButton.addActionListener(e -> showAddShipmentDialog());
        editButton.addActionListener(e -> showEditShipmentDialog());
        deleteButton.addActionListener(e -> deleteSelectedShipment());
        refreshButton.addActionListener(e -> refreshShipmentTable());
    }

    private void refreshShipmentTable() {
        tableModel.setRowCount(0);
        List<Shipment> shipments = shipmentDAO.getAllShipments();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Shipment shipment : shipments) {
            Object[] rowData = {
                    shipment.getTrackingNumber(),
                    shipment.getSenderName(),
                    shipment.getReceiverName(),
                    shipment.getPackageContents(),
                    shipment.getStatus(),
                    dateFormat.format(shipment.getShipmentDate()),
                    dateFormat.format(shipment.getEstimatedDeliveryDate()),
                    shipment.getActualDeliveryDate() != null ?
                            dateFormat.format(shipment.getActualDeliveryDate()) : "N/A"
            };
            tableModel.addRow(rowData);
        }
    }

    private void showAddShipmentDialog() {
        ShipmentDialog dialog = new ShipmentDialog(this, "Add New Shipment", null);
        dialog.setVisible(true);

        if (dialog.isOkPressed()) {
            Shipment newShipment = dialog.getShipment();
            if (shipmentDAO.addShipment(newShipment)) {
                JOptionPane.showMessageDialog(this, "Shipment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshShipmentTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add shipment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditShipmentDialog() {
        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a shipment to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String trackingNumber = (String) tableModel.getValueAt(selectedRow, 0);
        Shipment shipment = shipmentDAO.getShipmentByTrackingNumber(trackingNumber);

        if (shipment != null) {
            ShipmentDialog dialog = new ShipmentDialog(this, "Edit Shipment", shipment);
            dialog.setVisible(true);

            if (dialog.isOkPressed()) {
                Shipment updatedShipment = dialog.getShipment();
                if (shipmentDAO.updateShipment(updatedShipment)) {
                    JOptionPane.showMessageDialog(this, "Shipment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshShipmentTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update shipment.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteSelectedShipment() {
        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a shipment to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String trackingNumber = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete shipment " + trackingNumber + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (shipmentDAO.deleteShipment(trackingNumber)) {
                JOptionPane.showMessageDialog(this, "Shipment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshShipmentTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete shipment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
