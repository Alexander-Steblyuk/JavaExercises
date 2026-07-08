insert into roles(code) values
('SIMPLE_USER'), ('ADMIN');

insert into users_roles(user_id, role_id) values
(1, 1),
(2, 2);