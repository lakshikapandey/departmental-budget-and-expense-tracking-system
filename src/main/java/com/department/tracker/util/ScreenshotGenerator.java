package com.department.tracker.util;

import com.department.tracker.model.User;
import com.department.tracker.ui.UITheme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility to generate high-fidelity test screenshots of all application screens.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class ScreenshotGenerator {

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        File dir = new File("screenshots");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Initialize mock session for Lakshika Pandey
        Session.login(new User(1, "lakshika", "1234", "ADMIN", "Lakshika Pandey"));

        try {
            System.out.println("Generating screen captures in screenshots/ ...");

            // 1. Login Screen
            captureLoginScreen(new File(dir, "01_login_screen.png"));

            // 2. Dashboard Screen
            captureDashboardScreen(new File(dir, "02_dashboard_live_metrics.png"));

            // 3. Budget Management Screen
            captureBudgetScreen(new File(dir, "03_budget_management.png"));

            // 4. Expense Management Screen
            captureExpenseScreen(new File(dir, "04_expense_management.png"));

            // 5. Budget Overrun Validation Dialog
            captureBudgetOverrunDialog(new File(dir, "05_budget_overrun_validation.png"));

            // 6. Financial Reports & 2D Bar Chart
            captureReportScreen(new File(dir, "06_financial_reports_analytics.png"));

            // 7. User Management Screen (Admin Console)
            captureUserManagementScreen(new File(dir, "07_user_management_admin.png"));

            // 8. Change Password Screen
            captureChangePasswordScreen(new File(dir, "08_change_password.png"));

            System.out.println("All 8 screenshots generated successfully in " + dir.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveComponent(Component comp, int width, int height, File outFile) throws IOException {
        comp.setSize(width, height);
        layoutTree(comp);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        comp.printAll(g2);
        g2.dispose();
        ImageIO.write(image, "PNG", outFile);
        System.out.println("Saved: " + outFile.getName() + " (" + width + "x" + height + ")");
    }

    private static void layoutTree(Component comp) {
        comp.doLayout();
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                layoutTree(child);
            }
        }
    }

    // 1. Login Screen
    private static void captureLoginScreen(File file) throws IOException {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Budget & Expense Tracker", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("College Department Financial System", SwingConstants.CENTER);
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(new Color(224, 231, 255));

        JLabel lblAuthor = new JLabel("PIET Minor Project - Lakshika Pandey (PIET25CR029)", SwingConstants.CENTER);
        lblAuthor.setFont(UITheme.FONT_SMALL);
        lblAuthor.setForeground(new Color(199, 210, 254));

        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        headerPanel.add(lblAuthor);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(25, 35, 20, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.FONT_BODY_BOLD);
        formCard.add(lblUser, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField txtUsername = new JTextField("admin", 15);
        txtUsername.setFont(UITheme.FONT_BODY);
        formCard.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.FONT_BODY_BOLD);
        formCard.add(lblPass, gbc);

        gbc.gridx = 1;
        JPasswordField txtPassword = new JPasswordField("••••", 15);
        txtPassword.setFont(UITheme.FONT_BODY);
        formCard.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel lblStatus = new JLabel("Default: admin/1234 (Admin) or lakshika/1234 (User)", SwingConstants.CENTER);
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.TEXT_MUTED);
        formCard.add(lblStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(UITheme.createPrimaryButton("Login"));
        btnPanel.add(UITheme.createSecondaryButton("Exit"));
        formCard.add(btnPanel, gbc);

        mainPanel.add(formCard, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(241, 245, 249));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));
        JLabel lblDb = new JLabel(" Database: MySQL (department_expense_tracker @ localhost:3306)");
        lblDb.setFont(UITheme.FONT_SMALL);
        lblDb.setForeground(UITheme.TEXT_MUTED);
        footer.add(lblDb);
        mainPanel.add(footer, BorderLayout.SOUTH);

        saveComponent(mainPanel, 480, 440, file);
    }

    // 2. Dashboard Screen
    private static void captureDashboardScreen(File file) throws IOException {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // Header
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
        JLabel lblUserInfo = new JLabel("User: Lakshika Pandey (ADMIN)");
        lblUserInfo.setFont(UITheme.FONT_BODY_BOLD);
        lblUserInfo.setForeground(Color.WHITE);
        userBlock.add(lblUserInfo);
        userBlock.add(UITheme.createDangerButton("Logout"));

        topHeader.add(titleBlock, BorderLayout.WEST);
        topHeader.add(userBlock, BorderLayout.EAST);
        root.add(topHeader, BorderLayout.NORTH);

        // Nav Sidebar
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER_COLOR),
                new EmptyBorder(20, 15, 20, 15)
        ));
        navPanel.setPreferredSize(new Dimension(220, 0));

        navPanel.add(new JLabel("NAVIGATION"));
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(makeNavBtn("Dashboard", true));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(makeNavBtn("Budget Management", false));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(makeNavBtn("Expense Tracker", false));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(makeNavBtn("Financial Reports", false));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(makeNavBtn("User Management", false));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(makeNavBtn("Change Password", false));
        navPanel.add(Box.createVerticalGlue());
        navPanel.add(makeNavBtn("Exit Application", false));
        root.add(navPanel, BorderLayout.WEST);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(UITheme.BG_LIGHT);
        centerPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Amber Warning Banner
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(new Color(254, 243, 199));
        bannerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel lblAlert = new JLabel("SYSTEM STATUS: All department budgets active for Academic Session 2026-27. Live data synced with MySQL.");
        lblAlert.setFont(UITheme.FONT_BODY_BOLD);
        lblAlert.setForeground(new Color(180, 83, 9));
        bannerPanel.add(lblAlert, BorderLayout.CENTER);
        centerPanel.add(bannerPanel, BorderLayout.NORTH);

        // Cards Grid
        JPanel cardsGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        cardsGrid.setBackground(UITheme.BG_LIGHT);

        cardsGrid.add(makeKpiCard("TOTAL ALLOCATED BUDGET", "Rs. 20,00,000.00", UITheme.PRIMARY, "Total across CSE, AI&ML, IT departments"));
        cardsGrid.add(makeKpiCard("TOTAL EXPENSES", "Rs. 3,35,000.00", UITheme.ACCENT_DANGER, "Total approved department vouchers"));
        cardsGrid.add(makeKpiCard("REMAINING BUDGET", "Rs. 16,65,000.00", UITheme.ACCENT_SUCCESS, "Net available spending funds"));
        cardsGrid.add(makeKpiCard("BUDGET UTILIZATION", "16.75%", UITheme.SECONDARY, "Expenditure / Allocation * 100"));
        cardsGrid.add(makeKpiCard("EXPENSE TRANSACTIONS", "9 Vouchers", new Color(139, 92, 246), "Audit-verified transaction count"));

        // Quick Action Card
        JPanel actionCard = UITheme.createCardPanel();
        actionCard.setLayout(new BorderLayout(8, 8));
        actionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.ACCENT_WARNING),
                new EmptyBorder(12, 14, 12, 14)
        ));
        JLabel actTitle = new JLabel("QUICK ACTIONS");
        actTitle.setFont(UITheme.FONT_SMALL);
        actTitle.setForeground(UITheme.TEXT_MUTED);
        JPanel actBtns = new JPanel(new GridLayout(2, 1, 6, 6));
        actBtns.setOpaque(false);
        actBtns.add(UITheme.createPrimaryButton("+ Add New Expense"));
        actBtns.add(UITheme.createSecondaryButton("View Visual Reports"));
        actionCard.add(actTitle, BorderLayout.NORTH);
        actionCard.add(actBtns, BorderLayout.CENTER);
        cardsGrid.add(actionCard);

        centerPanel.add(cardsGrid, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 6));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));
        bottomBar.add(UITheme.createSecondaryButton("Refresh Live Metrics"));
        centerPanel.add(bottomBar, BorderLayout.SOUTH);

        root.add(centerPanel, BorderLayout.CENTER);

        saveComponent(root, 980, 680, file);
    }

    // 3. Budget Management Screen
    private static void captureBudgetScreen(File file) throws IOException {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG_LIGHT);
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblTitle = new JLabel("Departmental Budget Allocation");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Add, update, search, and manage annual allocations");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(224, 231, 255));
        JPanel tb = new JPanel(new GridLayout(2, 1));
        tb.setOpaque(false);
        tb.add(lblTitle); tb.add(lblSub);
        headerPanel.add(tb, BorderLayout.WEST);
        root.add(headerPanel, BorderLayout.NORTH);

        // Left Form
        JPanel formCard = UITheme.createCardPanel();
        formCard.setPreferredSize(new Dimension(360, 0));
        formCard.setLayout(new BorderLayout(10, 10));

        JLabel formHeader = new JLabel("Budget Details");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(6, 2, 6, 8));
        fields.setOpaque(false);
        fields.add(new JLabel("Budget ID:"));
        JTextField txtId = new JTextField("1"); txtId.setEditable(false); fields.add(txtId);
        fields.add(new JLabel("Department Name:*"));
        fields.add(new JComboBox<>(new String[]{"Computer Science & Engineering", "AI & Machine Learning"}));
        fields.add(new JLabel("Financial Year:*"));
        fields.add(new JTextField("2026-27"));
        fields.add(new JLabel("Allocated Amount (₹):*"));
        fields.add(new JTextField("500000.00"));
        fields.add(new JLabel("Description:"));
        fields.add(new JTextField("Annual academic & lab allocation"));
        fields.add(new JLabel("Created Date:*"));
        fields.add(new JTextField("2026-04-01"));
        formCard.add(fields, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(UITheme.createPrimaryButton("Add Budget"));
        btnPanel.add(UITheme.createSecondaryButton("Update Budget"));
        btnPanel.add(UITheme.createDangerButton("Delete Budget"));
        btnPanel.add(UITheme.createSecondaryButton("Clear Fields"));
        formCard.add(btnPanel, BorderLayout.SOUTH);
        root.add(formCard, BorderLayout.WEST);

        // Right Table
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setOpaque(false);

        JPanel searchPanel = UITheme.createCardPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.add(new JLabel("Search Budget:"));
        searchPanel.add(new JTextField(18));
        searchPanel.add(UITheme.createPrimaryButton("Search"));
        searchPanel.add(UITheme.createSecondaryButton("Refresh All"));
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Department", "Financial Year", "Allocated (₹)", "Description", "Created Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        model.addRow(new Object[]{1, "Computer Science & Engineering", "2026-27", "₹ 5,00,000.00", "Annual academic and lab allocation", "2026-04-01"});
        model.addRow(new Object[]{2, "Computer Science & Engineering", "2027-28", "₹ 6,50,000.00", "Projected budget for AI lab expansion", "2027-04-01"});
        model.addRow(new Object[]{3, "AI & Machine Learning", "2026-27", "₹ 4,50,000.00", "GPU workstations and research grants", "2026-04-01"});
        model.addRow(new Object[]{4, "Information Technology", "2026-27", "₹ 4,00,000.00", "Networking infrastructure & certification", "2026-04-01"});

        JTable table = new JTable(model);
        UITheme.styleTable(table);
        table.setRowSelectionInterval(0, 0);
        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(rightPanel, BorderLayout.CENTER);

        saveComponent(root, 1000, 650, file);
    }

    // 4. Expense Management Screen
    private static void captureExpenseScreen(File file) throws IOException {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG_LIGHT);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblTitle = new JLabel("Departmental Expense Records");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Log expenses, verify against budget limits, and track department vouchers");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(224, 231, 255));
        JPanel tb = new JPanel(new GridLayout(2, 1));
        tb.setOpaque(false);
        tb.add(lblTitle); tb.add(lblSub);
        headerPanel.add(tb, BorderLayout.WEST);
        root.add(headerPanel, BorderLayout.NORTH);

        // Left Form
        JPanel formCard = UITheme.createCardPanel();
        formCard.setPreferredSize(new Dimension(380, 0));
        formCard.setLayout(new BorderLayout(8, 8));

        JLabel formHeader = new JLabel("Expense Voucher Entry");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(9, 2, 4, 6));
        fields.setOpaque(false);
        fields.add(new JLabel("Expense ID:"));
        JTextField txtId = new JTextField("1"); txtId.setEditable(false); fields.add(txtId);
        fields.add(new JLabel("Select Budget:*"));
        fields.add(new JComboBox<>(new String[]{"#1 - CSE (2026-27)", "#3 - AI & ML (2026-27)"}));
        fields.add(new JLabel("Expense Date:*"));
        fields.add(new JTextField("2026-04-15"));
        fields.add(new JLabel("Category:*"));
        fields.add(new JComboBox<>(new String[]{"Laboratory", "Software", "Equipment", "Events"}));
        fields.add(new JLabel("Amount (₹):*"));
        fields.add(new JTextField("45000.00"));
        fields.add(new JLabel("Description:"));
        fields.add(new JTextField("Switches & CAT6 cabling for Lab 3"));
        fields.add(new JLabel("Payment Mode:*"));
        fields.add(new JComboBox<>(new String[]{"Bank Transfer", "UPI", "Card", "Cheque"}));
        fields.add(new JLabel("Vendor / Payee:*"));
        fields.add(new JTextField("NetTech Solutions"));
        fields.add(new JLabel("Recorded By:"));
        JTextField txtRec = new JTextField("Lakshika Pandey (ADMIN)"); txtRec.setEditable(false); fields.add(txtRec);
        formCard.add(fields, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        btnPanel.setOpaque(false);
        btnPanel.add(UITheme.createPrimaryButton("Add Expense"));
        btnPanel.add(UITheme.createSecondaryButton("Update Expense"));
        btnPanel.add(UITheme.createDangerButton("Delete Expense"));
        btnPanel.add(UITheme.createSecondaryButton("Clear Fields"));
        formCard.add(btnPanel, BorderLayout.SOUTH);
        root.add(formCard, BorderLayout.WEST);

        // Right Filter + Table
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setOpaque(false);

        JPanel filterCard = UITheme.createCardPanel();
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterCard.add(new JLabel("Search:"));
        filterCard.add(new JTextField(12));
        filterCard.add(new JLabel("Category:"));
        filterCard.add(new JComboBox<>(new String[]{"All", "Laboratory", "Software", "Equipment"}));
        filterCard.add(new JLabel("Year:"));
        filterCard.add(new JComboBox<>(new String[]{"All", "2026-27", "2027-28"}));
        filterCard.add(UITheme.createPrimaryButton("Filter"));
        filterCard.add(UITheme.createSecondaryButton("Reset"));
        centerPanel.add(filterCard, BorderLayout.NORTH);

        String[] cols = {"ID", "Date", "Department", "FY", "Category", "Amount (₹)", "Payment", "Vendor", "Recorded By"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        model.addRow(new Object[]{1, "2026-04-15", "CSE", "2026-27", "Laboratory", "₹ 45,000.00", "Bank Transfer", "NetTech Solutions", "System Administrator"});
        model.addRow(new Object[]{2, "2026-05-10", "CSE", "2026-27", "Software", "₹ 60,000.00", "Card", "MathWorks & JetBrains", "System Administrator"});
        model.addRow(new Object[]{3, "2026-06-02", "CSE", "2026-27", "Stationery", "₹ 12,500.00", "UPI", "Shree Balaji Enterprises", "Lakshika Pandey"});
        model.addRow(new Object[]{4, "2026-06-25", "CSE", "2026-27", "Maintenance", "₹ 22,500.00", "Cheque", "CoolCare Services Ltd", "System Administrator"});
        model.addRow(new Object[]{5, "2026-07-18", "CSE", "2026-27", "Events", "₹ 50,000.00", "Bank Transfer", "Apex Event Organizers", "Lakshika Pandey"});
        model.addRow(new Object[]{6, "2026-08-04", "CSE", "2026-27", "Travel", "₹ 15,000.00", "UPI", "Indian Railways & MMT", "Lakshika Pandey"});
        model.addRow(new Object[]{7, "2026-08-20", "CSE", "2026-27", "Equipment", "₹ 55,000.00", "Bank Transfer", "Infomax Displays Ltd", "System Administrator"});
        model.addRow(new Object[]{8, "2026-05-12", "AI & ML", "2026-27", "Equipment", "₹ 95,000.00", "Bank Transfer", "Supermicro India", "System Administrator"});
        model.addRow(new Object[]{9, "2026-07-14", "AI & ML", "2026-27", "Software", "₹ 35,000.00", "Card", "AWS Cloud Educate", "Lakshika Pandey"});

        JTable table = new JTable(model);
        UITheme.styleTable(table);
        table.setRowSelectionInterval(0, 0);
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        root.add(centerPanel, BorderLayout.CENTER);
        saveComponent(root, 1180, 720, file);
    }

    // 5. Budget Overrun Validation Dialog Screen
    private static void captureBudgetOverrunDialog(File file) throws IOException {
        JPanel backdrop = new JPanel(new BorderLayout());
        backdrop.setBackground(new Color(241, 245, 249));

        JPanel dialog = UITheme.createCardPanel();
        dialog.setLayout(new BorderLayout(15, 15));
        dialog.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.ACCENT_DANGER, 2),
                new EmptyBorder(20, 25, 20, 25)
        ));
        dialog.setPreferredSize(new Dimension(540, 320));

        JPanel titleP = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleP.setOpaque(false);
        JLabel icon = new JLabel("🚫");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        JLabel title = new JLabel("BUDGET LIMIT EXCEEDED - TRANSACTION BLOCKED");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(UITheme.ACCENT_DANGER);
        titleP.add(icon);
        titleP.add(title);
        dialog.add(titleP, BorderLayout.NORTH);

        JTextArea body = new JTextArea(
                "Expense cannot be added because it exceeds the available departmental budget!\n\n"
                + "• Department: Computer Science & Engineering (FY 2026-27)\n"
                + "• Total Budget Allocated: ₹ 5,00,000.00\n"
                + "• Already Spent to date: ₹ 2,60,000.00\n"
                + "• Available Balance: ₹ 2,40,000.00\n"
                + "• Requested Expense: ₹ 3,15,000.00 (DEFICIT: ₹ 75,000.00)\n\n"
                + "Status: Strict database validation prevented this transaction to avoid departmental deficit.\n"
                + "Authorized Action: Request additional allocation from Head of Department or Admin."
        );
        body.setFont(UITheme.FONT_BODY);
        body.setEditable(false);
        body.setOpaque(false);
        dialog.add(body, BorderLayout.CENTER);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnP.setOpaque(false);
        btnP.add(UITheme.createSecondaryButton("Close Dialog"));
        btnP.add(UITheme.createDangerButton("Admin Override (Requires Role Approval)"));
        dialog.add(btnP, BorderLayout.SOUTH);

        backdrop.add(dialog, BorderLayout.CENTER);
        saveComponent(backdrop, 620, 400, file);
    }

    // 6. Financial Reports & Analytics Screen
    private static void captureReportScreen(File file) throws IOException {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG_LIGHT);
        root.setBorder(new EmptyBorder(12, 14, 12, 14));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblTitle = new JLabel("Departmental Financial Reports & Analytics");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Category breakdowns, monthly expenditure trends, and visual budget utilization");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(224, 231, 255));
        JPanel tb = new JPanel(new GridLayout(2, 1));
        tb.setOpaque(false);
        tb.add(lblTitle); tb.add(lblSub);
        headerPanel.add(tb, BorderLayout.WEST);
        headerPanel.add(UITheme.createSecondaryButton("🔄 Refresh Reports"), BorderLayout.EAST);
        root.add(headerPanel, BorderLayout.NORTH);

        // KPI mini strip
        JPanel summaryStrip = new JPanel(new GridLayout(1, 4, 12, 12));
        summaryStrip.setOpaque(false);
        summaryStrip.add(makeKpiCard("TOTAL BUDGET", "₹ 20,00,000.00", UITheme.PRIMARY, "All Departments"));
        summaryStrip.add(makeKpiCard("TOTAL EXPENSES", "₹ 3,89,000.00", UITheme.ACCENT_DANGER, "To Date"));
        summaryStrip.add(makeKpiCard("REMAINING BALANCE", "₹ 16,11,000.00", UITheme.ACCENT_SUCCESS, "Net Available"));
        summaryStrip.add(makeKpiCard("UTILIZATION %", "19.45%", UITheme.SECONDARY, "Safe Utilization"));

        // Split view: Left Table, Right Custom 2D Graphics Chart
        JPanel splitView = new JPanel(new GridLayout(1, 2, 12, 12));
        splitView.setOpaque(false);

        // Left Table: Category breakdown
        JPanel leftCard = UITheme.createCardPanel();
        leftCard.setLayout(new BorderLayout(8, 8));
        JLabel catH = new JLabel("Category-wise Spending Breakdown");
        catH.setFont(UITheme.FONT_HEADER);
        leftCard.add(catH, BorderLayout.NORTH);

        DefaultTableModel catModel = new DefaultTableModel(new String[]{"Category", "Total Spent (₹)", "Share (%)"}, 0);
        catModel.addRow(new Object[]{"Equipment", "₹ 1,50,000.00", "38.56%"});
        catModel.addRow(new Object[]{"Software", "₹ 95,000.00", "24.42%"});
        catModel.addRow(new Object[]{"Events", "₹ 50,000.00", "12.85%"});
        catModel.addRow(new Object[]{"Laboratory", "₹ 45,000.00", "11.57%"});
        catModel.addRow(new Object[]{"Maintenance", "₹ 22,500.00", "5.78%"});
        catModel.addRow(new Object[]{"Travel", "₹ 15,000.00", "3.86%"});
        catModel.addRow(new Object[]{"Stationery", "₹ 12,500.00", "3.21%"});

        JTable catTable = new JTable(catModel);
        UITheme.styleTable(catTable);
        leftCard.add(new JScrollPane(catTable), BorderLayout.CENTER);
        splitView.add(leftCard);

        // Right Visual Chart Panel (Graphics2D)
        JPanel chartCard = UITheme.createCardPanel();
        chartCard.setLayout(new BorderLayout(8, 8));
        JLabel chartH = new JLabel("Visual Expense Distribution (Pure Java Swing 2D)");
        chartH.setFont(UITheme.FONT_HEADER);
        chartCard.add(chartH, BorderLayout.NORTH);

        Map<String, BigDecimal> chartData = new LinkedHashMap<>();
        chartData.put("Equipment", new BigDecimal("150000"));
        chartData.put("Software", new BigDecimal("95000"));
        chartData.put("Events", new BigDecimal("50000"));
        chartData.put("Lab", new BigDecimal("45000"));
        chartData.put("Maint.", new BigDecimal("22500"));
        chartData.put("Travel", new BigDecimal("15000"));
        chartData.put("Stat.", new BigDecimal("12500"));

        JPanel visualPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int pad = 40;
                int bPad = 50;
                int chartH = h - pad - bPad;
                int chartW = w - (pad * 2);

                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawLine(pad, pad + chartH, pad + chartW, pad + chartH);

                Color[] colors = {
                        new Color(59, 130, 246), new Color(16, 185, 129),
                        new Color(245, 158, 11), new Color(239, 68, 68),
                        new Color(139, 92, 246), new Color(236, 72, 153),
                        new Color(20, 184, 166)
                };

                double max = 150000.0;
                int totalBarW = chartW / chartData.size();
                int barW = totalBarW - 14;

                int i = 0;
                for (Map.Entry<String, BigDecimal> e : chartData.entrySet()) {
                    double val = e.getValue().doubleValue();
                    int barH = (int) ((val / max) * (chartH - 25));
                    int x = pad + (i * totalBarW) + 7;
                    int y = pad + chartH - barH;

                    g2.setColor(colors[i % colors.length]);
                    g2.fillRoundRect(x, y, barW, barH, 6, 6);

                    g2.setColor(UITheme.TEXT_DARK);
                    g2.setFont(UITheme.FONT_SMALL);
                    String vStr = "₹" + (int)(val / 1000) + "k";
                    g2.drawString(vStr, x + (barW / 2) - 12, y - 5);

                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.drawString(e.getKey(), x + 2, pad + chartH + 18);
                    i++;
                }
            }
        };
        visualPanel.setBackground(Color.WHITE);
        chartCard.add(visualPanel, BorderLayout.CENTER);
        splitView.add(chartCard);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(summaryStrip, BorderLayout.NORTH);
        center.add(splitView, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        saveComponent(root, 1040, 680, file);
    }

    // 7. User Management Screen (Admin Console)
    private static void captureUserManagementScreen(File file) throws IOException {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG_LIGHT);
        root.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblTitle = new JLabel("System User Management (Admin Console)");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Manage departmental user accounts, roles, and administrative privileges");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(224, 231, 255));
        JPanel tb = new JPanel(new GridLayout(2, 1));
        tb.setOpaque(false);
        tb.add(lblTitle); tb.add(lblSub);
        headerPanel.add(tb, BorderLayout.WEST);
        root.add(headerPanel, BorderLayout.NORTH);

        // Left Form
        JPanel formCard = UITheme.createCardPanel();
        formCard.setPreferredSize(new Dimension(340, 0));
        formCard.setLayout(new BorderLayout(8, 8));

        JLabel formHeader = new JLabel("User Account Details");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(5, 2, 4, 8));
        fields.setOpaque(false);
        fields.add(new JLabel("User ID:"));
        JTextField txtUid = new JTextField("2"); txtUid.setEditable(false); fields.add(txtUid);
        fields.add(new JLabel("Username:*"));
        fields.add(new JTextField("lakshika"));
        fields.add(new JLabel("Password:*"));
        fields.add(new JPasswordField("••••"));
        fields.add(new JLabel("Full Name:*"));
        fields.add(new JTextField("Lakshika Pandey"));
        fields.add(new JLabel("Role:*"));
        fields.add(new JComboBox<>(new String[]{"ADMIN", "USER"}));
        formCard.add(fields, BorderLayout.CENTER);

        JPanel btnP = new JPanel(new GridLayout(2, 2, 6, 6));
        btnP.setOpaque(false);
        btnP.add(UITheme.createPrimaryButton("Add User"));
        btnP.add(UITheme.createSecondaryButton("Update User"));
        btnP.add(UITheme.createDangerButton("Delete User"));
        btnP.add(UITheme.createSecondaryButton("Clear Fields"));
        formCard.add(btnP, BorderLayout.SOUTH);
        root.add(formCard, BorderLayout.WEST);

        // Right Table
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(8, 8));
        JLabel tableH = new JLabel("Active Registered Department Accounts");
        tableH.setFont(UITheme.FONT_HEADER);
        tableCard.add(tableH, BorderLayout.NORTH);

        DefaultTableModel uModel = new DefaultTableModel(new String[]{"ID", "Username", "Role", "Full Name", "Created Date"}, 0);
        uModel.addRow(new Object[]{1, "admin", "ADMIN", "System Administrator", "2026-04-01 10:00:00"});
        uModel.addRow(new Object[]{2, "lakshika", "USER", "Lakshika Pandey", "2026-04-01 10:05:00"});
        uModel.addRow(new Object[]{3, "hod_cse", "ADMIN", "Prof. Rajesh Sharma (HOD CSE)", "2026-04-02 09:30:00"});
        uModel.addRow(new Object[]{4, "lab_assistant", "USER", "Vikas Kumar (Lab Tech)", "2026-04-05 11:15:00"});

        JTable uTable = new JTable(uModel);
        UITheme.styleTable(uTable);
        uTable.setRowSelectionInterval(1, 1);
        tableCard.add(new JScrollPane(uTable), BorderLayout.CENTER);
        root.add(tableCard, BorderLayout.CENTER);

        saveComponent(root, 900, 600, file);
    }

    // 8. Change Password Screen
    private static void captureChangePasswordScreen(File file) throws IOException {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Update Account Password", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("User: Lakshika Pandey (lakshika)", SwingConstants.CENTER);
        lblUser.setFont(UITheme.FONT_SMALL);
        lblUser.setForeground(new Color(224, 231, 255));
        headerPanel.add(lblTitle);
        headerPanel.add(lblUser);
        root.add(headerPanel, BorderLayout.NORTH);

        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        JLabel lblCur = new JLabel("Current Password:*");
        lblCur.setFont(UITheme.FONT_BODY_BOLD);
        formCard.add(lblCur, gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        formCard.add(new JPasswordField("••••", 15), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblNew = new JLabel("New Password:*");
        lblNew.setFont(UITheme.FONT_BODY_BOLD);
        formCard.add(lblNew, gbc);
        gbc.gridx = 1;
        formCard.add(new JPasswordField("••••••••", 15), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblConf = new JLabel("Confirm Password:*");
        lblConf.setFont(UITheme.FONT_BODY_BOLD);
        formCard.add(lblConf, gbc);
        gbc.gridx = 1;
        formCard.add(new JPasswordField("••••••••", 15), gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(UITheme.createPrimaryButton("Update Password"));
        btnPanel.add(UITheme.createSecondaryButton("Cancel"));
        formCard.add(btnPanel, gbc);

        root.add(formCard, BorderLayout.CENTER);
        saveComponent(root, 440, 360, file);
    }

    private static JButton makeNavBtn(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BODY);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
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

    private static JPanel makeKpiCard(String title, String val, Color accent, String sub) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(12, 14, 12, 14)
        ));
        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL);
        t.setForeground(UITheme.TEXT_MUTED);

        JLabel v = new JLabel(val);
        v.setFont(UITheme.FONT_CARD_VAL);
        v.setForeground(UITheme.TEXT_DARK);

        JLabel s = new JLabel(sub);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        s.setForeground(UITheme.TEXT_MUTED);

        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        card.add(s, BorderLayout.SOUTH);
        return card;
    }
}

