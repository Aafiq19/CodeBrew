package com.fasttrack.ui;

import com.fasttrack.dao.DeliveryPersonnelDAO;
import com.fasttrack.models.DeliveryAssignment;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class DeliveryHistoryDialog extends JDialog {
    public DeliveryHistoryDialog(JFrame parent, int personnelId, String personnelName) {
        super(parent, "Delivery History: " + personnelName, true);

        setSize(800, 600);
        setLocationRelativeTo(parent);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Get delivery history
        DeliveryPersonnelDAO dao = new DeliveryPersonnelDAO();
        List<DeliveryAssignment> history = dao.getDeliveryHistory(personnelId);

        // Table model
        String[] columns = {"Assignment Date", "Completion Date", "Status",
                "Tracking #", "Receiver", "Address"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Populate table
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (DeliveryAssignment assignment : history) {
            Object[] row = {
                    dateFormat.format(assignment.getAssignmentDate()),
                    assignment.getCompletionDate() != null ?
                            dateFormat.format(assignment.getCompletionDate()) : "N/A",
                    assignment.getStatus(),
                    assignment.getShipment().getTrackingNumber(),
                    assignment.getShipment().getReceiverName(),
                    assignment.getShipment().getReceiverAddress()
            };
            model.addRow(row);
        }

        // Create table
        JTable historyTable = new JTable(model);
        historyTable.setRowHeight(25);
        historyTable.setAutoCreateRowSorter(true);

        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(historyTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}