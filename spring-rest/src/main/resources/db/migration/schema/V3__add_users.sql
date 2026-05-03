create table users (
    id bigserial,
    login varchar(255),
    password varchar(255),
    role varchar(255),
    primary key (id)
);