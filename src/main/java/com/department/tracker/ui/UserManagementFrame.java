package com.department.tracker.ui;

import com.department.tracker.dao.UserDAO;
import com.department.tracker.model.User;
import com.department.tracker.util.Session;
import com.department.tracker.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * User Management Screen (ADMIN ONLY).
 * Facilitates adding, updating, and deleting application user accounts.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class UserManagementFrame extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    // Form inputs
    private JTextField txtUserId;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtFullName;
    private JComboBox<String> cmbRole;

    // Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // Table
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagementFrame() {
        // Enforce Admin Access
        if (!Session.isAdmin()) {
            JOptionPane.showMessageDialog(null, "Access Denied: Administrator role required.", "Forbidden", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        initUI();
        loadUserData();
    }

    private void initUI() {
        setTitle("User Management (Admin Console) - Departmental Budget & Expense Tracking System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG_LIGHT);
        root.setBorder(new EmptyBorder(12, 14, 12, 14));

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblTitle = new JLabel("System User Management");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Manage departmental user credentials and role-based permissions");
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
        formCard.setPreferredSize(new Dimension(340, 0));
        formCard.setLayout(new BorderLayout(8, 8));

        JLabel formHeader = new JLabel("User Account Details");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.PRIMARY);
        formCard.add(formHeader, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtUserId = new JTextField();
        txtUserId.setEditable(false);
        txtUserId.setBackground(new Color(241, 245, 249));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtFullName = new JTextField();
        cmbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});

        addFormField(fieldsPanel, gbc, 0, "User ID:", txtUserId);
        addFormField(fieldsPanel, gbc, 1, "Username:*", txtUsername);
        addFormField(fieldsPanel, gbc, 2, "Password:*", txtPassword);
        addFormField(fieldsPanel, gbc, 3, "Full Name:*", txtFullName);
        addFormField(fieldsPanel, gbc, 4, "Role:*", cmbRole);

        formCard.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        btnPanel.setOpaque(false);

        btnAdd = UITheme.createPrimaryButton("Add User");
        btnUpdate = UITheme.createSecondaryButton("Update User");
        btnDelete = UITheme.createDangerButton("Delete User");
        btnClear = UITheme.createSecondaryButton("Clear");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        formCard.add(btnPanel, BorderLayout.SOUTH);
        root.add(formCard, BorderLayout.WEST);

        // 3. Right Table
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(8, 8));

        JLabel tableHeader = new JLabel("Existing Registered Users");
        tableHeader.setFont(UITheme.FONT_HEADER);
        tableCard.add(tableHeader, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Username", "Role", "Full Name", "Created At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        UITheme.styleTable(userTable);

        userTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(140);

        tableCard.add(new JScrollPane(userTable), BorderLayout.CENTER);
        root.add(tableCard, BorderLayout.CENTER);

        add(root);

        // Event Handlers
        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearForm());

        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userTable.getSelectedRow() != -1) {
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

    private void loadUserData() {
        try {
            List<User> list = userDAO.getAllUsers();
            tableModel.setRowCount(0);
            for (User u : list) {
                tableModel.addRow(new Object[]{
                        u.getUserId(),
                        u.getUsername(),
                        u.getRole(),
                        u.getFullName(),
                        u.getCreatedAt()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching user accounts: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelectedRow() {
        int row = userTable.getSelectedRow();
        if (row >= 0) {
            int userId = (int) tableModel.getValueAt(row, 0);
            try {
                User u = userDAO.getUserById(userId);
                if (u != null) {
                    txtUserId.setText(String.valueOf(u.getUserId()));
                    txtUsername.setText(u.getUsername());
                    txtPassword.setText(""); // Keep password field blank for security during edit
                    txtFullName.setText(u.getFullName());
                    cmbRole.setSelectedItem(u.getRole());
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error retrieving user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String role = (String) cmbRole.getSelectedItem();

        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(fullName)) {
            JOptionPane.showMessageDialog(this, "Username and Full Name are required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        try {
            if (userDAO.isUsernameTaken(username, 0)) {
                JOptionPane.showMessageDialog(this, "Username '" + username + "' is already taken. Please choose another.", "Duplicate Username", JOptionPane.WARNING_MESSAGE);
                txtUsername.requestFocus();
                return;
            }

            User user = new User(username, password, role, fullName);
            boolean success = userDAO.addUser(user);
            if (success) {
                JOptionPane.showMessageDialog(this, "User account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        if (ValidationUtil.isEmpty(txtUserId.getText())) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(txtUserId.getText().trim());
        String username = txtUsername.getText().trim();
        String fullName = txtFullName.getText().trim();
        String role = (String) cmbRole.getSelectedItem();

        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(fullName)) {
            JOptionPane.showMessageDialog(this, "Username and Full Name are required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (userDAO.isUsernameTaken(username, userId)) {
                JOptionPane.showMessageDialog(this, "Username '" + username + "' is already registered to another account.", "Duplicate Username", JOptionPane.WARNING_MESSAGE);
                txtUsername.requestFocus();
                return;
            }

            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
            user.setFullName(fullName);
            user.setRole(role);

            boolean success = userDAO.updateUser(user);

            // Optional password update if admin entered a new password
            String newPassword = new String(txtPassword.getPassword()).trim();
            if (!ValidationUtil.isEmpty(newPassword)) {
                if (!ValidationUtil.isValidPassword(newPassword)) {
                    JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                userDAO.changePassword(userId, newPassword);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "User account updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUserData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        if (ValidationUtil.isEmpty(txtUserId.getText())) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(txtUserId.getText().trim());

        // Prevent self-deletion
        if (userId == Session.getUserId()) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own active administrator account!", "Action Prohibited", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user ID #" + userId + " (" + txtUsername.getText() + ")?",
                "Confirm User Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = userDAO.deleteUser(userId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadUserData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete user because records or expenses are linked to this account.\n"
                        + "Details: " + ex.getMessage(),
                        "Foreign Key Constraint",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtUserId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtFullName.setText("");
        cmbRole.setSelectedIndex(0);
        userTable.clearSelection();
    }
}
