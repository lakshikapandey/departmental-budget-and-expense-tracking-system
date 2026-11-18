package com.department.tracker.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Reusable validation utility for the Departmental Budget & Expense Tracker.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class ValidationUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ValidationUtil() {
    }

    /**
     * Checks if a string is null or empty after trimming.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Checks if a string represents a valid number (integer or decimal).
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        try {
            new BigDecimal(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if a string represents a strictly positive decimal (> 0).
     */
    public static boolean isPositiveDecimal(String str) {
        if (!isNumeric(str)) {
            return false;
        }
        try {
            BigDecimal val = new BigDecimal(str.trim());
            return val.compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates a date string in YYYY-MM-DD format.
     */
    public static boolean isValidDate(String dateStr) {
        if (isEmpty(dateStr)) {
            return false;
        }
        try {
            LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Parses a date string to java.sql.Date safely.
     */
    public static java.sql.Date parseSqlDate(String dateStr) {
        if (!isValidDate(dateStr)) {
            return null;
        }
        return java.sql.Date.valueOf(LocalDate.parse(dateStr.trim(), DATE_FORMATTER));
    }

    /**
     * Validates that password has at least 4 characters.
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.trim().length() >= 4;
    }

    /**
     * Validates financial year format, e.g. "2026-27" or "2026-2027"
     */
    public static boolean isValidFinancialYear(String fy) {
        if (isEmpty(fy)) {
            return false;
        }
        return fy.trim().matches("^\\d{4}-\\d{2,4}$");
    }
}
