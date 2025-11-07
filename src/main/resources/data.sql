INSERT INTO leave_balances (id, sick_leave, casual_leave, earned_leave, year)
VALUES (1, 10, 12, 18, 2024);

INSERT INTO leave_balances (id, sick_leave, casual_leave, earned_leave, year)
VALUES (2, 10, 12, 18, 2024);

INSERT INTO employees (id, name, email, department, manager_id, leave_balance_id)
VALUES (1, 'John Doe', 'john@hcltech.com', 'IT', 2, 1);

INSERT INTO employees (id, name, email, department, leave_balance_id)
VALUES (2, 'Jane Manager', 'jane@hcltech.com', 'IT', 2);