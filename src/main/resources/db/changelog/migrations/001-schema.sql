--liquibase formatted sql

--changeset smirnov:1
create table "users"
(
    id        bigserial primary key,
    login     varchar(255),
    password  varchar(255),
    authority varchar(255) default 'USER',
    enabled   boolean      default true,
    token     varchar(255)
);
--rollback drop table users
create table "files"
(
    id      bigserial primary key,
    user_id bigint references users (id),
    hash    varchar(255),
    file_name    varchar(255),
    file_nameuuid    varchar(255),
    size    bigint
);
--rollback drop table files
create unique index user_id_hash_unique_idx on files(user_id, hash);
-- --rollback
