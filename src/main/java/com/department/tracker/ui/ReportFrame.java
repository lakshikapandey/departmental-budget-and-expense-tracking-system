package com.department.tracker.ui;

import com.department.tracker.dao.BudgetDAO;
import com.department.tracker.dao.ExpenseDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Map;

/**
 * Report & Financial Summary Module.
 * Displays category-wise and monthly spending tables along with a custom
 * Swing Graphics2D Bar Chart visualization without external libraries.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class ReportFrame extends JFrame {

    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    // Summary labels
    private JLabel lblTotalBudget;
    private JLabel lblTotalExpenses;
    private JLabel lblRemainingBudget;
    private JLabel lblUtilization;

    // Tables
    private DefaultTableModel categoryTableModel;
    private DefaultTableModel monthlyTableModel;

    // Custom Chart Panel
    private BarChartPanel chartPanel;

    public ReportFrame() {
        initUI();
        loadReportData();
    }

    private void initUI() {
        setTitle("Financial Reports & Spending Summary");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG_LIGHT);
        root.setBorder(new EmptyBorder(12, 14, 12, 14));

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblTitle = new JLabel("Departmental Financial Reports & Analytics");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Category breakdowns, monthly expenditure trends, and visual budget utilization");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(224, 231, 255));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setOpaque(false);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);
        headerPanel.add(titleBlock, BorderLayout.WEST);

        JButton btnRefresh = UITheme.createSecondaryButton("🔄 Refresh Reports");
        btnRefresh.addActionListener(e -> loadReportData());
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        root.add(headerPanel, BorderLayout.NORTH);

        // 2. Summary KPI Strip
        JPanel summaryStrip = new JPanel(new GridLayout(1, 4, 12, 12));
        summaryStrip.setOpaque(false);

        lblTotalBudget = new JLabel("₹ 0.00");
        lblTotalExpenses = new JLabel("₹ 0.00");
        lblRemainingBudget = new JLabel("₹ 0.00");
        lblUtilization = new JLabel("0.00%");

        summaryStrip.add(createMiniCard("TOTAL BUDGET", lblTotalBudget, UITheme.PRIMARY));
        summaryStrip.add(createMiniCard("TOTAL EXPENSES", lblTotalExpenses, UITheme.ACCENT_DANGER));
        summaryStrip.add(createMiniCard("REMAINING BALANCE", lblRemainingBudget, UITheme.ACCENT_SUCCESS));
        summaryStrip.add(createMiniCard("UTILIZATION %", lblUtilization, UITheme.SECONDARY));

        // 3. Tabbed Pane for Tables & Chart
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);

        // Tab 1: Category Breakdown Table
        JPanel catPanel = new JPanel(new BorderLayout(8, 8));
        catPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        categoryTableModel = new DefaultTableModel(new String[]{"Category", "Total Spent (₹)", "Percentage Share (%)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable catTable = new JTable(categoryTableModel);
        UITheme.styleTable(catTable);
        catPanel.add(new JScrollPane(catTable), BorderLayout.CENTER);
        tabbedPane.addTab("📊 Category Breakdown", catPanel);

        // Tab 2: Monthly Trends Table
        JPanel monthPanel = new JPanel(new BorderLayout(8, 8));
        monthPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        monthlyTableModel = new DefaultTableModel(new String[]{"Month (YYYY-MM)", "Expenditure (₹)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable monthTable = new JTable(monthlyTableModel);
        UITheme.styleTable(monthTable);
        monthPanel.add(new JScrollPane(monthTable), BorderLayout.CENTER);
        tabbedPane.addTab("📅 Monthly Trends", monthPanel);

        // Tab 3: Visual Bar Chart (Pure Java Swing 2D Graphics)
        chartPanel = new BarChartPanel();
        tabbedPane.addTab("📈 Visual Expense Chart", chartPanel);

        // Assemble center
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(summaryStrip, BorderLayout.NORTH);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        root.add(centerPanel, BorderLayout.CENTER);
        add(root);
    }

    private JPanel createMiniCard(String title, JLabel valLabel, Color accent) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(4, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblT = new JLabel(title);
        lblT.setFont(UITheme.FONT_SMALL);
        lblT.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valLabel.setForeground(UITheme.TEXT_DARK);

        card.add(lblT, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    private void loadReportData() {
        try {
            BigDecimal totalBudget = budgetDAO.getTotalBudget();
            BigDecimal totalExpenses = expenseDAO.getTotalExpenses();
            BigDecimal remaining = totalBudget.subtract(totalExpenses);

            BigDecimal utilPercent = BigDecimal.ZERO;
            if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
                utilPercent = totalExpenses.multiply(new BigDecimal(100))
                        .divide(totalBudget, 2, RoundingMode.HALF_UP);
            }

            lblTotalBudget.setText(UITheme.formatCurrency(totalBudget));
            lblTotalExpenses.setText(UITheme.formatCurrency(totalExpenses));
            lblRemainingBudget.setText(UITheme.formatCurrency(remaining));
            lblUtilization.setText(utilPercent + "%");

            if (remaining.compareTo(BigDecimal.ZERO) < 0) {
                lblRemainingBudget.setForeground(UITheme.ACCENT_DANGER);
            } else {
                lblRemainingBudget.setForeground(UITheme.ACCENT_SUCCESS);
            }

            // Category Wise Data
            Map<String, BigDecimal> catMap = expenseDAO.getCategoryWiseExpenses();
            categoryTableModel.setRowCount(0);
            for (Map.Entry<String, BigDecimal> entry : catMap.entrySet()) {
                BigDecimal share = BigDecimal.ZERO;
                if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
                    share = entry.getValue().multiply(new BigDecimal(100))
                            .divide(totalExpenses, 2, RoundingMode.HALF_UP);
                }
                categoryTableModel.addRow(new Object[]{
                        entry.getKey(),
                        UITheme.formatCurrency(entry.getValue()),
                        share + "%"
                });
            }

            // Monthly Wise Data
            Map<String, BigDecimal> monthMap = expenseDAO.getMonthlyExpenses();
            monthlyTableModel.setRowCount(0);
            for (Map.Entry<String, BigDecimal> entry : monthMap.entrySet()) {
                monthlyTableModel.addRow(new Object[]{
                        entry.getKey(),
                        UITheme.formatCurrency(entry.getValue())
                });
            }

            // Update Graphical Chart
            chartPanel.setData(catMap);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating reports: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Custom lightweight Swing 2D graphics bar chart component.
     */
    private static class BarChartPanel extends JPanel {
        private Map<String, BigDecimal> data;
        private static final Color[] BAR_COLORS = {
                new Color(59, 130, 246),
                new Color(16, 185, 129),
                new Color(245, 158, 11),
                new Color(239, 68, 68),
                new Color(139, 92, 246),
                new Color(236, 72, 153),
                new Color(20, 184, 166),
                new Color(14, 165, 233),
                new Color(168, 85, 247)
        };

        public BarChartPanel() {
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(20, 20, 20, 20));
        }

        public void setData(Map<String, BigDecimal> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.setColor(UITheme.TEXT_MUTED);
                g.setFont(UITheme.FONT_BODY);
                g.drawString("No expense data available to render chart.", getWidth() / 2 - 120, getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 60;
            int bottomPadding = 70;

            // Find maximum value
            double maxVal = 0.0;
            for (BigDecimal v : data.values()) {
                if (v.doubleValue() > maxVal) {
                    maxVal = v.doubleValue();
                }
            }
            if (maxVal == 0) maxVal = 1.0;

            // Draw Title
            g2.setColor(UITheme.PRIMARY);
            g2.setFont(UITheme.FONT_HEADER);
            g2.drawString("Category-wise Spending Distribution", padding, 30);

            // Chart Axes
            int chartWidth = width - (padding * 2);
            int chartHeight = height - padding - bottomPadding;

            g2.setColor(UITheme.BORDER_COLOR);
            g2.drawLine(padding, padding + chartHeight, padding + chartWidth, padding + chartHeight); // X axis
            g2.drawLine(padding, padding, padding, padding + chartHeight);                             // Y axis

            int barCount = data.size();
            int totalBarWidth = chartWidth / barCount;
            int barWidth = Math.max(18, totalBarWidth - 25);

            int i = 0;
            for (Map.Entry<String, BigDecimal> entry : data.entrySet()) {
                double val = entry.getValue().doubleValue();
                int barHeight = (int) ((val / maxVal) * (chartHeight - 20));
                int x = padding + (i * totalBarWidth) + 12;
                int y = padding + chartHeight - barHeight;

                Color color = BAR_COLORS[i % BAR_COLORS.length];
                g2.setColor(color);
                g2.fillRoundRect(x, y, barWidth, barHeight, 6, 6);

                // Bar border
                g2.setColor(color.darker());
                g2.drawRoundRect(x, y, barWidth, barHeight, 6, 6);

                // Value on top of bar
                g2.setColor(UITheme.TEXT_DARK);
                g2.setFont(UITheme.FONT_SMALL);
                String valStr = "₹" + (int) val;
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(valStr, x + (barWidth / 2) - (fm.stringWidth(valStr) / 2), y - 6);

                // Category label underneath (rotated or truncated)
                String cat = entry.getKey();
                if (cat.length() > 9) {
                    cat = cat.substring(0, 7) + "..";
                }
                g2.setColor(UITheme.TEXT_MUTED);
                g2.drawString(cat, x + (barWidth / 2) - (fm.stringWidth(cat) / 2), padding + chartHeight + 18);

                i++;
            }
        }
    }
}

