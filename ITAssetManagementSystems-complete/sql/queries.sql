USE it_asset_helpdesk;

-- List all assets with assigned employee details.
SELECT
    a.asset_id,
    a.asset_name,
    a.asset_type,
    a.serial_number,
    a.status,
    e.employee_name
FROM assets a
LEFT JOIN employees e ON a.employee_id = e.employee_id
ORDER BY a.asset_id;

-- List active asset assignments.
SELECT
    aa.assignment_id,
    aa.asset_id,
    a.asset_name,
    aa.employee_id,
    e.employee_name,
    aa.assigned_date,
    aa.status
FROM asset_assignments aa
JOIN assets a ON aa.asset_id = a.asset_id
JOIN employees e ON aa.employee_id = e.employee_id
WHERE aa.status = 'ACTIVE'
ORDER BY aa.assigned_date DESC;

-- Assign an asset to an employee.
START TRANSACTION;

INSERT INTO asset_assignments (asset_id, employee_id, assigned_date, return_date, status)
VALUES (202, 102, CURRENT_DATE, NULL, 'ACTIVE');

UPDATE assets
SET employee_id = 102,
    status = 'ASSIGNED'
WHERE asset_id = 202
  AND status = 'AVAILABLE';

COMMIT;

-- Return an assigned asset.
START TRANSACTION;

UPDATE asset_assignments
SET return_date = CURRENT_DATE,
    status = 'RETURNED'
WHERE asset_id = 202
  AND status = 'ACTIVE';

UPDATE assets
SET employee_id = NULL,
    status = 'AVAILABLE'
WHERE asset_id = 202;

COMMIT;

-- List open helpdesk tickets.
SELECT
    ht.ticket_id,
    ht.issue_title,
    ht.priority,
    ht.status,
    ht.created_date,
    e.employee_name,
    a.asset_name
FROM helpdesk_tickets ht
JOIN employees e ON ht.employee_id = e.employee_id
JOIN assets a ON ht.asset_id = a.asset_id
WHERE ht.status IN ('OPEN', 'IN_PROGRESS')
ORDER BY ht.created_date DESC, ht.priority DESC;
