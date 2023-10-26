create table if not exists customer
(
    id       serial primary key,
    first    text         not null,
    last     text         not null,
    username varchar(255) not null
);