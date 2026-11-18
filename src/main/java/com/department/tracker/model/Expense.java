package com.department.tracker.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model class representing an Expense entry.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class Expense {
    private int expenseId;
    private int budgetId;
    private Date expenseDate;
    private String category;
    private String description;
    private BigDecimal amount;
    private String paymentMode;
    private String vendor;
    private int addedBy;
    private Timestamp createdAt;

    // Joined/Display fields
    private String departmentName;
    private String financialYear;
    private String addedByName;

    public Expense() {
    }

    public Expense(int expenseId, int budgetId, Date expenseDate, String category,
                   String description, BigDecimal amount, String paymentMode,
                   String vendor, int addedBy) {
        this.expenseId = expenseId;
        this.budgetId = budgetId;
        this.expenseDate = expenseDate;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.vendor = vendor;
        this.addedBy = addedBy;
    }

    public Expense(int budgetId, Date expenseDate, String category,
                   String description, BigDecimal amount, String paymentMode,
                   String vendor, int addedBy) {
        this.budgetId = budgetId;
        this.expenseDate = expenseDate;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.vendor = vendor;
        this.addedBy = addedBy;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public int getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(int addedBy) {
        this.addedBy = addedBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public String getAddedByName() {
        return addedByName;
    }

    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }
}
