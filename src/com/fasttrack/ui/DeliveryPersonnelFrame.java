package com.fasttrack.ui;

import com.fasttrack.dao.DeliveryPersonnelDAO;
import com.fasttrack.models.DeliveryPersonnel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class DeliveryPersonnelFrame extends JFrame {
    private DeliveryPersonnelDAO personnelDAO;
    private JTable personnelTable;
    private DefaultTableModel tableModel;

    public DeliveryPersonnelFrame() {
        personnelDAO = new DeliveryPersonnelDAO();

        setTitle("Delivery Personnel Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title panel with styled header (matching ReportDashboardFrame)
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 50, 80));
        JLabel titleLabel = new JLabel("MANAGE DELIVERY PERSONNEL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("Add Personnel");
        JButton editButton = new JButton("Edit Personnel");
        JButton deleteButton = new JButton("Delete Personnel");
        JButton viewHistoryButton = new JButton("View Delivery History");
        JButton refreshButton = new JButton("Refresh");

        // Button styling (same as your ShipmentManagementFrame)
        Color normalColor = new Color(220, 220, 220);      // Light Gray
        Color hoverColor = new Color(192, 192, 192);       // Darker Gray
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        for (JButton button : new JButton[]{addButton, editButton, deleteButton, viewHistoryButton, refreshButton}) {
            button.setBackground(normalColor);
            button.setForeground(Color.BLACK);             // Dark text for visibility
            button.setFont(buttonFont);
            button.setPreferredSize(new Dimension(160, 40));
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

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Personnel List"));

        String[] columnNames = {
                "ID", "Name", "Phone", "Email", "Vehicle",
                "License", "Hire Date", "Schedule", "Route Area", "Status"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        personnelTable = new JTable(tableModel);
        personnelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        personnelTable.getTableHeader().setReorderingAllowed(false);

        // Set preferred column widths (adjust if needed)
        int[] widths = {50, 150, 100, 200, 100, 120, 100, 150, 150, 100};
        for (int i = 0; i < widths.length; i++) {
            personnelTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(personnelTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);

        add(mainPanel);

        refreshPersonnelTable();

        // Button actions
        addButton.addActionListener(e -> showAddEditDialog(null));
        editButton.addActionListener(e -> editSelectedPersonnel());
        deleteButton.addActionListener(e -> deleteSelectedPersonnel());
        viewHistoryButton.addActionListener(e -> viewDeliveryHistory());
        refreshButton.addActionListener(e -> refreshPersonnelTable());
    }

    private void refreshPersonnelTable() {
        tableModel.setRowCount(0);
        List<DeliveryPersonnel> personnelList = personnelDAO.getAllPersonnel();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (DeliveryPersonnel personnel : personnelList) {
            Object[] rowData = {
                    personnel.getPersonnelId(),
                    personnel.getFirstName() + " " + personnel.getLastName(),
                    personnel.getPhone(),
                    personnel.getEmail(),
                    personnel.getVehicleType(),
                    personnel.getLicenseNumber(),
                    dateFormat.format(personnel.getHireDate()),
                    personnel.getSchedule(),
                    personnel.getRouteArea(),
                    personnel.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void showAddEditDialog(DeliveryPersonnel personnel) {
        PersonnelDialog dialog = new PersonnelDialog(this,
                personnel == null ? "Add New Personnel" : "Edit Personnel",
                personnel);
        dialog.setVisible(true);

        if (dialog.isOkPressed()) {
            DeliveryPersonnel updatedPersonnel = dialog.getPersonnel();
            boolean success;

            if (personnel == null) {
                success = personnelDAO.addPersonnel(updatedPersonnel);
            } else {
                success = personnelDAO.updatePersonnel(updatedPersonnel);
            }

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Personnel " + (personnel == null ? "added" : "updated") + " successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshPersonnelTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to " + (personnel == null ? "add" : "update") + " personnel.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedPersonnel() {
        int selectedRow = personnelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a personnel to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int personnelId = (int) tableModel.getValueAt(selectedRow, 0);
        DeliveryPersonnel personnel = personnelDAO.getPersonnelById(personnelId);

        if (personnel != null) {
            showAddEditDialog(personnel);
        }
    }

    private void deleteSelectedPersonnel() {
        int selectedRow = personnelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a personnel to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int personnelId = (int) tableModel.getValueAt(selectedRow, 0);
        String personnelName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + personnelName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (personnelDAO.deletePersonnel(personnelId)) {
                JOptionPane.showMessageDialog(this,
                        "Personnel deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshPersonnelTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete personnel.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewDeliveryHistory() {
        int selectedRow = personnelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a personnel to view history.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int personnelId = (int) tableModel.getValueAt(selectedRow, 0);
        String personnelName = (String) tableModel.getValueAt(selectedRow, 1);

        DeliveryHistoryDialog historyDialog = new DeliveryHistoryDialog(this, personnelId, personnelName);
        historyDialog.setVisible(true);
    }
}
