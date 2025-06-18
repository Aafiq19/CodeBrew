package com.fasttrack.ui;

import com.fasttrack.dao.ReportDAO;
import com.fasttrack.models.DriverReport;
import com.fasttrack.models.Report;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportDashboardFrame extends JFrame {
    private ReportDAO reportDAO;
    private JTable dataTable;
    private JComboBox<String> reportTypeCombo;

    public ReportDashboardFrame() {
        reportDAO = new ReportDAO();
        setTitle("Analytics Dashboard");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 245, 250));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 50, 80));
        JLabel title = new JLabel("FASTTRACK PERFORMANCE ANALYTICS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        headerPanel.add(title);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(new Color(240, 245, 250));

        reportTypeCombo = new JComboBox<>(new String[]{"Monthly Performance", "Driver Efficiency"});
        reportTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton generateBtn = createStyledButton("Generate", new Color(76, 175, 80), Color.BLACK);

        generateBtn.addActionListener(e -> generateReport());

        controlPanel.add(new JLabel("Report Type:"));
        controlPanel.add(reportTypeCombo);
        controlPanel.add(generateBtn);

        dataTable = new JTable();
        dataTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dataTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
        generateReport();
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return btn;
    }

    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        DefaultTableModel model = new DefaultTableModel();

        if ("Monthly Performance".equals(reportType)) {
            model.setColumnIdentifiers(new String[]{
                    "Month", "Total Shipments", "Delivered", "On Time", "Success Rate"
            });

            for (Report report : reportDAO.getMonthlyReports()) {
                model.addRow(new Object[]{
                        report.getPeriod(),
                        report.getTotal(),
                        report.getCompleted(),
                        report.getOnTime(),
                        String.format("%.1f%%", report.getSuccessRate())
                });
            }
        } else {
            model.setColumnIdentifiers(new String[]{
                    "Driver", "Assignments", "Successful", "Avg Hours", "Success Rate"
            });

            for (DriverReport report : reportDAO.getDriverReports()) {
                model.addRow(new Object[]{
                        report.getDriverName(),
                        report.getAssignments(),
                        report.getSuccesses(),
                        String.format("%.1f", report.getAvgDeliveryTime()),
                        String.format("%.1f%%", report.getSuccessRate())
                });
            }
        }

        dataTable.setModel(model);
        styleTableColumns();
    }

    private void styleTableColumns() {
        dataTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (int i = 1; i < dataTable.getColumnCount(); i++) {
            dataTable.getColumnModel().getColumn(i).setCellRenderer(new CenterRenderer());
        }
    }

    private static class CenterRenderer extends DefaultTableCellRenderer {
        public CenterRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new ReportDashboardFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
