DROP DATABASE IF EXISTS buscompany;
CREATE DATABASE buscompany;
USE buscompany;

CREATE TABLE buses(
id INT(11) NOT NULL AUTO_INCREMENT, 
busname VARCHAR(25),
placecount INT(11), 
PRIMARY KEY (id)
)ENGINE=INNODB DEFAULT CHARSET=utf8;

INSERT INTO buses (busname, placecount) VALUES ("ANKAI", 40);
INSERT INTO buses (busname, placecount) VALUES ("FOTON", 30);
INSERT INTO buses (busname, placecount) VALUES ("BAW", 20);

CREATE TABLE users(
id INT(11) NOT NULL AUTO_INCREMENT,
username VARCHAR(50) NOT NULL,
`password` VARCHAR(50) NOT NULL,
firstname VARCHAR(50) NOT NULL,
lastname VARCHAR(50) NOT NULL,
patronymic VARCHAR(50),
`role` VARCHAR(50) NULL,
enabled BOOLEAN DEFAULT TRUE,
PRIMARY KEY (id),
UNIQUE KEY (username)
)ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE admins(
id INT(11) NOT NULL,
`position` VARCHAR(50) NOT NULL,
PRIMARY KEY(id),
FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE clients(
id INT(11) NOT NULL,
email VARCHAR(50) NULL,
phone VARCHAR(50) NULL,
PRIMARY KEY (id),
FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8;


CREATE TABLE `schedules` (
id INT(11) AUTO_INCREMENT NOT NULL,
from_date DATETIME(6) NULL,
to_date DATETIME(6) NULL,
PERIOD VARCHAR(255) NULL,
PRIMARY KEY (id)
)ENGINE=INNODB DEFAULT CHARSET=utf8;  

CREATE TABLE trips (
  id INT(11) AUTO_INCREMENT NOT NULL,
  bus_id INT(11) NULL,
  VERSION INT(11),
  from_station VARCHAR(50) NULL,
  to_station VARCHAR(50) NULL,
  `start` VARCHAR(10) NULL,
  duration VARCHAR(10) NULL,
  price VARCHAR(50) NULL,
  approved BOOLEAN NULL,
  schedule_id INT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (bus_id) REFERENCES buses (id),
  FOREIGN KEY (schedule_id) REFERENCES `schedules` (id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8;  

CREATE TABLE date_trip (
id INTEGER NOT NULL AUTO_INCREMENT,
VERSION INT(11),
`date` DATETIME(6),
free_places INT(11) NOT NULL,
PRIMARY KEY (id)
)ENGINE=INNODB DEFAULT CHARSET=utf8;
   
CREATE TABLE trips_date_trips (
trip_id INT(11) NOT NULL,
date_trips_id INT(11) NOT NULL,
FOREIGN KEY (date_trips_id) REFERENCES date_trip (id),
FOREIGN KEY (trip_id) REFERENCES trips (id)
)ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE orders (
id INT(11) NOT NULL AUTO_INCREMENT,
DATE DATETIME(6),
total_price VARCHAR(50),
client_id INT(11), 
trip_id INT(11),
PRIMARY KEY (id),
FOREIGN KEY (client_id) REFERENCES clients (id) ON DELETE CASCADE,
FOREIGN KEY (trip_id) REFERENCES trips (id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8; 

CREATE TABLE passengers(
id INT(11) NOT NULL AUTO_INCREMENT,
first_name VARCHAR(50),
last_name VARCHAR(50),
passport VARCHAR(50),
PRIMARY KEY (id)
)ENGINE=INNODB DEFAULT CHARSET=utf8; 

CREATE TABLE orders_passengers(
order_id INT(11) NOT NULL,
passengers_id INT(11) NOT NULL,
FOREIGN KEY (passengers_id) REFERENCES passengers (id) ON DELETE CASCADE,
FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8; 

CREATE TABLE places (
id INT(11) NOT NULL AUTO_INCREMENT,
place_number INT(11) NOT NULL,
passenger_id INT(11),
PRIMARY KEY (id),
FOREIGN KEY (passenger_id) REFERENCES passengers (id) ON DELETE SET NULL
)ENGINE=INNODB DEFAULT CHARSET=utf8; 

CREATE TABLE date_trip_places (
date_trip_id INT(11) NOT NULL,
places_id INT(11) NOT NULL,
FOREIGN KEY (places_id) REFERENCES places (id),
FOREIGN KEY (date_trip_id) REFERENCES date_trip (id)
)ENGINE=INNODB DEFAULT CHARSET=utf8; 
    
    
