--liquibase formatted sql

--changeset smirnov:1
insert into "users" ("login", "password")
values ('ya@yandex.ru', '$2y$10$jz8M.GScq8cf3QrVgl7t/ue2tLsXBUH8G11UXhqeUWgBrv47/bHjm'),
       ('my@yandex.ru', '$2y$10$FblLqKI8t.lFrjvIo.VN/uMLtumPbjDuY6.cs7KWpPKmEJpGEQ5ui')
--rollback ;