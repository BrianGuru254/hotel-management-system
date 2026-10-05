ALTER TABLE housekeeping_tasks
    ADD COLUMN assigned_employee_id UUID REFERENCES employees(id) ON DELETE SET NULL;
ALTER TABLE maintenance_requests
    ADD COLUMN assigned_employee_id UUID REFERENCES employees(id) ON DELETE SET NULL;

UPDATE housekeeping_tasks task
SET assigned_employee_id = e.id
FROM employees e, properties p
WHERE task.assigned_user_id = e.user_id
  AND p.id = task.property_id
  AND e.branch_id = p.branch_id
  AND (e.department_id IS NULL OR EXISTS (
      SELECT 1 FROM departments d WHERE d.id = e.department_id AND d.property_id = task.property_id));

UPDATE maintenance_requests mr
SET assigned_employee_id = e.id
FROM employees e, properties p
WHERE mr.assigned_user_id = e.user_id
  AND p.id = mr.property_id
  AND e.branch_id = p.branch_id
  AND (e.department_id IS NULL OR EXISTS (
      SELECT 1 FROM departments d WHERE d.id = e.department_id AND d.property_id = mr.property_id));

CREATE INDEX idx_housekeeping_assigned_employee ON housekeeping_tasks(assigned_employee_id);
CREATE INDEX idx_maintenance_assigned_employee ON maintenance_requests(assigned_employee_id);
