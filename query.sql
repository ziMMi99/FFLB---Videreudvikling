--The master database always exists, so we hop to that one before editing FFLB_DB.
USE master;
GO

--Check if FFLB_DB exists, and if it does, delete it.
IF DB_ID('FFLB_DB') IS NOT NULL
    DROP DATABASE FFLB_DB;
GO

--Create the database
CREATE DATABASE FFLB_DB;
GO

--Use the newly created database
USE FFLB_DB;

-- TABLES

CREATE TABLE customer(
 customer_id INT IDENTITY NOT NULL,
 first_name NVARCHAR(50) NOT NULL,
 last_name NVARCHAR(50) NOT NULL,
 home_address NVARCHAR(50) NOT NULL,
 post_code INT NOT NULL,
 cpr NVARCHAR(10) NOT NULL,
 email NVARCHAR(50) NOT NULL,
 phone_number INT NOT NULL,
 credit_score VARCHAR(1) NOT NULL --A, B, C or D

     PRIMARY KEY(customer_id) --Define primary key
)

CREATE TABLE car(
car_id INT IDENTITY NOT NULL,
model_name NVARCHAR(50) NOT NULL,
price FLOAT(53) NOT NULL,
has_payment_plan BIT NOT NULL

    PRIMARY KEY(car_id) --Define primary key
)

CREATE TABLE salesman(
salesman_id INT IDENTITY NOT NULL,
first_name NVARCHAR(50) NOT NULL,
last_name NVARCHAR(50) NOT NULL,
email NVARCHAR(50) NOT NULL,
phone_number INT NOT NULL,
loan_limit float(53) NOT NULL,
is_active BIT NOT NULL

     PRIMARY KEY(salesman_id) --Define primary key
)

CREATE TABLE payment_plan(
payment_plan_id INT IDENTITY NOT NULL,
customer_id INT NOT NULL
    FOREIGN KEY REFERENCES customer(customer_id)
        ON DELETE CASCADE,
car_id INT NOT NULL
    FOREIGN KEY REFERENCES car(car_id)
        ON DELETE CASCADE,
salesman_id INT NOT NULL
    FOREIGN KEY REFERENCES salesman(salesman_id)
        ON DELETE CASCADE,
plan_length INT NOT NULL,
down_payment FLOAT(53) NOT NULL,
monthly_rent FLOAT(53) NOT NULL,
start_date DATE NOT NULL,
car_fixed_price FLOAT(53) NOT NULL,

     PRIMARY KEY(payment_plan_id) --Define primary key
)

--
--  TEST ROWS
--  A bunch of dummy values to make testing the user interface easier by having pre-existing data to work with.
--

-- Insert rows into customer table
INSERT INTO customer (first_name, last_name, home_address, post_code, cpr, email, phone_number, credit_score) VALUES
('John', 'Doe', '123 Elm St', 12345, '1212567890', 'john.doe@example.com', 51234567, 'A'),
('Jane', 'Smith', '456 Oak St', 67890, '2301678901', 'jane.smith@example.com', 59876543, 'B'),
('Alice', 'Johnson', '789 Pine St', 54321, '1709789012', 'alice.johnson@example.com', 54567890, 'C'),
('Bob', 'Brown', '321 Maple St', 98765, '1908890123', 'bob.brown@example.com', 53456789, 'D'),
('Carol', 'Taylor', '654 Birch St', 13579, '2106901234', 'carol.taylor@example.com', 52345678, 'A');

-- Insert rows into car table
INSERT INTO car (model_name, price, has_payment_plan) VALUES
('Toyota Camry', 24000, 1),
('Honda Accord', 26000, 1),
('Ford Mustang', 35000, 0),
('Chevrolet Malibu', 22000, 0),
('Nissan Altima', 23000, 0);

-- Insert rows into salesman table
INSERT INTO salesman (first_name, last_name, email, phone_number, loan_limit, is_active) VALUES
('Mark', 'Davis', 'mark.davis@example.com', 51112222, 500000, 1),
('Nancy', 'Wilson', 'nancy.wilson@example.com', 53334444, 1000000, 1),
('Paul', 'Miller', 'paul.miller@example.com', 55556666, 1200000, 1),
('Laura', 'Clark', 'laura.clark@example.com', 57778888, 1100000, 1),
('Tom', 'Lewis', 'tom.lewis@example.com', 59990000, 900000, 0);

-- Insert rows into payment_plan table
INSERT INTO payment_plan (customer_id, car_id, salesman_id, plan_length, down_payment, monthly_rent, start_date, car_fixed_price) VALUES
(1, 1, 1, 36, 5000, 5, '2024-01-01', 24000),
(2, 2, 2, 48, 7000, 6, '2024-02-01', 26000),
(3, 3, 3, 24, 3000, 4, '2024-07-12', 35000);


-- STORED PROCEDURES

-- CRUD-procedures for every table to easily call from the Java backend.

-------
--CAR--
-------

--Create
IF OBJECT_ID('car_addToDB', 'P') IS NOT NULL
    DROP PROCEDURE car_addToDB;
GO

CREATE PROCEDURE car_addToDB
    @name NVARCHAR(50),
    @price FLOAT(53),
    @has_payment_plan TINYINT
AS
INSERT INTO car(model_name, price, has_payment_plan) values (@name, @price, @has_payment_plan)
GO

--Get
IF OBJECT_ID('car_getFromDB_ByID', 'P') IS NOT NULL
    DROP PROCEDURE car_getFromDB_ByID;
GO

CREATE PROCEDURE car_getFromDB_ByID
    @car_id INT
AS
SELECT * FROM car WHERE car_id = @car_id;
GO

--Get All
IF OBJECT_ID('car_getAll') IS NOT NULL
DROP PROCEDURE car_getAll;
GO

CREATE PROCEDURE car_getAll
    AS
SELECT * FROM car;
GO

--Update
IF OBJECT_ID('car_updateCar_ByID', 'P') IS NOT NULL
    DROP PROCEDURE car_updateCar_ByID;
GO

CREATE PROCEDURE car_updateCar_ByID
    @car_id INT,
    @name NVARCHAR(50),
    @price FLOAT(53),
    @has_payment_plan BIT
AS
UPDATE car SET model_name = @name, price = @price, has_payment_plan = @has_payment_plan
WHERE car_id = @car_id
GO

--Delete
IF OBJECT_ID('car_deleteCar_ByID', 'P') IS NOT NULL
    DROP PROCEDURE car_deleteCar_ByID;
GO

CREATE PROCEDURE car_deleteCar_ByID
    @car_id INT
AS
DELETE FROM car WHERE car_id = @car_id
GO

------------
--CUSTOMER--
------------

--Create
IF OBJECT_ID('customer_addToDB', 'P') IS NOT NULL
    DROP PROCEDURE customer_addToDB;
GO

CREATE PROCEDURE customer_addToDB
    @first_name NVARCHAR(50),
    @last_name NVARCHAR(50),
    @address NVARCHAR(50),
    @post_code INT,
    @cpr NVARCHAR(10),
    @email NVARCHAR(50),
    @phone_number INT,
    @credit_score NVARCHAR(1)
AS
INSERT INTO
customer(first_name,  last_name,  home_address, post_code,  cpr,  email,  phone_number,  credit_score)
values  (@first_name, @last_name, @address,     @post_code, @cpr, @email, @phone_number, @credit_score)
GO

--Get
IF OBJECT_ID('customer_getFromDB_ByID', 'P') IS NOT NULL
    DROP PROCEDURE customer_getFromDB_ByID;
GO

CREATE PROCEDURE customer_getFromDB_ByID
    @customer_id INT
AS
SELECT * FROM customer WHERE customer_id = @customer_id;
GO

--Get All
IF OBJECT_ID('customer_getAll') IS NOT NULL
DROP PROCEDURE customer_getAll;
GO

CREATE PROCEDURE customer_getAll
AS
SELECT * FROM customer;
GO

--Update
IF OBJECT_ID('customer_updateCustomer_ByID', 'P') IS NOT NULL
    DROP PROCEDURE customer_updateCustomer_ByID;
GO

CREATE PROCEDURE customer_updateCustomer_ByID
    @customer_id INT,
    @first_name NVARCHAR(50),
    @last_name NVARCHAR(50),
    @address NVARCHAR(50),
    @post_code INT,
    @cpr NVARCHAR(10),
    @email NVARCHAR(50),
    @phone_number INT,
    @credit_score NVARCHAR(1)
AS
UPDATE customer SET
    first_name = @first_name,
    last_name = @last_name,
    home_address = @address,
    post_code = @post_code,
    cpr = @cpr,
    email = @email,
    phone_number = @phone_number,
    credit_score = @credit_score
WHERE customer_id = @customer_id
GO

--Delete
IF OBJECT_ID('customer_deleteCustomer_ByID', 'P') IS NOT NULL
    DROP PROCEDURE customer_deleteCustomer_ByID;
GO

CREATE PROCEDURE customer_deleteCustomer_ByID @id INT
AS
DELETE FROM customer WHERE customer_id = @id
GO

------------
--SALESMAN--
------------

--Create
IF OBJECT_ID('salesman_addToDB', 'P') IS NOT NULL
    DROP PROCEDURE salesman_addToDB;
GO

CREATE PROCEDURE salesman_addToDB
    @first_name NVARCHAR(100),
    @last_name NVARCHAR(100),
    @email NVARCHAR(100),
    @phone_number INT,
    @loan_limit FLOAT(53),
    @is_active BIT
AS
INSERT INTO
salesman(first_name,  last_name,  email,  phone_number,  loan_limit,  is_active)
VALUES  (@first_name, @last_name, @email, @phone_number, @loan_limit, @is_active)
GO

--Get
IF OBJECT_ID('salesman_getFromDB_ByID', 'P') IS NOT NULL
    DROP PROCEDURE salesman_getFromDB_ByID;
GO

CREATE PROCEDURE salesman_getFromDB_ByID
    @salesman_id int
AS
SELECT * FROM salesman WHERE salesman_id = @salesman_id
GO

--Get All
IF OBJECT_ID('salesman_getAll') IS NOT NULL
DROP PROCEDURE salesman_getAll;
GO

CREATE PROCEDURE salesman_getAll
AS
SELECT * FROM salesman;
GO

--Update
IF OBJECT_ID('salesman_updateSalesman_ByID', 'P') IS NOT NULL
    DROP PROCEDURE salesman_updateSalesman_ByID;
GO

CREATE PROCEDURE salesman_updateSalesman_ByID
    @salesman_id int,
    @first_name NVARCHAR(100),
    @last_name NVARCHAR(100),
    @email NVARCHAR(100),
    @phone_number int,
    @loan_limit FLOAT(53),
    @is_active BIT
AS
UPDATE salesman SET
    first_name = @first_name,
    last_name = @last_name,
    email = @email,
    phone_number = @phone_number,
    loan_limit = @loan_limit,
    is_active = @is_active
WHERE salesman_id = @salesman_id
GO

--Delete
IF OBJECT_ID('salesman_deleteSalesman_ByID', 'P') IS NOT NULL
    DROP PROCEDURE salesman_deleteSalesman_ByID;
GO

CREATE PROCEDURE salesman_deleteSalesman_ByID
    @salesman_id int
AS
DELETE FROM salesman WHERE salesman_id = @salesman_id
GO

----------------
--PAYMENT PLAN--
----------------

--Create
IF OBJECT_ID('paymentplan_addToDB', 'P') IS NOT NULL
    DROP PROCEDURE paymentplan_addToDB;
GO

CREATE PROCEDURE paymentplan_addToDB
    @customer_id INT,
    @car_id INT,
    @salesman_id INT,
    @plan_length INT,
    @down_payment FLOAT(53),
    @monthly_rent FLOAT(53),
    @start_date DATE,
    @car_fixed_price FLOAT(53)
AS
INSERT INTO
payment_plan(customer_id,  car_id,  salesman_id,  plan_length,  down_payment,  monthly_rent,  start_date, car_fixed_price)
VALUES      (@customer_id, @car_id, @salesman_id, @plan_length, @down_payment, @monthly_rent, @start_date, @car_fixed_price)
GO

--Get
IF OBJECT_ID('paymentplan_getFromDB_ByID', 'P') IS NOT NULL
    DROP PROCEDURE paymentplan_getFromDB_ByID;
GO

CREATE PROCEDURE paymentplan_getFromDB_ByID
   @paymentplan_id int
AS
SELECT * FROM payment_plan WHERE payment_plan_id = @paymentplan_id
GO

--Get All
IF OBJECT_ID('paymentplan_getAll') IS NOT NULL
DROP PROCEDURE paymentplan_getAll;
GO

CREATE PROCEDURE paymentplan_getAll
    AS
SELECT * FROM payment_plan;
GO

--Update
IF OBJECT_ID('paymentplan_updatePaymentPlan_ByID', 'P') IS NOT NULL
    DROP PROCEDURE paymentplan_updatePaymentPlan_ByID;
GO

CREATE PROCEDURE paymentplan_updatePaymentPlan_ByID
    @paymentplan_id INT,
    @customer_id INT,
    @car_id INT,
    @salesman_id INT,
    @plan_length INT,
    @down_payment FLOAT(53),
    @monthly_rent FLOAT(53),
    @start_date DATE,
    @car_fixed_price FLOAT(53)
AS
UPDATE payment_plan SET
    customer_id = @customer_id,
    car_id = @car_id,
    salesman_id = @salesman_id,
    plan_length = @plan_length,
    down_payment = @down_payment,
    monthly_rent = @monthly_rent,
    start_date = @start_date,
    car_fixed_price = @car_fixed_price
WHERE payment_plan_id = @paymentplan_id
GO

--Delete
IF OBJECT_ID('paymentplan_deletePaymentPlan_ByID', 'P') IS NOT NULL
    DROP PROCEDURE paymentplan_deletePaymentPlan_ByID;
GO

CREATE PROCEDURE paymentplan_deletePaymentPlan_ByID
    @paymentplan_id int
AS
DELETE FROM payment_plan WHERE payment_plan_id = @paymentplan_id
GO