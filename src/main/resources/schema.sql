drop table if exists customers cascade;
drop table if exists products cascade;
drop table if exists customer_orders cascade;
drop table if exists stock cascade;

create table if not exists customers
(
    id       serial primary key,
    first    text         not null,
    last     text         not null,
    username varchar(255) not null
);

create table if not exists products
(
    id  serial primary key,
    sku varchar(255) not null
);

create table if not exists customer_orders
(
    id          serial primary key,
    customer_fk bigint not null references customers (id),
    product_fk  bigint not null references products (id)
);

create table if not exists stock
(
    id                serial primary key,
    product_fk        bigint not null references products (id) unique,
    quantity_in_stock bigint not null default 100 check ( quantity_in_stock >= 0 )
);