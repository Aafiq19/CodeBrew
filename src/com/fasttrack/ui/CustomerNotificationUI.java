package com.fasttrack.ui;

import com.fasttrack.dao.CustomerNotificationDAO;
import com.fasttrack.models.CustomerNotification;
import com.fasttrack.services.EmailService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class CustomerNotificationUI extends JFrame {
    private final CustomerNotificationDAO notificationDAO;
    private final EmailService emailService;
    private final Connection connection;
    private JTable table;
    private JTextField trackingField, emailField, subjectField;
    private JTextArea messageArea;

    public CustomerNotificationUI(Connection connection) {
        this.connection = connection;
        this.notificationDAO = new CustomerNotificationDAO(connection);
        this.emailService = new EmailService(); // Make sure this is already implemented

        initializeUI();
        loadRecentNotifications();
    }

    private void initializeUI() {
        setTitle("Customer Notification System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(240, 245, 250)); // match ReportDashboardFrame

        // Styled Header Panel (same as ReportDashboardFrame)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 50, 80));
        JLabel heading = new JLabel("SEND EMAIL NOTIFICATIONS TO CUSTOMERS");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(Color.WHITE);
        headerPanel.add(heading);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Send Notification"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tracking Number:"), gbc);
        trackingField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(trackingField, gbc);

        // Add Focus Listener to fetch email on focus lost
        trackingField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                fetchAndSetEmail();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Customer Email:"), gbc);
        emailField = new JTextField(20);
        emailField.setEditable(false); // Non-editable
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Subject:"), gbc);
        subjectField = new JTextField(30);
        gbc.gridx = 1;
        formPanel.add(subjectField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Message:"), gbc);
        messageArea = new JTextArea(5, 30);
        messageArea.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(messageArea);
        gbc.gridx = 1;
        formPanel.add(scroll, gbc);

        JButton sendButton = new JButton("Send Email");
        styleButton(sendButton);
        sendButton.addActionListener(e -> sendNotification());

        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(sendButton, gbc);

        panel.add(formPanel, BorderLayout.WEST);

        String[] columnNames = {"ID", "Tracking #", "Email", "Subject", "Sent Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new TitledBorder("Recent Notifications"));
        panel.add(tableScroll, BorderLayout.CENTER);

        add(panel); // Add the entire constructed panel to the JFrame
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

    private void fetchAndSetEmail() {
        String trackingNumber = trackingField.getText().trim();

        if (trackingNumber.isEmpty()) {
            emailField.setText("");
            return;
        }

        String sql = "SELECT customer_email FROM shipments WHERE tracking_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trackingNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                emailField.setText(rs.getString("customer_email"));
            } else {
                emailField.setText("");
                JOptionPane.showMessageDialog(this, "No shipment found for the given tracking number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching email: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendNotification() {
        String trackingNumber = trackingField.getText().trim();
        String email = emailField.getText().trim();
        String subject = subjectField.getText().trim();
        String message = messageArea.getText().trim();

        if (trackingNumber.isEmpty() || email.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            emailService.sendEmail(email, subject, message);

            CustomerNotification notification = new CustomerNotification();
            notification.setTrackingNumber(trackingNumber);
            notification.setCustomerEmail(email);
            notification.setSubject(subject);
            notification.setMessage(message);

            notificationDAO.saveNotification(notification);

            JOptionPane.showMessageDialog(this, "Notification sent successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRecentNotifications();
            trackingField.setText("");
            emailField.setText("");
            subjectField.setText("");
            messageArea.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to send notification: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecentNotifications() {
        try {
            List<CustomerNotification> list = notificationDAO.getRecentNotifications(50);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (CustomerNotification n : list) {
                model.addRow(new Object[]{
                        n.getNotificationId(),
                        n.getTrackingNumber(),
                        n.getCustomerEmail(),
                        n.getSubject(),
                        n.getSentTime()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
