package com.department.tracker.util;

import com.department.tracker.model.User;

/**
 * Global Session Manager for the active application user.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class Session {
    private static int userId = 0;
    private static String username = null;
    private static String role = null; // "ADMIN" or "USER"
    private static String fullName = null;

    private Session() {
    }

    /**
     * Store logged-in user details in session.
     */
    public static void login(User user) {
        if (user != null) {
            userId = user.getUserId();
            username = user.getUsername();
            role = user.getRole();
            fullName = user.getFullName();
        }
    }

    /**
     * Clear the current session on logout.
     */
    public static void logout() {
        userId = 0;
        username = null;
        role = null;
        fullName = null;
    }

    public static boolean isLoggedIn() {
        return userId > 0 && username != null;
    }

    public static boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }

    public static String getFullName() {
        return fullName;
    }
}
