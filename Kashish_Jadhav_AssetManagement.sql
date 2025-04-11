CREATE DATABASE AssetManagementDB;
use AssetManagementDB;

CREATE TABLE employees (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE assets (
    asset_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,  
    serial_number VARCHAR(100) UNIQUE NOT NULL,
    purchase_date DATE NOT NULL,
    location VARCHAR(100),
    status ENUM('In Use', 'Decommissioned', 'Under Maintenance') NOT NULL,
    owner_id INT,
    FOREIGN KEY (owner_id) REFERENCES employees(employee_id) ON DELETE SET NULL
);

ALTER TABLE assets MODIFY status ENUM('In_Use', 'Decommissioned', 'Under_Maintenance') NOT NULL;
UPDATE assets SET status = 'In_Use' WHERE status = 'In Use';
UPDATE assets SET status = 'Under_Maintenance' WHERE status = 'Under Maintenance';
ALTER TABLE assets MODIFY status VARCHAR(50);

ALTER TABLE assets 
MODIFY status ENUM('Available', 'In_Use', 'Under_Maintenance', 'Decommissioned') NOT NULL;



CREATE TABLE maintenance_records (
    maintenance_id INT PRIMARY KEY AUTO_INCREMENT,
    asset_id INT NOT NULL,
    maintenance_date DATE NOT NULL,
    description TEXT NOT NULL,
    cost DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (asset_id) REFERENCES assets(asset_id) ON DELETE CASCADE
);

CREATE TABLE asset_allocations (
    allocation_id INT PRIMARY KEY AUTO_INCREMENT,
    asset_id INT NOT NULL,
    employee_id INT NOT NULL,
    allocation_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (asset_id) REFERENCES assets(asset_id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);

CREATE TABLE reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    asset_id INT NOT NULL,
    employee_id INT NOT NULL,
    reservation_date DATE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('Pending', 'Approved', 'Canceled') NOT NULL DEFAULT 'Pending',
    FOREIGN KEY (asset_id) REFERENCES assets(asset_id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);


INSERT INTO employees (name, department, email, password) VALUES
    ('Amit Sharma', 'IT', 'amit.sharma@gmail.com', 'password123'),
    ('Priya Verma', 'Finance', 'priya.verma@gmail.com', 'password456'),
    ('Rajesh Kumar', 'HR', 'rajesh.kumar@gmail.com', 'password789'),
    ('Neha Gupta', 'Admin', 'neha.gupta@gmail.com', 'password234'),
    ('Vikram Patel', 'IT', 'vikram.patel@gmail.com', 'password567'),
    ('Sneha Reddy', 'Finance', 'sneha.reddy@gmail.com', 'password890'),
    ('Arjun Nair', 'Operations', 'arjun.nair@gmail.com', 'password345'),
    ('Kiran Joshi', 'HR', 'kiran.joshi@gmail.com', 'password678'),
    ('Pooja Mehta', 'Admin', 'pooja.mehta@gmail.com', 'password901'),
    ('Suresh Menon', 'IT', 'suresh.menon@gmail.com', 'password1234');
    
INSERT INTO employees (name, department, email, password) VALUES
('Tanvi Desai', 'Marketing', 'tanvi.desai@gmail.com', 'tanvi123'),
('Rohan Shetty', 'Sales', 'rohan.shetty@gmail.com', 'rohan456'),
('Anjali Chauhan', 'IT', 'anjali.chauhan@gmail.com', 'anjali789'),
('Devendra Joshi', 'Finance', 'devendra.joshi@gmail.com', 'dev123'),
('Meera Iyer', 'HR', 'meera.iyer@gmail.com', 'meera234'),
('Kabir Malhotra', 'Logistics', 'kabir.malhotra@gmail.com', 'kabir567'),
('Shweta Rao', 'Admin', 'shweta.rao@gmail.com', 'shweta890'),
('Nikhil Bansal', 'IT', 'nikhil.bansal@gmail.com', 'nikhil345'),
('Ayesha Khan', 'Design', 'ayesha.khan@gmail.com', 'ayesha678'),
('Harshit Jain', 'Legal', 'harshit.jain@gmail.com', 'harshit901');



INSERT INTO assets (name, type, serial_number, purchase_date, location, status, owner_id) VALUES
    ('Dell Laptop', 'Laptop', 'DL123456', '2023-05-10', 'Mumbai Office', 'In Use', 1),
    ('HP Printer', 'Equipment', 'HP56789', '2022-07-22', 'Delhi Office', 'In Use', 2),
    ('Toyota Innova', 'Vehicle', 'TN789123', '2021-08-15', 'Garage', 'Under Maintenance', 3),
    ('MacBook Pro', 'Laptop', 'MBP345678', '2024-01-01', 'Bangalore Office', 'In Use', 4),
    ('Canon Camera', 'Equipment', 'CC901234', '2020-11-25', 'Marketing Room', 'Decommissioned', 5),
    ('Samsung Monitor', 'Equipment', 'SM567890', '2023-09-10', 'Chennai Office', 'In Use', 6),
    ('Honda City', 'Vehicle', 'HC456789', '2020-02-20', 'Parking Lot', 'Under Maintenance', 7),
    ('ThinkPad X1', 'Laptop', 'TX123678', '2024-02-28', 'Pune Office', 'In Use', 8),
    ('Epson Projector', 'Equipment', 'EP789012', '2022-06-14', 'Conference Room', 'In Use', 9),
    ('Cisco Router', 'Equipment', 'CR901678', '2021-04-05', 'Server Room', 'Decommissioned', 10);
    
INSERT INTO assets (name, type, serial_number, purchase_date, location, status, owner_id) VALUES
('Cisco Router', 'Equipment', 'CR901678', '2021-04-05', 'Server Room', 'Decommissioned', 10);

INSERT INTO assets (name, type, serial_number, purchase_date, location, status, owner_id)
VALUES 
('Acer Aspire', 'Laptop', 'AA987654', '2019-08-10', 'Hyderabad Office', 'Decommissioned', 11),
('HP EliteDesk', 'Desktop', 'HPD123987', '2020-03-18', 'Noida Office', 'Decommissioned', 12),
('Canon Scanner', 'Equipment', 'CS345600', '2018-11-05', 'Storage Room', 'Decommissioned', 13),
('Asus ZenBook', 'Laptop', 'AZ567890', '2023-06-01', 'Goa Office', 'Available', 14),
('Lenovo Desktop', 'Desktop', 'LD908712', '2023-01-20', 'Nashik Office', 'Available', 15),
('Nikon DSLR', 'Camera', 'ND456789', '2024-03-12', 'Media Room', 'Available', 16),
('Cisco Switch', 'Equipment', 'CSW001122', '2022-05-30', 'Network Hub', 'Under_Maintenance', 17),
('Tata Hexa', 'Vehicle', 'TH123456', '2020-12-11', 'Vehicle Bay', 'Under_Maintenance', 18);

INSERT INTO maintenance_records (asset_id, maintenance_date, description, cost) VALUES
    (3, '2024-03-10', 'Engine oil change', 5000.00),
    (7, '2024-02-20', 'Brake pad replacement', 2500.00),
    (5, '2023-12-12', 'Lens cleaning and servicing', 1200.50),
    (10, '2023-11-05', 'Firmware update', 800.00),
    (1, '2024-02-01', 'Keyboard replacement', 3000.00),
    (2, '2023-10-10', 'Cartridge replacement', 1500.00),
    (4, '2024-01-15', 'Battery replacement', 4500.75),
    (6, '2023-09-25', 'Screen calibration', 2000.00),
    (8, '2024-03-01', 'SSD Upgrade', 7000.00),
    (9, '2023-06-20', 'Lamp replacement', 1800.25);


INSERT INTO asset_allocations (asset_id, employee_id, allocation_date, return_date) VALUES
    (1, 1, '2024-03-01', NULL),
    (2, 2, '2024-03-05', '2024-03-10'),
    (3, 3, '2024-02-15', NULL),
    (4, 4, '2024-01-20', '2024-02-28'),
    (5, 5, '2024-02-10', NULL),
    (6, 6, '2024-03-08', NULL),
    (7, 7, '2024-01-15', '2024-03-01'),
    (8, 8, '2024-02-25', NULL),
    (9, 9, '2024-03-10', NULL),
    (10, 10, '2024-01-05', '2024-02-20');


INSERT INTO reservations (asset_id, employee_id, reservation_date, start_date, end_date, status) VALUES
    (1, 2, '2024-03-05', '2024-03-10', '2024-03-15', 'Pending'),
    (3, 4, '2024-02-25', '2024-03-01', '2024-03-05', 'Approved'),
    (5, 6, '2024-01-10', '2024-01-15', '2024-01-20', 'Canceled'),
    (7, 8, '2024-02-15', '2024-02-20', '2024-02-25', 'Pending'),
    (9, 10, '2024-03-01', '2024-03-05', '2024-03-10', 'Approved'),
    (2, 1, '2024-02-28', '2024-03-01', '2024-03-07', 'Pending'),
    (4, 3, '2024-02-18', '2024-02-20', '2024-02-28', 'Approved'),
    (6, 5, '2024-01-22', '2024-01-25', '2024-01-30', 'Canceled'),
    (8, 7, '2024-02-05', '2024-02-10', '2024-02-18', 'Pending'),
    (10, 9, '2024-03-12', '2024-03-15', '2024-03-20', 'Approved');

SELECT * FROM employees;
SELECT * FROM assets;
SELECT * FROM maintenance_records;
SELECT * FROM asset_allocations;
SELECT * FROM reservations;


