insert into users (id, username, password, email, first_name, last_name, is_deleted) values
(3, 'admin', 'password123', 'admin@gmail.com', 'First', 'Last', false),
(4, 'user', 'password123', 'user@gmail.com', 'FirstUser', 'LastUser', false);

insert into users_roles (role_id, user_id) values
(2, 3),
(2, 4);