package com.fasttrack.ui;

import com.fasttrack.dao.DriverNotificationDAO;
import com.fasttrack.models.DriverNotification;
import com.fasttrack.models.DeliveryAssignment;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class DriverNotificationUI extends JFrame {
    private DriverNotificationDAO notificationDAO;
    private JTable notificationsTable;
    private JComboBox<DeliveryAssignment> assignmentCombo;
    private JComboBox<String> notificationTypeCombo;
    private JTextArea messageArea;
    private JButton sendButton;
    private JButton refreshButton;

    public DriverNotificationUI(Connection connection) {
        this.notificationDAO = new DriverNotificationDAO(connection);
        initializeUI();
        loadActiveAssignments();
    }

    private void initializeUI() {
        setTitle("Driver Notification System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        JLabel titleLabel = new JLabel("DRIVER NOTIFICATION CENTER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Send New Notification"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Assignment:"), gbc);
        assignmentCombo = new JComboBox<>();
        assignmentCombo.setRenderer(new AssignmentListRenderer());
        gbc.gridx = 1;
        formPanel.add(assignmentCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Notification Type:"), gbc);
        notificationTypeCombo = new JComboBox<>(new String[]{
                "New Assignment",
                "Route Change",
                "Urgent Delivery",
                "Schedule Update",
                "General Announcement"
        });
        gbc.gridx = 1;
        formPanel.add(notificationTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Message:"), gbc);
        messageArea = new JTextArea(5, 40);
        messageArea.setLineWrap(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        gbc.gridx = 1;
        formPanel.add(messageScroll, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        sendButton = new JButton("Send Notification");
        styleButton(sendButton);
        buttonPanel.add(sendButton);

        refreshButton = new JButton("Refresh");
        styleButton(refreshButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new TitledBorder("Recent Notifications"));

        String[] columnNames = {"ID", "Driver", "Type", "Message", "Sent Time", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        notificationsTable = new JTable(tableModel);
        notificationsTable.setRowHeight(30);

        JScrollPane tableScroll = new JScrollPane(notificationsTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        sendButton.addActionListener(e -> sendNotification());
        refreshButton.addActionListener(e -> loadActiveAssignments());
        notificationTypeCombo.addActionListener(e -> updateMessageTemplate());
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(220, 220, 220));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    private void loadActiveAssignments() {
        try {
            DeliveryAssignment current = (DeliveryAssignment) assignmentCombo.getSelectedItem();
            int currentId = (current != null) ? current.getAssignmentId() : -1;

            List<DeliveryAssignment> assignments = notificationDAO.getActiveAssignments();
            DefaultComboBoxModel<DeliveryAssignment> model = new DefaultComboBoxModel<>();
            DeliveryAssignment toSelect = null;

            for (DeliveryAssignment assignment : assignments) {
                model.addElement(assignment);
                if (assignment.getAssignmentId() == currentId) {
                    toSelect = assignment;
                }
            }

            assignmentCombo.setModel(model);

            if (toSelect != null) {
                assignmentCombo.setSelectedItem(toSelect);
                loadDriverNotifications(toSelect.getPersonnelId());
            } else if (!assignments.isEmpty()) {
                assignmentCombo.setSelectedIndex(0);
                loadDriverNotifications(assignments.get(0).getPersonnelId());
            } else {
                ((DefaultTableModel) notificationsTable.getModel()).setRowCount(0);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading assignments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDriverNotifications(int personnelId) {
        try {
            List<DriverNotification> notifications = notificationDAO.getDriverNotifications(personnelId);
            DefaultTableModel model = (DefaultTableModel) notificationsTable.getModel();
            model.setRowCount(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (DriverNotification notification : notifications) {
                Object[] row = {
                        notification.getNotificationId(),
                        notification.getDriverName(),
                        notification.getNotificationType(),
                        notification.getMessage().length() > 30 ?
                                notification.getMessage().substring(0, 30) + "..." : notification.getMessage(),
                        dateFormat.format(notification.getSentTime()),
                        notification.isRead() ? "Read" : "Unread"
                };
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendNotification() {
        DeliveryAssignment selectedAssignment = (DeliveryAssignment) assignmentCombo.getSelectedItem();
        String notificationType = (String) notificationTypeCombo.getSelectedItem();
        String message = messageArea.getText().trim();

        if (selectedAssignment == null || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an assignment and enter a message", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DriverNotification notification = new DriverNotification();
            notification.setPersonnelId(selectedAssignment.getPersonnelId());
            notification.setNotificationType(notificationType);
            notification.setMessage(message);

            if (notificationDAO.sendNotification(notification)) {
                JOptionPane.showMessageDialog(this, "Notification sent successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDriverNotifications(selectedAssignment.getPersonnelId());
                messageArea.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error sending notification: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMessageTemplate() {
        String type = (String) notificationTypeCombo.getSelectedItem();
        DeliveryAssignment assignment = (DeliveryAssignment) assignmentCombo.getSelectedItem();

        if (assignment == null) return;

        switch (type) {
            case "New Assignment":
                messageArea.setText("Dear " + assignment.getDriverName() + ",\n\n" +
                        "You have been assigned a new delivery:\n" +
                        "Tracking #: " + assignment.getTrackingNumber() + "\n" +
                        "Delivery to: " + assignment.getReceiverName() + "\n" +
                        "Address: " + assignment.getReceiverAddress() + "\n\n" +
                        "Please confirm receipt of this assignment.");
                break;

            case "Route Change":
                messageArea.setText("Dear " + assignment.getDriverName() + ",\n\n" +
                        "There has been a change to your assigned route for delivery " +
                        assignment.getTrackingNumber() + ".\n\n" +
                        "New route details will be provided separately.\n\n" +
                        "Please acknowledge this notification.");
                break;

            case "Urgent Delivery":
                messageArea.setText("URGENT: " + assignment.getDriverName() + "\n\n" +
                        "Delivery " + assignment.getTrackingNumber() + " has been marked as HIGH PRIORITY.\n\n" +
                        "Please prioritize this delivery above all others.\n\n" +
                        "Confirm immediate action.");
                break;

            default:
                messageArea.setText("");
        }
    }

    class AssignmentListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof DeliveryAssignment) {
                DeliveryAssignment assignment = (DeliveryAssignment) value;
                setText(assignment.getDriverName() + " - " + assignment.getTrackingNumber() +
                        " (" + assignment.getStatus() + ")");
            }
            return this;
        }
    }
}
