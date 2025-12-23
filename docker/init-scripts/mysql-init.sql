-- MySQL Initialization Script for IDAP
-- Creates database for Financial Service

-- Create database for Financial Service
CREATE DATABASE IF NOT EXISTS idap_financial CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant all privileges to the admin user
GRANT ALL PRIVILEGES ON idap_financial.* TO 'idap_admin'@'%';

FLUSH PRIVILEGES;
