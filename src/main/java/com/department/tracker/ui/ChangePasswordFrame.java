package com.department.tracker.ui;

import com.department.tracker.dao.UserDAO;
import com.department.tracker.util.Session;
import com.department.tracker.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Change Password Frame.
 * Allows authenticated users to safely update their database account password.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class ChangePasswordFrame extends JFrame {

    private final UserDAO userDAO = new UserDAO();

    private JPasswordField txtCurrentPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnSave;
    private JButton btnCancel;

    public ChangePasswordFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Change Account Password");
        setSize(440, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitle = new JLabel("Update Account Password", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_HEADER);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("User: " + Session.getFullName() + " (" + Session.getUsername() + ")", SwingConstants.CENTER);
        lblUser.setFont(UITheme.FONT_SMALL);
        lblUser.setForeground(new Color(224, 231, 255));

        headerPanel.add(lblTitle);
        headerPanel.add(lblUser);
        root.add(headerPanel, BorderLayout.NORTH);

        // Form Card
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCurrentPassword = new JPasswordField(15);
        txtNewPassword = new JPasswordField(15);
        txtConfirmPassword = new JPasswordField(15);

        // Current Password
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        JLabel lblCurrent = new JLabel("Current Password:*");
        lblCurrent.setFont(UITheme.FONT_BODY_BOLD);
        formCard.add(lblCurrent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        formCard.add(txtCurrentPassword, gbc);

        // New Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblNew = new JLabel("New Password:*");
        lblNew.setFont(UITheme.FONT_BODY_BOLD);
        formCard.add(lblNew, gbc);

        gbc.gridx = 1;
        formCard.add(txtNewPassword, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblConfirm = new JLabel("Confirm Password:*");
        lblConfirm.setFont(UITheme.FONT_BODY_BOLD);
        formCard.add(lblConfirm, gbc);

        gbc.gridx = 1;
        formCard.add(txtConfirmPassword, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setOpaque(false);

        btnSave = UITheme.createPrimaryButton("Update Password");
        btnCancel = UITheme.createSecondaryButton("Cancel");

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        formCard.add(btnPanel, gbc);

        root.add(formCard, BorderLayout.CENTER);
        add(root);

        // Event Listeners
        btnSave.addActionListener(e -> performPasswordChange());
        btnCancel.addActionListener(e -> dispose());
    }

    private void performPasswordChange() {
        String currentPassword = new String(txtCurrentPassword.getPassword()).trim();
        String newPassword = new String(txtNewPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

        if (ValidationUtil.isEmpty(currentPassword) || ValidationUtil.isEmpty(newPassword) || ValidationUtil.isEmpty(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "All password fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {
            JOptionPane.showMessageDialog(this, "New password must be at least 4 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New password and Confirm password do not match!", "Password Mismatch", JOptionPane.WARNING_MESSAGE);
            txtConfirmPassword.requestFocus();
            return;
        }

        int userId = Session.getUserId();
        try {
            // 1. Verify current password
            boolean isOldPasswordCorrect = userDAO.verifyPassword(userId, currentPassword);
            if (!isOldPasswordCorrect) {
                JOptionPane.showMessageDialog(this, "Incorrect current password. Please try again.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
                txtCurrentPassword.requestFocus();
                return;
            }

            // 2. Update password
            boolean updated = userDAO.changePassword(userId, newPassword);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Password has been updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
