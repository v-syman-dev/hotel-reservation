--liquibase formatted sql
--changeset vladislav:1

create sequence address_sequence start with 1 increment by 50;
create sequence booking_sequence start with 1 increment by 50;
create sequence convenience_sequence start with 1 increment by 50;
create sequence hotel_sequence start with 1 increment by 50;
create sequence room_sequence start with 1 increment by 50;

create table addresses (id bigint not null, city varchar(255) not null, country varchar(255) not null, street varchar(255) not null, primary key (id));

create table hotels (rating numeric(38,2), address_id bigint not null unique, id bigint not null, name varchar(255) not null unique, primary key (id));

create table conveniences (id bigint not null, name varchar(255) not null unique, primary key (id));

create table rooms (number integer not null, price_per_night numeric(38,2) not null, hotel_id bigint, id bigint not null, type varchar(255) not null, primary key (id));

create table bookings (check_in_date date not null, check_out_date date not null, total_price numeric(38,2) not null, id bigint not null, room_id bigint not null, guest_name varchar(255) not null, primary key (id));

create table hotel_conveniences (convenience_id bigint not null, hotel_id bigint not null, primary key (convenience_id, hotel_id));

-- Indexes
create index idx_address_country_city on addresses (country, city);
create index idx_booking_room_id on bookings (room_id);
create index idx_hotel_rating on hotels (rating);
create index idx_room_hotel_id on rooms (hotel_id);

-- Foreign Keys
alter table if exists bookings add constraint FK_booking_room foreign key (room_id) references rooms;
alter table if exists hotel_conveniences add constraint FK_hc_convenience foreign key (convenience_id) references conveniences;
alter table if exists hotel_conveniences add constraint FK_hc_hotel foreign key (hotel_id) references hotels;
alter table if exists hotels add constraint FK_hotel_address foreign key (address_id) references addresses;
alter table if exists rooms add constraint FK_room_hotel foreign key (hotel_id) references hotels;
