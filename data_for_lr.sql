
-- скрипт для создания табличек и загрузки данных в бд

CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    login VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    role VARCHAR NOT NULL,
    first_name VARCHAR,
    last_name VARCHAR,
    passport_number INTEGER
);

CREATE TABLE airports (
    code VARCHAR PRIMARY KEY,
    name VARCHAR NOT NULL,
    city VARCHAR NOT NULL,
    is_available_flight BOOLEAN NOT NULL
);

CREATE TABLE airlines (
    iata_code VARCHAR PRIMARY KEY,
    name VARCHAR NOT NULL,
    country VARCHAR NOT NULL,
    founded_year INTEGER NOT NULL,
    info VARCHAR NOT NULL,
    website VARCHAR,
    free_baggage_kg INTEGER NOT NULL,
    extra_kg_price DOUBLE PRECISION NOT NULL
);
CREATE TABLE planes (
    registration_number VARCHAR PRIMARY KEY,
    model VARCHAR NOT NULL,
    type VARCHAR NOT NULL,
    capacity INTEGER NOT NULL,
    max_weight_kg INTEGER NOT NULL,
    max_distance_km INTEGER NOT NULL,
    production_year INTEGER NOT NULL
);
CREATE TABLE flights (
    flight_id BIGINT PRIMARY KEY,
    departure_airport_code VARCHAR NOT NULL,
    arrival_airport_code VARCHAR NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    airline_code VARCHAR NOT NULL,
    plane_registration VARCHAR NOT NULL,
    available_seats INTEGER NOT NULL,
    CONSTRAINT fk_flight_airline FOREIGN KEY (airline_code) REFERENCES airlines(iata_code),
    CONSTRAINT fk_flight_departure FOREIGN KEY (departure_airport_code) REFERENCES airports(code),
    CONSTRAINT fk_flight_arrival FOREIGN KEY (arrival_airport_code) REFERENCES airports(code),
    CONSTRAINT fk_flight_plane FOREIGN KEY (plane_registration) REFERENCES planes(registration_number)
);
CREATE TABLE bookings (
    booking_id BIGINT PRIMARY KEY,
    passenger_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,
    booking_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_paid BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_booking_user FOREIGN KEY (passenger_id) REFERENCES users(id),
    CONSTRAINT fk_booking_flight FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
);



INSERT INTO users (id, login, password, role) VALUES
(11, 'admin1', 'pass', 'ADMIN'),
(21, 'manager1', 'pass', 'MANAGER');

INSERT INTO users (id, login, password, role, first_name, last_name, passport_number) VALUES
(31, 'name1', 'pass', 'CUSTOMER', 'name1', 'surname1', 1111),
(32, 'name2', 'pass', 'CUSTOMER', 'name2', 'surname2', 2222),
(33, 'name3', 'pass', 'CUSTOMER', 'name3', 'surname3', 3333),
(34, 'name4', 'pass', 'CUSTOMER', 'name4', 'surname4', 4444);
INSERT INTO airports (code, name, city, is_available_flight) VALUES
('GOJ', 'Стригино', 'Нижний Новгород', true),
('SVO', 'Шереметьево', 'Москва', true),
('LED', 'Пулково', 'Санкт-Петербург', true);
INSERT INTO airlines (iata_code, name, country, founded_year, info) VALUES
('SU', 'Аэрофлот', 'Россия', 1923, 'российская государственно-частная авиакомпания. Полное наименование — ПАО «Аэрофлот — российские авиалинии»'),
('S7', 'S7 Airlines', 'Россия', 1957, 'российская авиакомпания, выполняющая внутренние и международные пассажирские авиаперевозки'),
('UT', 'Utair', 'Россия', 1967, 'российская авиакомпания. Полное наименование — ПАО «Авиакомпания ЮТэйр»'),
('TA', 'TestAir', 'TestCountry', 2020, 'Test airline');


INSERT INTO planes (registration_number, model, type, capacity, max_weight_kg, max_distance_km, production_year) VALUES
('TP-001', 'TestPlane', 'тестовый', 2, 3000, 1200, 2020),
('SSJ-100-001', 'SSJ-100', 'региональный', 90, 18000, 4500, 2019),
('B737-001', 'Boeing 737-800', 'магистральный', 180, 25000, 5500, 2015);

INSERT INTO flights (flight_id, departure_airport_code, arrival_airport_code, departure_time, arrival_time, airline_code, plane_registration, available_seats) VALUES
(101, 'GOJ', 'SVO', '2025-11-01 09:00:00', '2025-11-01 11:00:00', 'UT', 'TP-001', 2),
(201, 'SVO', 'LED', '2025-11-01 16:00:00', '2025-11-01 17:30:00', 'SU', 'SSJ-100-001', 90),
(202, 'SVO', 'LED', '2025-11-02 10:00:00', '2025-11-02 12:00:00', 'SU', 'B737-001', 180);
