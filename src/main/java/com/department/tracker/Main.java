package com.department.tracker;

import com.department.tracker.ui.LoginFrame;
import com.department.tracker.util.DatabaseConnection;

import javax.swing.*;

/**
 * Main Application Entry Point.
 * Departmental Budget & Expense Tracking System.
 * Author: Lakshika Pandey (Student ID: PIET25CR029)
 */
public class Main {

    public static void main(String[] args) {
        // Set Look and Feel for modern appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Check Database Connectivity on startup
            boolean connected = DatabaseConnection.testConnection();
            if (!connected) {
                System.out.println("Notice: MySQL connection could not be established immediately on localhost:3306.");
                System.out.println("Make sure MySQL service is running and 'database.sql' has been imported.");
            } else {
                System.out.println("Database connection established successfully to " + DatabaseConnection.getDatabaseName() + ".");
            }

            // Launch Login Screen
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
