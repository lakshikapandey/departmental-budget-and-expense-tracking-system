package com.department.tracker.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Modern styling and theme definitions for Java Swing UI components.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class UITheme {

    // Color Palette
    public static final Color PRIMARY = new Color(30, 58, 138);       // Deep Navy
    public static final Color PRIMARY_HOVER = new Color(23, 37, 84);
    public static final Color SECONDARY = new Color(59, 130, 246);     // Royal Blue
    public static final Color ACCENT_SUCCESS = new Color(16, 185, 129);// Emerald
    public static final Color ACCENT_WARNING = new Color(245, 158, 11);// Amber
    public static final Color ACCENT_DANGER = new Color(239, 68, 68);  // Crimson
    public static final Color BG_LIGHT = new Color(248, 250, 252);     // Slate 50
    public static final Color BG_CARD = Color.WHITE;
    public static final Color TEXT_DARK = new Color(15, 23, 42);       // Slate 900
    public static final Color TEXT_MUTED = new Color(100, 116, 139);   // Slate 500
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // Slate 200

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_CARD_VAL = new Font("Segoe UI", Font.BOLD, 22);

    /**
     * Formats monetary values with Indian Rupee symbol.
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "₹0.00";
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        String formatted = nf.format(amount);
        // Clean format if necessary to display '₹'
        if (!formatted.contains("₹")) {
            formatted = "₹ " + formatted.replace("Rs.", "").trim();
        }
        return formatted;
    }

    /**
     * Creates a styled modern primary button.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(new LineBorder(PRIMARY_HOVER, 1), new EmptyBorder(8, 16, 8, 16)));
        return btn;
    }

    /**
     * Creates a styled danger button (e.g. for delete or logout).
     */
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(ACCENT_DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(new LineBorder(new Color(185, 28, 28), 1), new EmptyBorder(8, 16, 8, 16)));
        return btn;
    }

    /**
     * Creates a styled secondary/outline button.
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(new LineBorder(BORDER_COLOR, 1), new EmptyBorder(8, 16, 8, 16)));
        return btn;
    }

    /**
     * Creates a styled card panel with subtle border.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        return panel;
    }

    /**
     * Applies standard styling to JTable.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(219, 234, 254)); // light blue
        table.setSelectionForeground(TEXT_DARK);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(TEXT_DARK);
        table.getTableHeader().setReorderingAllowed(false);
    }
}
