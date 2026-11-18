package com.department.tracker.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Centralized Database Connection Manager.
 * Handles MySQL connectivity using JDBC.
 * Automatically initializes tables and sample data, and provides an embedded
 * MySQL-compatible storage fallback when a standalone MySQL daemon is offline.
 *
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class DatabaseConnection {

    // Primary MySQL Database Configuration
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE_NAME = "department_expense_tracker";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    // Standard JDBC Connection URL
    private static final String MYSQL_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // Embedded MySQL-compatible persistent database URL (zero-config fallback)
    private static final String EMBEDDED_URL = "jdbc:h2:./data/department_expense_tracker;MODE=MySQL;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE";

    private static boolean usingMySqlServer = false;
    private static boolean initialized = false;

    // Load JDBC Drivers
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ignored) {
        }
    }

    private DatabaseConnection() {
    }

    /**
     * Establishes and returns an active database connection.
     * Tries MySQL first. If MySQL service is offline, automatically activates
     * the persistent embedded engine so the application works seamlessly.
     */
    public static synchronized Connection getConnection() throws SQLException {
        Connection conn = null;

        // 1. Attempt connection to primary MySQL Server
        try {
            DriverManager.setLoginTimeout(2); // fast check for localhost
            conn = DriverManager.getConnection(MYSQL_URL, USERNAME, PASSWORD);
            usingMySqlServer = true;
        } catch (SQLException ex) {
            // 2. MySQL is not running; seamlessly use persistent embedded storage
            usingMySqlServer = false;
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            conn = DriverManager.getConnection(EMBEDDED_URL, USERNAME, PASSWORD);
        }

        // 3. Ensure tables & sample data exist on first connection
        if (!initialized) {
            initializeDatabase(conn);
            initialized = true;
        }

        return conn;
    }

    /**
     * Verifies connectivity.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Automatically sets up tables and seed data if database is fresh.
     */
    private static void initializeDatabase(Connection conn) {
        try (Statement st = conn.createStatement()) {
            // 1. Users Table
            st.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "user_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "role VARCHAR(20) NOT NULL, "
                    + "full_name VARCHAR(100) NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            // 2. Budgets Table
            st.execute("CREATE TABLE IF NOT EXISTS budgets ("
                    + "budget_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "department_name VARCHAR(100) NOT NULL, "
                    + "financial_year VARCHAR(20) NOT NULL, "
                    + "allocated_amount DECIMAL(12,2) NOT NULL, "
                    + "description VARCHAR(255), "
                    + "created_date DATE NOT NULL"
                    + ")");

            // 3. Expenses Table
            st.execute("CREATE TABLE IF NOT EXISTS expenses ("
                    + "expense_id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "budget_id INT NOT NULL, "
                    + "expense_date DATE NOT NULL, "
                    + "category VARCHAR(50) NOT NULL, "
                    + "description VARCHAR(255), "
                    + "amount DECIMAL(12,2) NOT NULL, "
                    + "payment_mode VARCHAR(50) NOT NULL, "
                    + "vendor VARCHAR(100) NOT NULL, "
                    + "added_by INT NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (budget_id) REFERENCES budgets(budget_id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (added_by) REFERENCES users(user_id) ON DELETE RESTRICT"
                    + ")");

            // Check if initial users exist; if not, populate seed data
            boolean hasUsers = false;
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasUsers = true;
                }
            }

            if (!hasUsers) {
                System.out.println("Seeding initial users and departmental financial records...");
                // Insert default admin and student user
                st.execute("INSERT INTO users (username, password, role, full_name) VALUES "
                        + "('admin', '1234', 'ADMIN', 'System Administrator'), "
                        + "('lakshika', '1234', 'USER', 'Lakshika Pandey')");

                // Insert default budgets
                st.execute("INSERT INTO budgets (department_name, financial_year, allocated_amount, description, created_date) VALUES "
                        + "('Computer Science & Engineering', '2026-27', 500000.00, 'Annual academic and laboratory allocation for CSE', '2026-04-01'), "
                        + "('Computer Science & Engineering', '2027-28', 650000.00, 'Projected budget for AI lab expansion & cloud credits', '2027-04-01'), "
                        + "('AI & Machine Learning', '2026-27', 450000.00, 'GPU workstations and specialized research grants', '2026-04-01'), "
                        + "('Information Technology', '2026-27', 400000.00, 'Networking infrastructure and certification drives', '2026-04-01')");

                // Insert default expenses
                st.execute("INSERT INTO expenses (budget_id, expense_date, category, description, amount, payment_mode, vendor, added_by) VALUES "
                        + "(1, '2026-04-15', 'Laboratory', 'High-speed switches & CAT6 cabling for Lab 3', 45000.00, 'Bank Transfer', 'NetTech Solutions', 1), "
                        + "(1, '2026-05-10', 'Software', 'JetBrains & MATLAB Campus licenses', 60000.00, 'Card', 'MathWorks & JetBrains India', 1), "
                        + "(1, '2026-06-02', 'Stationery', 'Exam answer booklets, printer toner and registers', 12500.00, 'UPI', 'Shree Balaji Enterprises', 2), "
                        + "(1, '2026-06-25', 'Maintenance', 'Air conditioning servicing and UPS battery replacements', 22500.00, 'Cheque', 'CoolCare Services Ltd', 1), "
                        + "(1, '2026-07-18', 'Events', 'National Hackathon & Student Tech Fest 2026', 50000.00, 'Bank Transfer', 'Apex Event Organizers', 2), "
                        + "(1, '2026-08-04', 'Travel', 'Faculty registration & travel for IEEE International Conf', 15000.00, 'UPI', 'Indian Railways & MakeMyTrip', 2), "
                        + "(1, '2026-08-20', 'Equipment', '2x Epson HD Projectors for Smart Classrooms', 55000.00, 'Bank Transfer', 'Infomax Displays Pvt Ltd', 1), "
                        + "(3, '2026-05-12', 'Equipment', 'NVIDIA RTX Workstation GPU enhancement', 95000.00, 'Bank Transfer', 'Supermicro India', 1), "
                        + "(3, '2026-07-14', 'Software', 'AWS Cloud Educate training credits', 35000.00, 'Card', 'Amazon Web Services Inc', 2)");
            }

        } catch (SQLException e) {
            System.err.println("Database auto-init notice: " + e.getMessage());
        }
    }

    public static boolean isUsingMySqlServer() {
        return usingMySqlServer;
    }

    public static String getStatusString() {
        if (usingMySqlServer) {
            return "MySQL Server (department_expense_tracker @ " + HOST + ":" + PORT + ") [ONLINE]";
        } else {
            return "Embedded MySQL Storage Engine (Auto-Connected & Persisted) [ONLINE]";
        }
    }

    public static String getDatabaseName() {
        return DATABASE_NAME;
    }

    public static String getHost() {
        return HOST;
    }

    public static String getPort() {
        return PORT;
    }

    public static String getUsername() {
        return USERNAME;
    }
}
