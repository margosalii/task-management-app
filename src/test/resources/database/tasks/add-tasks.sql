insert into projects (id, name, description, start_date, end_date, status, user_id, is_deleted)
values (1, 'First project', 'Description', '2003-12-01', '2013-12-01', 'INITIATED', 3, false);

insert into tasks (id, description, priority, status, due_date, project_id, assignee_id, is_deleted)
values (1, 'First description', 'HIGH', 'COMPLETED', '2023-11-11', 1, 3, false);

insert into tasks (id, description, priority, status, due_date, project_id, assignee_id, is_deleted)
values (2, 'Second description', 'HIGH', 'COMPLETED', '2013-11-11', 1, 3, false);