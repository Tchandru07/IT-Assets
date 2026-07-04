USE it_asset_helpdesk;

INSERT INTO departments (department_id, department_name) VALUES
(10, 'IT'),
(20, 'HR'),
(30, 'Finance');

INSERT INTO employees (employee_id, employee_name, email, phone, department_id) VALUES
(101, 'Chandru', 'chandru@example.com', '9876543210', 10),
(102, 'Priya', 'priya@example.com', '9876501234', 20);

INSERT INTO assets (asset_id, asset_name, asset_type, serial_number, employee_id, status) VALUES
(201, 'Laptop', 'Electronics', 'SN-LAP-201', NULL, 'AVAILABLE'),
(202, 'Monitor', 'Electronics', 'SN-MON-202', NULL, 'AVAILABLE'),
(203, 'Printer', 'Office Equipment', 'SN-PRN-203', NULL, 'AVAILABLE');

INSERT INTO asset_assignments (asset_id, employee_id, assigned_date, return_date, status) VALUES
(201, 101, CURRENT_DATE, NULL, 'ACTIVE');

UPDATE assets
SET employee_id = 101,
    status = 'ASSIGNED'
WHERE asset_id = 201;

INSERT INTO helpdesk_tickets (
    employee_id,
    asset_id,
    issue_title,
    description,
    priority,
    status,
    created_date,
    resolved_date
) VALUES
(101, 201, 'Laptop not booting', 'The assigned laptop does not start.', 'HIGH', 'OPEN', CURRENT_DATE, NULL);
