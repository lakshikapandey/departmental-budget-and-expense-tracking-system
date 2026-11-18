package com.department.tracker.ui;

import com.department.tracker.dao.UserDAO;
import com.department.tracker.model.User;
import com.department.tracker.util.DatabaseConnection;
import com.department.tracker.util.Session;
import com.department.tracker.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Login Screen for Departmental Budget & Expense Tracking System.
 * Authenticates users against MySQL database.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblStatus;

    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Login - Departmental Budget & Expense Tracking System");
        setSize(480, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // Header Panel
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

        // Form Card Panel
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(25, 35, 20, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.FONT_BODY_BOLD);
        lblUser.setForeground(UITheme.TEXT_DARK);
        formCard.add(lblUser, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtUsername = new JTextField(15);
        txtUsername.setFont(UITheme.FONT_BODY);
        formCard.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.FONT_BODY_BOLD);
        lblPass.setForeground(UITheme.TEXT_DARK);
        formCard.add(lblPass, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(UITheme.FONT_BODY);
        formCard.add(txtPassword, gbc);

        // Status / Hint label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        lblStatus = new JLabel("Default: admin/1234 (Admin) or lakshika/1234 (User)", SwingConstants.CENTER);
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.TEXT_MUTED);
        formCard.add(lblStatus, gbc);

        // Buttons Panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setOpaque(false);

        btnLogin = UITheme.createPrimaryButton("Login");
        btnExit = UITheme.createSecondaryButton("Exit");

        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);
        formCard.add(btnPanel, gbc);

        mainPanel.add(formCard, BorderLayout.CENTER);

        // Footer status bar
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(241, 245, 249));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR));
        JLabel lblDbInfo = new JLabel(" Database: " + DatabaseConnection.getStatusString());
        lblDbInfo.setFont(UITheme.FONT_SMALL);
        lblDbInfo.setForeground(UITheme.TEXT_MUTED);
        footer.add(lblDbInfo);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        // Action Listeners
        btnLogin.addActionListener(e -> performLogin());
        btnExit.addActionListener(e -> System.exit(0));

        // Enter key to trigger login
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Input Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = userDAO.login(username, password);
            if (user != null) {
                // Store user in session
                Session.login(user);

                JOptionPane.showMessageDialog(this,
                        "Welcome, " + user.getFullName() + "!\nLogged in successfully as: " + user.getRole(),
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                // Open Dashboard and close LoginFrame
                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database connection error:\n" + ex.getMessage() + "\n\n"
                    + "Please ensure MySQL service is running and 'department_expense_tracker' database exists.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
