-- ============================================================
-- Academic Minor Project: Departmental Budget & Expense Tracking System
-- Student: Lakshika Pandey (Student ID: PIET25CR029)
-- Database Script: database.sql
-- ============================================================

-- 1. Create and select the database
DROP DATABASE IF EXISTS department_expense_tracker;
CREATE DATABASE department_expense_tracker;
USE department_expense_tracker;

-- 2. USERS TABLE
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'ADMIN' or 'USER'
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. BUDGETS TABLE
CREATE TABLE budgets (
    budget_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(100) NOT NULL,
    financial_year VARCHAR(20) NOT NULL,
    allocated_amount DECIMAL(12,2) NOT NULL CHECK (allocated_amount > 0),
    description VARCHAR(255),
    created_date DATE NOT NULL
);

-- 4. EXPENSES TABLE
CREATE TABLE expenses (
    expense_id INT PRIMARY KEY AUTO_INCREMENT,
    budget_id INT NOT NULL,
    expense_date DATE NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    payment_mode VARCHAR(50) NOT NULL,
    vendor VARCHAR(100) NOT NULL,
    added_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_expenses_budget FOREIGN KEY (budget_id) 
        REFERENCES budgets(budget_id) ON DELETE CASCADE,
    CONSTRAINT fk_expenses_user FOREIGN KEY (added_by) 
        REFERENCES users(user_id) ON DELETE RESTRICT
);

-- ============================================================
-- SAMPLE DATA INSERTION
-- ============================================================

-- 5. Insert Initial Users
-- Password is 1234 for local academic demonstration
INSERT INTO users (username, password, role, full_name) VALUES
('admin', '1234', 'ADMIN', 'System Administrator'),
('lakshika', '1234', 'USER', 'Lakshika Pandey');

-- 6. Insert Sample Budgets
INSERT INTO budgets (department_name, financial_year, allocated_amount, description, created_date) VALUES
('Computer Science & Engineering', '2026-27', 500000.00, 'Annual academic and laboratory allocation for CSE', '2026-04-01'),
('Computer Science & Engineering', '2027-28', 650000.00, 'Projected budget for AI lab expansion & cloud credits', '2027-04-01'),
('AI & Machine Learning', '2026-27', 450000.00, 'GPU workstations and specialized research grants', '2026-04-01'),
('Information Technology', '2026-27', 400000.00, 'Networking infrastructure and certification drives', '2026-04-01');

-- 7. Insert Sample Expenses (linked to Budget #1 - CSE 2026-27 and Budget #3 - AI&ML)
INSERT INTO expenses (budget_id, expense_date, category, description, amount, payment_mode, vendor, added_by) VALUES
(1, '2026-04-15', 'Laboratory', 'High-speed switches & CAT6 cabling for Lab 3', 45000.00, 'Bank Transfer', 'NetTech Solutions', 1),
(1, '2026-05-10', 'Software', 'JetBrains & MATLAB Campus licenses', 60000.00, 'Card', 'MathWorks & JetBrains India', 1),
(1, '2026-06-02', 'Stationery', 'Exam answer booklets, printer toner and registers', 12500.00, 'UPI', 'Shree Balaji Enterprises', 2),
(1, '2026-06-25', 'Maintenance', 'Air conditioning servicing and UPS battery replacements', 22500.00, 'Cheque', 'CoolCare Services Ltd', 1),
(1, '2026-07-18', 'Events', 'National Hackathon & Student Tech Fest 2026', 50000.00, 'Bank Transfer', 'Apex Event Organizers', 2),
(1, '2026-08-04', 'Travel', 'Faculty registration & travel for IEEE International Conf', 15000.00, 'UPI', 'Indian Railways & MakeMyTrip', 2),
(1, '2026-08-20', 'Equipment', '2x Epson HD Projectors for Smart Classrooms', 55000.00, 'Bank Transfer', 'Infomax Displays Pvt Ltd', 1),
(3, '2026-05-12', 'Equipment', 'NVIDIA RTX Workstation GPU enhancement', 95000.00, 'Bank Transfer', 'Supermicro India', 1),
(3, '2026-07-14', 'Software', 'AWS Cloud Educate training credits', 35000.00, 'Card', 'Amazon Web Services Inc', 2);
