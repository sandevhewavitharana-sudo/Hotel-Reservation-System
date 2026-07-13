-- Hotel Booking System - reference schema
--
-- You do NOT need to run this by hand: with spring.jpa.hibernate.ddl-auto=update
-- in application.properties, Hibernate creates these tables automatically the
-- first time the app starts. This file exists so you can:
--   1) see the schema at a glance without starting the app,
--   2) load the sample data below for a quick demo,
--   3) run it manually if you'd rather manage the schema yourself
--      (in that case set ddl-auto=validate instead of update).

CREATE DATABASE IF NOT EXISTS hotel_booking_db;
USE hotel_booking_db;

CREATE TABLE IF NOT EXISTS guests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30) NOT NULL,
    loyalty_points INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type VARCHAR(20) NOT NULL,      -- SINGLE, DOUBLE, DELUXE, SUITE
    status VARCHAR(20) NOT NULL,         -- AVAILABLE, OCCUPIED, MAINTENANCE, OUT_OF_SERVICE
    price_per_night DECIMAL(10,2) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guest_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_guests INT NOT NULL,
    status VARCHAR(20) NOT NULL,          -- PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    total_price DECIMAL(10,2) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_booking_guest FOREIGN KEY (guest_id) REFERENCES guests(id),
    CONSTRAINT fk_booking_room FOREIGN KEY (room_id) REFERENCES rooms(id)
);

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(20) NOT NULL,          -- CREDIT_CARD, DEBIT_CARD, CASH, BANK_TRANSFER
    status VARCHAR(20) NOT NULL,          -- PENDING, PAID, REFUNDED, FAILED
    payment_date DATETIME,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- Sample seed data for a quick demo (optional)
INSERT INTO rooms (room_number, room_type, status, price_per_night, description) VALUES
('101', 'SINGLE', 'AVAILABLE', 59.00,  'Cozy single room with a garden view'),
('102', 'SINGLE', 'AVAILABLE', 59.00,  'Cozy single room, street facing'),
('201', 'DOUBLE', 'AVAILABLE', 89.00,  'Double room with a queen bed'),
('202', 'DOUBLE', 'AVAILABLE', 89.00,  'Double room with two twin beds'),
('301', 'DELUXE', 'AVAILABLE', 149.00, 'Deluxe room with a balcony and sea view'),
('401', 'SUITE',  'AVAILABLE', 259.00, 'Executive suite with a separate living area');

INSERT INTO guests (first_name, last_name, email, phone) VALUES
('Amara', 'Silva', 'amara.silva@example.com', '+94-71-234-5678'),
('Dinesh', 'Perera', 'dinesh.perera@example.com', '+94-77-987-6543');
