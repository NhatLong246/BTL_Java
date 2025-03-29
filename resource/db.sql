DROP DATABASE IF EXISTS userdb;
CREATE DATABASE IF NOT EXISTS userdb;

USE userdb;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'doctor', 'patient') DEFAULT 'patient', -- Thêm 'admin' vào ENUM
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, email, password, role) VALUES 
('admin1', 'admin1@example.com', SHA2('admin123', 256), 'admin'), -- Thêm tài khoản admin
('doctor1', 'doctor1@example.com', SHA2('doctor123', 256), 'doctor'),
('patient1', 'patient1@example.com', SHA2('patient123', 256), 'patient'),
('patient2', 'patient2@example.com', SHA2('patient456', 256), 'patient');

SELECT * FROM users;