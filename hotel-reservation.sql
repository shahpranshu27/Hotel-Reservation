create database hotel_db;

use hotel_db;

CREATE TABLE reservation (
  reservation_id INT AUTO_INCREMENT PRIMARY KEY,
  guest_name VARCHAR(255) NOT NULL,
  room_number INT NOT NULL,
  contact_number VARCHAR(20) NOT NULL,
  reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null
);

desc reservation;

select * from reservation;