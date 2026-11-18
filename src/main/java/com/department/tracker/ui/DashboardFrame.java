package com.department.tracker.ui;

import com.department.tracker.dao.BudgetDAO;
import com.department.tracker.dao.ExpenseDAO;
import com.department.tracker.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

/**
 * Main Dashboard Screen.
 * Displays live KPI summaries calculated directly from MySQL.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class DashboardFrame extends JFrame {

    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    // Value Labels for KPI Cards
    private JLabel lblTotalBudgetVal;
    private JLabel lblTotalExpensesVal;
    private JLabel lblRemainingBudgetVal;
    private JLabel lblUtilizationVal;
    private JLabel lblExpenseCountVal;

    // Alert / Status banner
    private JLabel lblAlertBanner;
    private JPanel bannerPanel;

    // User info label
    private JLabel lblUserInfo;

    public DashboardFrame() {
        initUI();
        loadDashboardMetrics();
    }

    private void initUI() {
        setTitle("Dashboard - Departmental Budget & Expense Tracking System");
        setSize(980, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Root container
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // 1. Top Header & User Info Bar
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(UITheme.PRIMARY);
        topHeader.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBlock.setOpaque(false);
        JLabel appTitle = new JLabel("Departmental Budget & Expense Tracking System");
        appTitle.setFont(UITheme.FONT_TITLE);
        appTitle.setForeground(Color.WHITE);

        JLabel appSub = new JLabel("Academic Minor Project | Lakshika Pandey (PIET25CR029)");
        appSub.setFont(UITheme.FONT_SMALL);
        appSub.setForeground(new Color(224, 231, 255));
        titleBlock.add(appTitle);
        titleBlock.add(appSub);

        JPanel userBlock = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userBlock.setOpaque(false);

        String userDisplay = (Session.getFullName() != null ? Session.getFullName() : "User")
                + " (" + (Session.getRole() != null ? Session.getRole() : "GUEST") + ")";
        lblUserInfo = new JLabel("👤 " + userDisplay);
        lblUserInfo.setFont(UITheme.FONT_BODY_BOLD);
        lblUserInfo.setForeground(Color.WHITE);

        JButton btnLogout = UITheme.createDangerButton("Logout");
        btnLogout.addActionListener(e -> performLogout());

        userBlock.add(lblUserInfo);
        userBlock.add(btnLogout);

        topHeader.add(titleBlock, BorderLayout.WEST);
        topHeader.add(userBlock, BorderLayout.EAST);
        root.add(topHeader, BorderLayout.NORTH);

        // 2. Navigation Sidebar
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER_COLOR),
                new EmptyBorder(20, 15, 20, 15)
        ));
        navPanel.setPreferredSize(new Dimension(220, 0));

        JLabel navHeading = new JLabel("NAVIGATION");
        navHeading.setFont(UITheme.FONT_SMALL);
        navHeading.setForeground(UITheme.TEXT_MUTED);
        navPanel.add(navHeading);
        navPanel.add(Box.createVerticalStrut(15));

        JButton btnNavDashboard = createNavButton("Dashboard", true);
        JButton btnNavBudget = createNavButton("Budget Management", false);
        JButton btnNavExpenses = createNavButton(" Expense Tracker", false);
        JButton btnNavReports = createNavButton(" Financial Reports", false);
        JButton btnNavUsers = createNavButton("User Management", false);
        JButton btnNavPassword = createNavButton("Change Password", false);
        JButton btnNavExit = createNavButton("Exit Application", false);

        // Role-based restrictions
        if (!Session.isAdmin()) {
            btnNavUsers.setEnabled(false);
            btnNavUsers.setToolTipText("Admin access required");
        }

        btnNavDashboard.addActionListener(e -> loadDashboardMetrics());
        btnNavBudget.addActionListener(e -> new BudgetFrame(this).setVisible(true));
        btnNavExpenses.addActionListener(e -> new ExpenseFrame(this).setVisible(true));
        btnNavReports.addActionListener(e -> new ReportFrame().setVisible(true));
        btnNavUsers.addActionListener(e -> {
            if (Session.isAdmin()) {
                new UserManagementFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Access Denied. Only ADMIN can manage users.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnNavPassword.addActionListener(e -> new ChangePasswordFrame().setVisible(true));
        btnNavExit.addActionListener(e -> System.exit(0));

        navPanel.add(btnNavDashboard);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(btnNavBudget);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(btnNavExpenses);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(btnNavReports);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(btnNavUsers);
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(btnNavPassword);
        navPanel.add(Box.createVerticalGlue());
        navPanel.add(btnNavExit);

        root.add(navPanel, BorderLayout.WEST);

        // 3. Center Content Area (Cards & Quick Details)
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(UITheme.BG_LIGHT);
        centerPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Alert Banner at top of Center Panel
        bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        bannerPanel.setVisible(false);
        lblAlertBanner = new JLabel("");
        lblAlertBanner.setFont(UITheme.FONT_BODY_BOLD);
        bannerPanel.add(lblAlertBanner, BorderLayout.CENTER);
        centerPanel.add(bannerPanel, BorderLayout.NORTH);

        // KPI Cards Grid (2 rows x 3 columns)
        JPanel cardsGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        cardsGrid.setBackground(UITheme.BG_LIGHT);

        lblTotalBudgetVal = new JLabel("₹ 0.00");
        lblTotalExpensesVal = new JLabel("₹ 0.00");
        lblRemainingBudgetVal = new JLabel("₹ 0.00");
        lblUtilizationVal = new JLabel("0.00%");
        lblExpenseCountVal = new JLabel("0");

        cardsGrid.add(createKpiCard("TOTAL ALLOCATED BUDGET", lblTotalBudgetVal, UITheme.PRIMARY, "Total sum of all department budgets"));
        cardsGrid.add(createKpiCard("TOTAL EXPENSES", lblTotalExpensesVal, UITheme.ACCENT_DANGER, "Total expenditure incurred to date"));
        cardsGrid.add(createKpiCard("REMAINING BUDGET", lblRemainingBudgetVal, UITheme.ACCENT_SUCCESS, "Remaining departmental balance"));
        cardsGrid.add(createKpiCard("BUDGET UTILIZATION", lblUtilizationVal, UITheme.SECONDARY, "Expenditure / Allocation * 100"));
        cardsGrid.add(createKpiCard("EXPENSE TRANSACTIONS", lblExpenseCountVal, new Color(139, 92, 246), "Total approved expense vouchers"));
        cardsGrid.add(createActionCard());

        centerPanel.add(cardsGrid, BorderLayout.CENTER);

        // Bottom quick info bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 6));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));

        JButton btnRefresh = UITheme.createSecondaryButton(" Refresh Live Metrics");
        btnRefresh.addActionListener(e -> loadDashboardMetrics());
        bottomBar.add(btnRefresh);

        root.add(bottomBar, BorderLayout.SOUTH);

        centerPanel.add(bottomBar, BorderLayout.SOUTH);
        root.add(centerPanel, BorderLayout.CENTER);

        add(root);
    }

    private JButton createNavButton(String title, boolean active) {
        JButton btn = new JButton(title);
        btn.setFont(UITheme.FONT_BODY);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));

        if (active) {
            btn.setBackground(new Color(238, 242, 255));
            btn.setForeground(UITheme.PRIMARY);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(UITheme.TEXT_DARK);
        }
        return btn;
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor, String subtitle) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UITheme.FONT_SMALL);
        lblTitle.setForeground(UITheme.TEXT_MUTED);

        valueLabel.setFont(UITheme.FONT_CARD_VAL);
        valueLabel.setForeground(UITheme.TEXT_DARK);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(UITheme.TEXT_MUTED);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);

        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createActionCard() {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.ACCENT_WARNING),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblTitle = new JLabel("QUICK ACTIONS");
        lblTitle.setFont(UITheme.FONT_SMALL);
        lblTitle.setForeground(UITheme.TEXT_MUTED);


        JPanel actions = new JPanel(new GridLayout(2, 1, 6, 6));
        actions.setOpaque(false);

        JButton btnAddExp = UITheme.createPrimaryButton("+ Add New Expense");
        btnAddExp.addActionListener(e -> new ExpenseFrame(this).setVisible(true));

        JButton btnViewReport = UITheme.createSecondaryButton(" View Visual Reports");
        btnViewReport.addActionListener(e -> new ReportFrame().setVisible(true));

        actions.add(btnAddExp);
        actions.add(btnViewReport);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(actions, BorderLayout.CENTER);

        return card;
    }

    /**
     * Loads live figures from MySQL and updates dashboard metrics.
     */
    public void loadDashboardMetrics() {
        try {
            BigDecimal totalBudget = budgetDAO.getTotalBudget();
            BigDecimal totalExpenses = expenseDAO.getTotalExpenses();
            BigDecimal remainingBudget = totalBudget.subtract(totalExpenses);
            int expenseCount = expenseDAO.getTotalExpenseCount();

            // Calculate Utilization %
            BigDecimal utilization = BigDecimal.ZERO;
            if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
                utilization = totalExpenses.multiply(new BigDecimal(100))
                        .divide(totalBudget, 2, RoundingMode.HALF_UP);
            }

            lblTotalBudgetVal.setText(UITheme.formatCurrency(totalBudget));
            lblTotalExpensesVal.setText(UITheme.formatCurrency(totalExpenses));
            lblRemainingBudgetVal.setText(UITheme.formatCurrency(remainingBudget));
            lblUtilizationVal.setText(utilization + "%");
            lblExpenseCountVal.setText(String.valueOf(expenseCount));

            // Color coding remaining budget
            if (remainingBudget.compareTo(BigDecimal.ZERO) < 0) {
                lblRemainingBudgetVal.setForeground(UITheme.ACCENT_DANGER);
            } else {
                lblRemainingBudgetVal.setForeground(UITheme.ACCENT_SUCCESS);
            }

            // High utilization or over budget warning logic
            if (totalExpenses.compareTo(totalBudget) > 0 && totalBudget.compareTo(BigDecimal.ZERO) > 0) {
                bannerPanel.setBackground(new Color(233, 233, 233)); // soft green
                lblAlertBanner.setForeground(UITheme.ACCENT_DANGER);
                lblAlertBanner.setText("⚠️ CRITICAL ALERT: Total expenditure exceeds allocated budget by "
                        + UITheme.formatCurrency(totalExpenses.subtract(totalBudget)) + "! Immediate review required.");
                bannerPanel.setVisible(true);
            } else if (utilization.compareTo(new BigDecimal(80)) >= 0) {
                bannerPanel.setBackground(new Color(254, 243, 199)); // soft amber
                lblAlertBanner.setForeground(new Color(180, 83, 9));
                lblAlertBanner.setText("⚠️ WARNING: High budget utilization (" + utilization + "%). Approaching departmental limit.");
                bannerPanel.setVisible(true);
            } else {
                bannerPanel.setVisible(false);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to fetch live dashboard metrics:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Session.logout();
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}

