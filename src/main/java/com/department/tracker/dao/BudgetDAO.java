package com.department.tracker.dao;

import com.department.tracker.model.Budget;
import com.department.tracker.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Budget entity.
 * Uses PreparedStatements for all database operations.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class BudgetDAO {

    /**
     * Inserts a new budget record into MySQL.
     */
    public boolean addBudget(Budget budget) throws SQLException {
        String sql = "INSERT INTO budgets (department_name, financial_year, allocated_amount, description, created_date) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, budget.getDepartmentName());
            ps.setString(2, budget.getFinancialYear());
            ps.setBigDecimal(3, budget.getAllocatedAmount());
            ps.setString(4, budget.getDescription());
            ps.setDate(5, budget.getCreatedDate());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all budgets ordered by financial year descending and department name.
     */
    public List<Budget> getAllBudgets() throws SQLException {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT budget_id, department_name, financial_year, allocated_amount, description, created_date "
                   + "FROM budgets ORDER BY financial_year DESC, department_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Budget b = new Budget();
                b.setBudgetId(rs.getInt("budget_id"));
                b.setDepartmentName(rs.getString("department_name"));
                b.setFinancialYear(rs.getString("financial_year"));
                b.setAllocatedAmount(rs.getBigDecimal("allocated_amount"));
                b.setDescription(rs.getString("description"));
                b.setCreatedDate(rs.getDate("created_date"));
                list.add(b);
            }
        }
        return list;
    }

    /**
     * Retrieves a single budget by ID.
     */
    public Budget getBudgetById(int budgetId) throws SQLException {
        String sql = "SELECT budget_id, department_name, financial_year, allocated_amount, description, created_date "
                   + "FROM budgets WHERE budget_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Budget b = new Budget();
                    b.setBudgetId(rs.getInt("budget_id"));
                    b.setDepartmentName(rs.getString("department_name"));
                    b.setFinancialYear(rs.getString("financial_year"));
                    b.setAllocatedAmount(rs.getBigDecimal("allocated_amount"));
                    b.setDescription(rs.getString("description"));
                    b.setCreatedDate(rs.getDate("created_date"));
                    return b;
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing budget record.
     */
    public boolean updateBudget(Budget budget) throws SQLException {
        String sql = "UPDATE budgets SET department_name = ?, financial_year = ?, allocated_amount = ?, "
                   + "description = ?, created_date = ? WHERE budget_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, budget.getDepartmentName());
            ps.setString(2, budget.getFinancialYear());
            ps.setBigDecimal(3, budget.getAllocatedAmount());
            ps.setString(4, budget.getDescription());
            ps.setDate(5, budget.getCreatedDate());
            ps.setInt(6, budget.getBudgetId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a budget record by ID.
     */
    public boolean deleteBudget(int budgetId) throws SQLException {
        String sql = "DELETE FROM budgets WHERE budget_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Searches budgets matching department name, financial year, or description.
     */
    public List<Budget> searchBudgets(String keyword) throws SQLException {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT budget_id, department_name, financial_year, allocated_amount, description, created_date "
                   + "FROM budgets WHERE department_name LIKE ? OR financial_year LIKE ? OR description LIKE ? "
                   + "ORDER BY financial_year DESC, department_name ASC";
        String pattern = "%" + keyword + "%";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Budget b = new Budget();
                    b.setBudgetId(rs.getInt("budget_id"));
                    b.setDepartmentName(rs.getString("department_name"));
                    b.setFinancialYear(rs.getString("financial_year"));
                    b.setAllocatedAmount(rs.getBigDecimal("allocated_amount"));
                    b.setDescription(rs.getString("description"));
                    b.setCreatedDate(rs.getDate("created_date"));
                    list.add(b);
                }
            }
        }
        return list;
    }

    /**
     * Calculates total allocated budget across all departments.
     */
    public BigDecimal getTotalBudget() throws SQLException {
        String sql = "SELECT COALESCE(SUM(allocated_amount), 0) AS total_budget FROM budgets";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal("total_budget");
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calculates total spent amount for a specific budget.
     */
    public BigDecimal getBudgetSpentAmount(int budgetId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_spent FROM expenses WHERE budget_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_spent");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Gets available/remaining budget for a specific budget ID.
     */
    public BigDecimal getBudgetRemainingAmount(int budgetId) throws SQLException {
        Budget b = getBudgetById(budgetId);
        if (b == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal spent = getBudgetSpentAmount(budgetId);
        return b.getAllocatedAmount().subtract(spent);
    }
}
