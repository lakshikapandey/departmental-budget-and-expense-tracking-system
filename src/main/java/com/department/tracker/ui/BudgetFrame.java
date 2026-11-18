package com.department.tracker.ui;

import com.department.tracker.dao.BudgetDAO;
import com.department.tracker.model.Budget;
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
import java.util.List;

/**
 * Budget Management Frame.
 * Supports CRUD and Search operations on Departmental Budgets.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class BudgetFrame extends JFrame {

    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final DashboardFrame parentDashboard;

    // Form inputs
    private JTextField txtBudgetId;
    private JComboBox<String> cmbDepartment;
    private JTextField txtFinancialYear;
    private JTextField txtAllocatedAmount;
    private JTextField txtDescription;
    private JTextField txtCreatedDate;

    // Action buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JButton btnRefresh;

    // Search input
    private JTextField txtSearch;

    // JTable
    private JTable budgetTable;
    private DefaultTableModel tableModel;

    public BudgetFrame(DashboardFrame parentDashboard) {
        this.parentDashboard = parentDashboard;
        initUI();
        loadBudgetData();
    }

    private void initUI() {
        setTitle("Budget Management - Departmental Budget & Expense Tracking System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG_LIGHT);
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblTitle = new JLabel("Departmental Budget Allocation");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Add, update, search, and manage annual allocations");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(new Color(224, 231, 255));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setOpaque(false);
        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);
        headerPanel.add(titleBlock, BorderLayout.WEST);

        root.add(headerPanel, BorderLayout.NORTH);

        // 2. Left Form Panel
        JPanel formCard = UITheme.createCardPanel();
        formCard.setPreferredSize(new Dimension(360, 0));
        formCard.setLayout(new BorderLayout(10, 10));

        JLabel formHeader = new JLabel("Budget Details");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtBudgetId = new JTextField();
        txtBudgetId.setEditable(false);
        txtBudgetId.setBackground(new Color(241, 245, 249));

        String[] departments = {
                "Computer Science & Engineering",
                "AI & Machine Learning",
                "Information Technology",
                "Electronics & Communication",
                "Mechanical Engineering",
                "Applied Sciences & Humanities"
        };
        cmbDepartment = new JComboBox<>(departments);
        cmbDepartment.setEditable(true);

        txtFinancialYear = new JTextField("2026-27");
        txtAllocatedAmount = new JTextField();
        txtDescription = new JTextField();
        txtCreatedDate = new JTextField(LocalDate.now().toString());

        addFormField(fieldsPanel, gbc, 0, "Budget ID (Auto):", txtBudgetId);
        addFormField(fieldsPanel, gbc, 1, "Department Name:*", cmbDepartment);
        addFormField(fieldsPanel, gbc, 2, "Financial Year (YYYY-YY):*", txtFinancialYear);
        addFormField(fieldsPanel, gbc, 3, "Allocated Amount (₹):*", txtAllocatedAmount);
        addFormField(fieldsPanel, gbc, 4, "Description / Purpose:", txtDescription);
        addFormField(fieldsPanel, gbc, 5, "Created Date (YYYY-MM-DD):*", txtCreatedDate);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Form Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = UITheme.createPrimaryButton("Add Budget");
        btnUpdate = UITheme.createSecondaryButton("Update Budget");
        btnDelete = UITheme.createDangerButton("Delete Budget");
        btnClear = UITheme.createSecondaryButton("Clear Fields");

        // Role restriction check: Only ADMIN can modify budgets
        if (!Session.isAdmin()) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            btnAdd.setToolTipText("Admin privilege required to add budget");
            btnUpdate.setToolTipText("Admin privilege required to update budget");
            btnDelete.setToolTipText("Admin privilege required to delete budget");
        }

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        formCard.add(btnPanel, BorderLayout.SOUTH);
        root.add(formCard, BorderLayout.WEST);

        // 3. Center Table & Search Panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setOpaque(false);

        // Search bar
        JPanel searchPanel = UITheme.createCardPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel lblSearch = new JLabel("Search Budget:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        txtSearch = new JTextField(18);
        txtSearch.setFont(UITheme.FONT_BODY);

        btnSearch = UITheme.createPrimaryButton("Search");
        btnRefresh = UITheme.createSecondaryButton("Refresh All");

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // JTable Setup
        String[] columns = {"ID", "Department", "Financial Year", "Allocated (₹)", "Description", "Created Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only cells
            }
        };
        budgetTable = new JTable(tableModel);
        UITheme.styleTable(budgetTable);
        budgetTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        budgetTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        budgetTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        budgetTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        budgetTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        budgetTable.getColumnModel().getColumn(5).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(budgetTable);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        root.add(rightPanel, BorderLayout.CENTER);
        add(root);

        // Event Handlers
        btnAdd.addActionListener(e -> addBudget());
        btnUpdate.addActionListener(e -> updateBudget());
        btnDelete.addActionListener(e -> deleteBudget());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchBudgets());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadBudgetData();
        });

        // Click row to populate form
        budgetTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && budgetTable.getSelectedRow() != -1) {
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

    private void loadBudgetData() {
        try {
            List<Budget> list = budgetDAO.getAllBudgets();
            populateTable(list);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading budgets: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Budget> list) {
        tableModel.setRowCount(0);
        for (Budget b : list) {
            tableModel.addRow(new Object[]{
                    b.getBudgetId(),
                    b.getDepartmentName(),
                    b.getFinancialYear(),
                    UITheme.formatCurrency(b.getAllocatedAmount()),
                    b.getDescription(),
                    b.getCreatedDate()
            });
        }
    }

    private void populateFormFromSelectedRow() {
        int row = budgetTable.getSelectedRow();
        if (row >= 0) {
            txtBudgetId.setText(tableModel.getValueAt(row, 0).toString());
            cmbDepartment.setSelectedItem(tableModel.getValueAt(row, 1).toString());
            txtFinancialYear.setText(tableModel.getValueAt(row, 2).toString());

            String amountRaw = tableModel.getValueAt(row, 3).toString()
                    .replace("₹", "").replace(",", "").trim();
            txtAllocatedAmount.setText(amountRaw);
            txtDescription.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
            txtCreatedDate.setText(tableModel.getValueAt(row, 5).toString());
        }
    }

    private void addBudget() {
        if (!validateInput()) return;

        try {
            Budget b = extractBudgetFromForm();
            boolean success = budgetDAO.addBudget(b);
            if (success) {
                JOptionPane.showMessageDialog(this, "Budget record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadBudgetData();
                if (parentDashboard != null) parentDashboard.loadDashboardMetrics();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add budget.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBudget() {
        if (ValidationUtil.isEmpty(txtBudgetId.getText())) {
            JOptionPane.showMessageDialog(this, "Please select a budget from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInput()) return;

        try {
            Budget b = extractBudgetFromForm();
            b.setBudgetId(Integer.parseInt(txtBudgetId.getText().trim()));

            boolean success = budgetDAO.updateBudget(b);
            if (success) {
                JOptionPane.showMessageDialog(this, "Budget record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadBudgetData();
                if (parentDashboard != null) parentDashboard.loadDashboardMetrics();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update budget.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBudget() {
        if (ValidationUtil.isEmpty(txtBudgetId.getText())) {
            JOptionPane.showMessageDialog(this, "Please select a budget from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int budgetId = Integer.parseInt(txtBudgetId.getText().trim());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Budget ID #" + budgetId + "?\n"
                + "WARNING: All associated expenses for this budget will also be deleted (CASCADE)!",
                "Confirm Budget Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = budgetDAO.deleteBudget(budgetId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Budget deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadBudgetData();
                    if (parentDashboard != null) parentDashboard.loadDashboardMetrics();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete budget.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchBudgets() {
        String keyword = txtSearch.getText().trim();
        if (ValidationUtil.isEmpty(keyword)) {
            loadBudgetData();
            return;
        }
        try {
            List<Budget> results = budgetDAO.searchBudgets(keyword);
            populateTable(results);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No budget records found matching: " + keyword, "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        String dept = (String) cmbDepartment.getSelectedItem();
        String fy = txtFinancialYear.getText().trim();
        String amount = txtAllocatedAmount.getText().trim();
        String dateStr = txtCreatedDate.getText().trim();

        if (ValidationUtil.isEmpty(dept)) {
            JOptionPane.showMessageDialog(this, "Please specify a department name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!ValidationUtil.isValidFinancialYear(fy)) {
            JOptionPane.showMessageDialog(this, "Financial Year must be in format YYYY-YY or YYYY-YYYY (e.g., 2026-27).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtFinancialYear.requestFocus();
            return false;
        }
        if (!ValidationUtil.isPositiveDecimal(amount)) {
            JOptionPane.showMessageDialog(this, "Allocated Amount must be a positive numeric value greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtAllocatedAmount.requestFocus();
            return false;
        }
        if (!ValidationUtil.isValidDate(dateStr)) {
            JOptionPane.showMessageDialog(this, "Created Date must be valid in format YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtCreatedDate.requestFocus();
            return false;
        }
        return true;
    }

    private Budget extractBudgetFromForm() {
        String dept = (String) cmbDepartment.getSelectedItem();
        String fy = txtFinancialYear.getText().trim();
        BigDecimal amount = new BigDecimal(txtAllocatedAmount.getText().trim());
        String desc = txtDescription.getText().trim();
        Date date = ValidationUtil.parseSqlDate(txtCreatedDate.getText().trim());

        return new Budget(dept, fy, amount, desc, date);
    }

    private void clearForm() {
        txtBudgetId.setText("");
        if (cmbDepartment.getItemCount() > 0) cmbDepartment.setSelectedIndex(0);
        txtFinancialYear.setText("2026-27");
        txtAllocatedAmount.setText("");
        txtDescription.setText("");
        txtCreatedDate.setText(LocalDate.now().toString());
        budgetTable.clearSelection();
    }
}
