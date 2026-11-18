package com.department.tracker.ui;

import com.department.tracker.dao.BudgetDAO;
import com.department.tracker.dao.ExpenseDAO;
import com.department.tracker.model.Budget;
import com.department.tracker.model.Expense;
import com.department.tracker.util.Session;
import com.department.tracker.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Expense Management Frame.
 * Handles adding, editing, deleting, searching, and filtering of departmental expenses
 * with live budget overrun validation.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class ExpenseFrame extends JFrame {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final DashboardFrame parentDashboard;

    // Form inputs
    private JTextField txtExpenseId;
    private JComboBox<BudgetComboItem> cmbBudget;
    private JTextField txtExpenseDate;
    private JComboBox<String> cmbCategory;
    private JTextField txtDescription;
    private JTextField txtAmount;
    private JComboBox<String> cmbPaymentMode;
    private JTextField txtVendor;
    private JTextField txtAddedByDisplay;

    // Action buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // Search and Filter controls
    private JTextField txtSearch;
    private JButton btnSearch;
    private JComboBox<String> cmbFilterCategory;
    private JComboBox<String> cmbFilterYear;
    private JTextField txtFilterStartDate;
    private JTextField txtFilterEndDate;
    private JButton btnApplyFilter;
    private JButton btnResetFilter;

    // JTable
    private JTable expenseTable;
    private DefaultTableModel tableModel;

    // Helper item for Budget ComboBox
    public static class BudgetComboItem {
        private final int budgetId;
        private final String label;

        public BudgetComboItem(int budgetId, String label) {
            this.budgetId = budgetId;
            this.label = label;
        }

        public int getBudgetId() {
            return budgetId;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public ExpenseFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        initUI();
        loadBudgetsIntoCombo();
        loadExpenses();
    }

    private void initUI() {
        setTitle("Expense Management - Departmental Budget & Expense Tracking System");
        setSize(1180, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG_LIGHT);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblTitle = new JLabel("Departmental Expense Records");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Log expenses, verify against budget limits, and track department vouchers");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(224, 231, 255));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setOpaque(false);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);
        headerPanel.add(titleBlock, BorderLayout.WEST);

        root.add(headerPanel, BorderLayout.NORTH);

        // 2. Left Form Panel (380px wide)
        JPanel formCard = UITheme.createCardPanel();
        formCard.setPreferredSize(new Dimension(380, 0));
        formCard.setLayout(new BorderLayout(8, 8));

        JLabel formHeader = new JLabel("Expense Voucher Entry");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtExpenseId = new JTextField();
        txtExpenseId.setEditable(false);
        txtExpenseId.setBackground(new Color(241, 245, 249));

        cmbBudget = new JComboBox<>();
        txtExpenseDate = new JTextField(LocalDate.now().toString());

        String[] categories = {
                "Laboratory",
                "Equipment",
                "Stationery",
                "Events",
                "Maintenance",
                "Travel",
                "Software",
                "Infrastructure",
                "Miscellaneous"
        };
        cmbCategory = new JComboBox<>(categories);

        txtDescription = new JTextField();
        txtAmount = new JTextField();

        String[] paymentModes = {"Cash", "UPI", "Card", "Bank Transfer", "Cheque"};
        cmbPaymentMode = new JComboBox<>(paymentModes);

        txtVendor = new JTextField();

        txtAddedByDisplay = new JTextField((Session.getFullName() != null ? Session.getFullName() : "User") + " (" + Session.getRole() + ")");
        txtAddedByDisplay.setEditable(false);
        txtAddedByDisplay.setBackground(new Color(241, 245, 249));

        addFormField(fieldsPanel, gbc, 0, "Expense ID:", txtExpenseId);
        addFormField(fieldsPanel, gbc, 1, "Select Budget:*", cmbBudget);
        addFormField(fieldsPanel, gbc, 2, "Expense Date:*", txtExpenseDate);
        addFormField(fieldsPanel, gbc, 3, "Category:*", cmbCategory);
        addFormField(fieldsPanel, gbc, 4, "Amount (₹):*", txtAmount);
        addFormField(fieldsPanel, gbc, 5, "Description / Purpose:", txtDescription);
        addFormField(fieldsPanel, gbc, 6, "Payment Mode:*", cmbPaymentMode);
        addFormField(fieldsPanel, gbc, 7, "Vendor / Payee:*", txtVendor);
        addFormField(fieldsPanel, gbc, 8, "Recorded By:", txtAddedByDisplay);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Form Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        btnPanel.setOpaque(false);

        btnAdd = UITheme.createPrimaryButton("Add Expense");
        btnUpdate = UITheme.createSecondaryButton("Update Expense");
        btnDelete = UITheme.createDangerButton("Delete Expense");
        btnClear = UITheme.createSecondaryButton("Clear Fields");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        formCard.add(btnPanel, BorderLayout.SOUTH);
        root.add(formCard, BorderLayout.WEST);

        // 3. Center Table & Filter/Search Area
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setOpaque(false);

        // Top Filter & Search Card
        JPanel filterCard = UITheme.createCardPanel();
        filterCard.setLayout(new BoxLayout(filterCard, BoxLayout.Y_AXIS));

        // Row 1: Search Keyword
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchRow.setOpaque(false);
        searchRow.add(new JLabel("Search Expenses:"));
        txtSearch = new JTextField(20);
        btnSearch = UITheme.createPrimaryButton("Search");
        searchRow.add(txtSearch);
        searchRow.add(btnSearch);

        // Row 2: Advanced Filters (Category, Year, Dates)
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterRow.setOpaque(false);

        filterRow.add(new JLabel("Category:"));
        List<String> filterCatList = new ArrayList<>();
        filterCatList.add("All");
        for (String c : categories) filterCatList.add(c);
        cmbFilterCategory = new JComboBox<>(filterCatList.toArray(new String[0]));
        filterRow.add(cmbFilterCategory);

        filterRow.add(new JLabel("Financial Year:"));
        cmbFilterYear = new JComboBox<>(new String[]{"All", "2026-27", "2027-28"});
        filterRow.add(cmbFilterYear);

        filterRow.add(new JLabel("From:"));
        txtFilterStartDate = new JTextField(8);
        filterRow.add(txtFilterStartDate);

        filterRow.add(new JLabel("To:"));
        txtFilterEndDate = new JTextField(8);
        filterRow.add(txtFilterEndDate);

        btnApplyFilter = UITheme.createPrimaryButton("Filter");
        btnResetFilter = UITheme.createSecondaryButton("Reset All");
        filterRow.add(btnApplyFilter);
        filterRow.add(btnResetFilter);

        filterCard.add(searchRow);
        filterCard.add(filterRow);
        centerPanel.add(filterCard, BorderLayout.NORTH);

        // JTable Setup
        String[] columns = {
                "ID", "Date", "Department", "FY", "Category", "Amount (₹)", "Payment", "Vendor", "Recorded By"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        expenseTable = new JTable(tableModel);
        UITheme.styleTable(expenseTable);

        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(85);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(5).setPreferredWidth(95);
        expenseTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        expenseTable.getColumnModel().getColumn(7).setPreferredWidth(130);
        expenseTable.getColumnModel().getColumn(8).setPreferredWidth(110);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        root.add(centerPanel, BorderLayout.CENTER);
        add(root);

        // Event listeners
        btnAdd.addActionListener(e -> addExpense());
        btnUpdate.addActionListener(e -> updateExpense());
        btnDelete.addActionListener(e -> deleteExpense());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchExpenses());
        btnApplyFilter.addActionListener(e -> applyFilter());
        btnResetFilter.addActionListener(e -> {
            txtSearch.setText("");
            cmbFilterCategory.setSelectedIndex(0);
            cmbFilterYear.setSelectedIndex(0);
            txtFilterStartDate.setText("");
            txtFilterEndDate.setText("");
            loadExpenses();
        });

        expenseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && expenseTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setFont(UITheme.FONT_BODY);
        panel.add(field, gbc);
    }

    private void loadBudgetsIntoCombo() {
        try {
            cmbBudget.removeAllItems();
            List<Budget> list = budgetDAO.getAllBudgets();
            for (Budget b : list) {
                String label = "#" + b.getBudgetId() + " - " + b.getDepartmentName() + " (" + b.getFinancialYear() + ")";
                cmbBudget.addItem(new BudgetComboItem(b.getBudgetId(), label));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading budget options: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadExpenses() {
        try {
            List<Expense> list = expenseDAO.getAllExpenses();
            populateTable(list);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Expense> list) {
        tableModel.setRowCount(0);
        for (Expense exp : list) {
            tableModel.addRow(new Object[]{
                    exp.getExpenseId(),
                    exp.getExpenseDate(),
                    exp.getDepartmentName(),
                    exp.getFinancialYear(),
                    exp.getCategory(),
                    UITheme.formatCurrency(exp.getAmount()),
                    exp.getPaymentMode(),
                    exp.getVendor(),
                    exp.getAddedByName()
            });
        }
    }

    private void populateFormFromSelectedRow() {
        int row = expenseTable.getSelectedRow();
        if (row >= 0) {
            int expenseId = (int) tableModel.getValueAt(row, 0);
            try {
                Expense exp = expenseDAO.getExpenseById(expenseId);
                if (exp != null) {
                    txtExpenseId.setText(String.valueOf(exp.getExpenseId()));
                    txtExpenseDate.setText(exp.getExpenseDate().toString());
                    cmbCategory.setSelectedItem(exp.getCategory());
                    txtAmount.setText(exp.getAmount().toString());
                    txtDescription.setText(exp.getDescription() != null ? exp.getDescription() : "");
                    cmbPaymentMode.setSelectedItem(exp.getPaymentMode());
                    txtVendor.setText(exp.getVendor());
                    txtAddedByDisplay.setText(exp.getAddedByName() != null ? exp.getAddedByName() : String.valueOf(exp.getAddedBy()));

                    // Match budget in combobox
                    for (int i = 0; i < cmbBudget.getItemCount(); i++) {
                        BudgetComboItem item = cmbBudget.getItemAt(i);
                        if (item.getBudgetId() == exp.getBudgetId()) {
                            cmbBudget.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error fetching expense details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addExpense() {
        if (!validateInput()) return;

        BudgetComboItem selectedBudget = (BudgetComboItem) cmbBudget.getSelectedItem();
        if (selectedBudget == null) {
            JOptionPane.showMessageDialog(this, "Please select an allocated budget.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal requestedAmount = new BigDecimal(txtAmount.getText().trim());

        // LIVE BUDGET OVERRUN VALIDATION
        try {
            int budgetId = selectedBudget.getBudgetId();
            Budget budget = budgetDAO.getBudgetById(budgetId);
            BigDecimal alreadySpent = budgetDAO.getBudgetSpentAmount(budgetId);
            BigDecimal availableBudget = budget.getAllocatedAmount().subtract(alreadySpent);

            if (requestedAmount.compareTo(availableBudget) > 0) {
                String message = String.format(
                        "Expense cannot be added because it exceeds the available budget!\n\n"
                        + "Department: %s (%s)\n"
                        + "Total Budget Allocated: %s\n"
                        + "Already Spent to date: %s\n"
                        + "Available Balance: %s\n"
                        + "Requested Expense Amount: %s\n\n",
                        budget.getDepartmentName(), budget.getFinancialYear(),
                        UITheme.formatCurrency(budget.getAllocatedAmount()),
                        UITheme.formatCurrency(alreadySpent),
                        UITheme.formatCurrency(availableBudget),
                        UITheme.formatCurrency(requestedAmount)
                );

                if (Session.isAdmin()) {
                    int override = JOptionPane.showConfirmDialog(this,
                            message + "As ADMIN, do you wish to override and approve this budget overrun?",
                            "Budget Limit Exceeded - Admin Override",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (override != JOptionPane.YES_OPTION) {
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            message + "Entries exceeding available budget are blocked. Contact Department Admin.",
                            "Expense Exceeds Available Budget",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Expense exp = extractExpenseFromForm(budgetId);
            exp.setAddedBy(Session.getUserId());

            boolean success = expenseDAO.addExpense(exp);
            if (success) {
                JOptionPane.showMessageDialog(this, "Expense recorded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadExpenses();
                if (parentDashboard != null) parentDashboard.loadDashboardMetrics();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to record expense.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error during expense validation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateExpense() {
        if (ValidationUtil.isEmpty(txtExpenseId.getText())) {
            JOptionPane.showMessageDialog(this, "Please select an expense from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInput()) return;

        BudgetComboItem selectedBudget = (BudgetComboItem) cmbBudget.getSelectedItem();
        if (selectedBudget == null) return;

        try {
            int expenseId = Integer.parseInt(txtExpenseId.getText().trim());
            Expense exp = extractExpenseFromForm(selectedBudget.getBudgetId());
            exp.setExpenseId(expenseId);

            boolean success = expenseDAO.updateExpense(exp);
            if (success) {
                JOptionPane.showMessageDialog(this, "Expense updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadExpenses();
                if (parentDashboard != null) parentDashboard.loadDashboardMetrics();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update expense.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExpense() {
        if (ValidationUtil.isEmpty(txtExpenseId.getText())) {
            JOptionPane.showMessageDialog(this, "Please select an expense from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expenseId = Integer.parseInt(txtExpenseId.getText().trim());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Expense ID #" + expenseId + "?",
                "Confirm Expense Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = expenseDAO.deleteExpense(expenseId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Expense deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadExpenses();
                    if (parentDashboard != null) parentDashboard.loadDashboardMetrics();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete expense.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchExpenses() {
        String kw = txtSearch.getText().trim();
        if (ValidationUtil.isEmpty(kw)) {
            loadExpenses();
            return;
        }
        try {
            List<Expense> results = expenseDAO.searchExpenses(kw);
            populateTable(results);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No expenses found matching: " + kw, "Search Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        String cat = (String) cmbFilterCategory.getSelectedItem();
        String fy = (String) cmbFilterYear.getSelectedItem();
        String startStr = txtFilterStartDate.getText().trim();
        String endStr = txtFilterEndDate.getText().trim();

        Date startDate = null;
        Date endDate = null;

        if (!ValidationUtil.isEmpty(startStr)) {
            if (!ValidationUtil.isValidDate(startStr)) {
                JOptionPane.showMessageDialog(this, "Filter 'From' date must be YYYY-MM-DD.", "Filter Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            startDate = ValidationUtil.parseSqlDate(startStr);
        }

        if (!ValidationUtil.isEmpty(endStr)) {
            if (!ValidationUtil.isValidDate(endStr)) {
                JOptionPane.showMessageDialog(this, "Filter 'To' date must be YYYY-MM-DD.", "Filter Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            endDate = ValidationUtil.parseSqlDate(endStr);
        }

        try {
            List<Expense> filtered = expenseDAO.filterExpenses(cat, startDate, endDate, fy);
            populateTable(filtered);
            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No records match the applied filter criteria.", "Filter Results", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Filter query error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        String dateStr = txtExpenseDate.getText().trim();
        String amountStr = txtAmount.getText().trim();
        String vendor = txtVendor.getText().trim();

        if (!ValidationUtil.isValidDate(dateStr)) {
            JOptionPane.showMessageDialog(this, "Expense date must be valid in YYYY-MM-DD format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtExpenseDate.requestFocus();
            return false;
        }

        if (!ValidationUtil.isPositiveDecimal(amountStr)) {
            JOptionPane.showMessageDialog(this, "Amount must be a positive numeric value greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtAmount.requestFocus();
            return false;
        }

        if (ValidationUtil.isEmpty(vendor)) {
            JOptionPane.showMessageDialog(this, "Vendor/Payee cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtVendor.requestFocus();
            return false;
        }

        return true;
    }

    private Expense extractExpenseFromForm(int budgetId) {
        Date date = ValidationUtil.parseSqlDate(txtExpenseDate.getText().trim());
        String cat = (String) cmbCategory.getSelectedItem();
        String desc = txtDescription.getText().trim();
        BigDecimal amount = new BigDecimal(txtAmount.getText().trim());
        String mode = (String) cmbPaymentMode.getSelectedItem();
        String vendor = txtVendor.getText().trim();

        return new Expense(budgetId, date, cat, desc, amount, mode, vendor, Session.getUserId());
    }

    private void clearForm() {
        txtExpenseId.setText("");
        txtExpenseDate.setText(LocalDate.now().toString());
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
        txtDescription.setText("");
        txtAmount.setText("");
        if (cmbPaymentMode.getItemCount() > 0) cmbPaymentMode.setSelectedIndex(0);
        txtVendor.setText("");
        expenseTable.clearSelection();
    }
}
