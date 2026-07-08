create table users (
    id bigserial,
    login varchar(255),
    password varchar(255),
    role varchar(255),
    primary key (id)
);

create table roles (
    id bigserial,
    code varchar(255),
    primary key (id)
);

create table users_roles (
    user_id bigint references users(id) on delete cascade,
    role_id bigint references roles(id) on delete cascade,
    primary key (user_id, role_id)
);