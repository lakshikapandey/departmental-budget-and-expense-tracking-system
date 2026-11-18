package com.department.tracker.dao;

import com.department.tracker.model.User;
import com.department.tracker.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entity.
 * Uses PreparedStatements for all database operations.
 * Author: Lakshika Pandey (PIET25CR029)
 */
public class UserDAO {

    /**
     * Authenticates a user with username and password.
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, role, full_name, created_at FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    user.setFullName(rs.getString("full_name"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Adds a new user to the system.
     */
    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getFullName());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all users from the database.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role, full_name, created_at FROM users ORDER BY user_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setFullName(rs.getString("full_name"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(user);
            }
        }
        return list;
    }

    /**
     * Retrieves a user by user_id.
     */
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT user_id, username, password, role, full_name, created_at FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                    user.setFullName(rs.getString("full_name"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing user's information.
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, role = ?, full_name = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getRole());
            ps.setString(3, user.getFullName());
            ps.setInt(4, user.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a user by ID.
     */
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Verifies if a user's current password matches what's stored in MySQL.
     */
    public boolean verifyPassword(int userId, String currentPassword) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE user_id = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, currentPassword);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Changes a user's password.
     */
    public boolean changePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Checks if a username already exists, optionally excluding a specific user ID.
     */
    public boolean isUsernameTaken(String username, int excludeUserId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ? AND user_id != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
