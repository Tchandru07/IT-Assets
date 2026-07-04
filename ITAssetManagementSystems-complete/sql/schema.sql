CREATE DATABASE IF NOT EXISTS it_asset_helpdesk;
USE it_asset_helpdesk;

CREATE TABLE departments (
    department_id INT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE employees (
    employee_id INT PRIMARY KEY,
    employee_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(20),
    department_id INT NOT NULL,
    CONSTRAINT fk_employees_departments
        FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

CREATE TABLE assets (
    asset_id INT PRIMARY KEY,
    asset_name VARCHAR(100) NOT NULL,
    asset_type VARCHAR(100) NOT NULL,
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    employee_id INT NULL,
    status ENUM('AVAILABLE', 'ASSIGNED', 'UNDER_MAINTENANCE', 'RETIRED') NOT NULL DEFAULT 'AVAILABLE',
    CONSTRAINT fk_assets_employees
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

CREATE TABLE asset_assignments (
    assignment_id INT PRIMARY KEY AUTO_INCREMENT,
    asset_id INT NOT NULL,
    employee_id INT NOT NULL,
    assigned_date DATE NOT NULL,
    return_date DATE NULL,
    status ENUM('ACTIVE', 'RETURNED') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_asset_assignments_assets
        FOREIGN KEY (asset_id) REFERENCES assets(asset_id),
    CONSTRAINT fk_asset_assignments_employees
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

CREATE TABLE helpdesk_tickets (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    asset_id INT NOT NULL,
    issue_title VARCHAR(150) NOT NULL,
    description TEXT,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    created_date DATE NOT NULL,
    resolved_date DATE NULL,
    CONSTRAINT fk_helpdesk_tickets_employees
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    CONSTRAINT fk_helpdesk_tickets_assets
        FOREIGN KEY (asset_id) REFERENCES assets(asset_id)
);
