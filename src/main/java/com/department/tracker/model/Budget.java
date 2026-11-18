package com.department.tracker.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Model class representing a Departmental Budget.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class Budget {
    private int budgetId;
    private String departmentName;
    private String financialYear;
    private BigDecimal allocatedAmount;
    private String description;
    private Date createdDate;

    public Budget() {
    }

    public Budget(int budgetId, String departmentName, String financialYear, BigDecimal allocatedAmount, String description, Date createdDate) {
        this.budgetId = budgetId;
        this.departmentName = departmentName;
        this.financialYear = financialYear;
        this.allocatedAmount = allocatedAmount;
        this.description = description;
        this.createdDate = createdDate;
    }

    public Budget(String departmentName, String financialYear, BigDecimal allocatedAmount, String description, Date createdDate) {
        this.departmentName = departmentName;
        this.financialYear = financialYear;
        this.allocatedAmount = allocatedAmount;
        this.description = description;
        this.createdDate = createdDate;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
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

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "ID #" + budgetId + " - " + departmentName + " (" + financialYear + ") [₹" + allocatedAmount + "]";
    }
}
