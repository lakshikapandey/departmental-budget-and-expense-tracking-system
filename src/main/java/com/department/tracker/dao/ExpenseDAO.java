package com.department.tracker.dao;

import com.department.tracker.model.Expense;
import com.department.tracker.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Expense entity.
 * Uses PreparedStatements for all database operations.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class ExpenseDAO {

    /**
     * Inserts a new expense into MySQL.
     */
    public boolean addExpense(Expense expense) throws SQLException {
        String sql = "INSERT INTO expenses (budget_id, expense_date, category, description, amount, payment_mode, vendor, added_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, expense.getBudgetId());
            ps.setDate(2, expense.getExpenseDate());
            ps.setString(3, expense.getCategory());
            ps.setString(4, expense.getDescription());
            ps.setBigDecimal(5, expense.getAmount());
            ps.setString(6, expense.getPaymentMode());
            ps.setString(7, expense.getVendor());
            ps.setInt(8, expense.getAddedBy());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all expenses with joined budget and user information.
     */
    public List<Expense> getAllExpenses() throws SQLException {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT e.expense_id, e.budget_id, e.expense_date, e.category, e.description, e.amount, "
                   + "       e.payment_mode, e.vendor, e.added_by, e.created_at, "
                   + "       b.department_name, b.financial_year, u.full_name AS added_by_name "
                   + "FROM expenses e "
                   + "JOIN budgets b ON e.budget_id = b.budget_id "
                   + "JOIN users u ON e.added_by = u.user_id "
                   + "ORDER BY e.expense_date DESC, e.expense_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToExpense(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves an expense by ID.
     */
    public Expense getExpenseById(int expenseId) throws SQLException {
        String sql = "SELECT e.expense_id, e.budget_id, e.expense_date, e.category, e.description, e.amount, "
                   + "       e.payment_mode, e.vendor, e.added_by, e.created_at, "
                   + "       b.department_name, b.financial_year, u.full_name AS added_by_name "
                   + "FROM expenses e "
                   + "JOIN budgets b ON e.budget_id = b.budget_id "
                   + "JOIN users u ON e.added_by = u.user_id "
                   + "WHERE e.expense_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, expenseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExpense(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing expense record.
     */
    public boolean updateExpense(Expense expense) throws SQLException {
        String sql = "UPDATE expenses SET budget_id = ?, expense_date = ?, category = ?, description = ?, "
                   + "amount = ?, payment_mode = ?, vendor = ? WHERE expense_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, expense.getBudgetId());
            ps.setDate(2, expense.getExpenseDate());
            ps.setString(3, expense.getCategory());
            ps.setString(4, expense.getDescription());
            ps.setBigDecimal(5, expense.getAmount());
            ps.setString(6, expense.getPaymentMode());
            ps.setString(7, expense.getVendor());
            ps.setInt(8, expense.getExpenseId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an expense by ID.
     */
    public boolean deleteExpense(int expenseId) throws SQLException {
        String sql = "DELETE FROM expenses WHERE expense_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, expenseId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Searches expenses by category, description, vendor, or department name.
     */
    public List<Expense> searchExpenses(String keyword) throws SQLException {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT e.expense_id, e.budget_id, e.expense_date, e.category, e.description, e.amount, "
                   + "       e.payment_mode, e.vendor, e.added_by, e.created_at, "
                   + "       b.department_name, b.financial_year, u.full_name AS added_by_name "
                   + "FROM expenses e "
                   + "JOIN budgets b ON e.budget_id = b.budget_id "
                   + "JOIN users u ON e.added_by = u.user_id "
                   + "WHERE e.category LIKE ? OR e.description LIKE ? OR e.vendor LIKE ? "
                   + "   OR b.department_name LIKE ? OR e.payment_mode LIKE ? "
                   + "ORDER BY e.expense_date DESC, e.expense_id DESC";
        String pattern = "%" + keyword + "%";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            ps.setString(5, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExpense(rs));
                }
            }
        }
        return list;
    }

    /**
     * Filters expenses by category, date range, and financial year.
     */
    public List<Expense> filterExpenses(String category, Date startDate, Date endDate, String financialYear) throws SQLException {
        List<Expense> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT e.expense_id, e.budget_id, e.expense_date, e.category, e.description, e.amount, "
          + "       e.payment_mode, e.vendor, e.added_by, e.created_at, "
          + "       b.department_name, b.financial_year, u.full_name AS added_by_name "
          + "FROM expenses e "
          + "JOIN budgets b ON e.budget_id = b.budget_id "
          + "JOIN users u ON e.added_by = u.user_id "
          + "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (category != null && !category.equalsIgnoreCase("All")) {
            sql.append("AND e.category = ? ");
            params.add(category);
        }
        if (startDate != null) {
            sql.append("AND e.expense_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("AND e.expense_date <= ? ");
            params.add(endDate);
        }
        if (financialYear != null && !financialYear.equalsIgnoreCase("All")) {
            sql.append("AND b.financial_year = ? ");
            params.add(financialYear);
        }

        sql.append("ORDER BY e.expense_date DESC, e.expense_id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) {
                    ps.setString(i + 1, (String) p);
                } else if (p instanceof Date) {
                    ps.setDate(i + 1, (Date) p);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExpense(rs));
                }
            }
        }
        return list;
    }

    /**
     * Returns total sum of expenses across the system.
     */
    public BigDecimal getTotalExpenses() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_amount FROM expenses";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("total_amount");
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Returns count of all expense entries.
     */
    public int getTotalExpenseCount() throws SQLException {
        String sql = "SELECT COUNT(*) AS total_count FROM expenses";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_count");
            }
        }
        return 0;
    }

    /**
     * Calculates total expenses grouped by category.
     */
    public Map<String, BigDecimal> getCategoryWiseExpenses() throws SQLException {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        String sql = "SELECT category, COALESCE(SUM(amount), 0) AS total "
                   + "FROM expenses GROUP BY category ORDER BY total DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("category"), rs.getBigDecimal("total"));
            }
        }
        return map;
    }

    /**
     * Calculates monthly expense breakdown (YYYY-MM).
     */
    public Map<String, BigDecimal> getMonthlyExpenses() throws SQLException {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        String sql = "SELECT SUBSTRING(CAST(expense_date AS VARCHAR(10)), 1, 7) AS month_label, COALESCE(SUM(amount), 0) AS total "
                   + "FROM expenses GROUP BY month_label ORDER BY month_label ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("month_label"), rs.getBigDecimal("total"));
            }
        }
        return map;
    }

    /**
     * Returns total expenses for a specific budget ID.
     */
    public BigDecimal getTotalExpensesForBudget(int budgetId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM expenses WHERE budget_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        Expense exp = new Expense();
        exp.setExpenseId(rs.getInt("expense_id"));
        exp.setBudgetId(rs.getInt("budget_id"));
        exp.setExpenseDate(rs.getDate("expense_date"));
        exp.setCategory(rs.getString("category"));
        exp.setDescription(rs.getString("description"));
        exp.setAmount(rs.getBigDecimal("amount"));
        exp.setPaymentMode(rs.getString("payment_mode"));
        exp.setVendor(rs.getString("vendor"));
        exp.setAddedBy(rs.getInt("added_by"));
        exp.setCreatedAt(rs.getTimestamp("created_at"));
        exp.setDepartmentName(rs.getString("department_name"));
        exp.setFinancialYear(rs.getString("financial_year"));
        exp.setAddedByName(rs.getString("added_by_name"));
        return exp;
    }
}
