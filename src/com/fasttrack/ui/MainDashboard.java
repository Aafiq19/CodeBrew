package com.fasttrack.ui;

import com.fasttrack.dao.ScheduleDeliveryDAO;
import com.fasttrack.dao.ShipmentDAO;
import com.fasttrack.dao.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class MainDashboard extends JFrame {

    private Connection conn;
    private ScheduleDeliveryDAO scheduleDeliveryDAO;
    private ShipmentDAO shipmentDAO;

    public MainDashboard(Connection conn) {
        this.conn = conn;

        // Initialize DAOs
        this.scheduleDeliveryDAO = new ScheduleDeliveryDAO(conn);
        this.shipmentDAO = new ShipmentDAO(conn);

        setTitle("FastTrack Logistics Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(30, 50, 80);
                Color color2 = new Color(0, 153, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("FASTTRACK LOGISTICS MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        String[] buttonLabels = {
                "1. Manage Shipments", "2. Manage Delivery Personnel",
                "3. Schedule Deliveries", "4. Track Shipment Progress",
                "5. Assign Drivers", "6. Generate Reports",
                "7. Customer Notifications", "8. Driver Notifications"
        };

        for (String label : buttonLabels) {
            JButton button = createMenuButton(label);
            buttonPanel.add(button);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String buttonText = ((JButton) e.getSource()).getText();
                    handleButtonClick(buttonText);
                }
            });
        }

        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 FastTrack Logistics - Group Project");
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(255, 255, 255, 150));
        button.setForeground(new Color(0, 51, 102));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 51, 102)),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        return button;
    }

    private void handleButtonClick(String buttonText) {
        switch (buttonText) {
            case "1. Manage Shipments":
                openShipmentManagement();
                break;
            case "2. Manage Delivery Personnel":
                openDeliveryPersonnelManagement();
                break;
            case "3. Schedule Deliveries":
                openDeliveryScheduling();
                break;
            case "4. Track Shipment Progress":
                openShipmentTracking();
                break;
            case "5. Assign Drivers":
                openDriverAssignment();
                break;
            case "6. Generate Reports":
                openReportDashboard();
                break;
            case "7. Customer Notifications":
                openNotificationSystem();
                break;
            case "8. Driver Notifications":
                openDriverNotificationUI();
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        buttonText + " feature is coming soon!",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openNotificationSystem() {
        SwingUtilities.invokeLater(() -> {
            CustomerNotificationUI frame = new CustomerNotificationUI(conn);
            frame.setVisible(true);
            frame.setLocationRelativeTo(this);
        });
    }

    private void openReportDashboard() {
        SwingUtilities.invokeLater(() -> {
            ReportDashboardFrame reportFrame = new ReportDashboardFrame();
            reportFrame.setVisible(true);
            reportFrame.setLocationRelativeTo(this);
        });
    }

    private void openDriverAssignment() {
        SwingUtilities.invokeLater(() -> {
            DriverAssignmentUI frame = new DriverAssignmentUI(conn);
            frame.setVisible(true);
            frame.setLocationRelativeTo(this);
        });
    }

    private void openDriverNotificationUI() {
        SwingUtilities.invokeLater(() -> {
            DriverNotificationUI frame = new DriverNotificationUI(conn);
            frame.setVisible(true);
            frame.setLocationRelativeTo(this);
        });
    }

    private void openShipmentManagement() {
        ShipmentManagementFrame shipmentFrame = new ShipmentManagementFrame(conn);
        shipmentFrame.setVisible(true);
    }

    private void openDeliveryPersonnelManagement() {
        DeliveryPersonnelFrame personnelFrame = new DeliveryPersonnelFrame();
        personnelFrame.setVisible(true);
    }

    private void openDeliveryScheduling() {
        ScheduleDeliveryUI scheduleFrame = new ScheduleDeliveryUI(scheduleDeliveryDAO, shipmentDAO);
        scheduleFrame.setVisible(true);
    }

    private void openShipmentTracking() {
        SwingUtilities.invokeLater(() -> {
            JFrame trackingFrame = new JFrame("Shipment Tracking");
            trackingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            trackingFrame.setSize(800, 600);
            trackingFrame.setLocationRelativeTo(this);
            trackingFrame.add(new ShipmentTrackingUI(conn));
            trackingFrame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                UIManager.put("Button.background", new Color(0, 102, 204));
                UIManager.put("Button.foreground", Color.WHITE);
                UIManager.put("Button.focus", new Color(0, 153, 255));

                // Get DB connection once
                Connection conn = DatabaseConnection.getConnection();

                MainDashboard dashboard = new MainDashboard(conn);
                dashboard.setVisible(true);

                DatabaseConnection.testConnection();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error initializing application: " + e.getMessage(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
