-- PostgreSQL Initialization Script for IDAP
-- Creates databases for Weather Service and User Service

-- Create database for Weather Service
CREATE DATABASE idap_weather;

-- Create database for User Service
CREATE DATABASE idap_users;

-- Grant all privileges to the admin user
GRANT ALL PRIVILEGES ON DATABASE idap_weather TO idap_admin;
GRANT ALL PRIVILEGES ON DATABASE idap_users TO idap_admin;

-- Connect to idap_weather and set up schema permissions
\c idap_weather;
GRANT ALL ON SCHEMA public TO idap_admin;

-- Connect to idap_users and set up schema permissions
\c idap_users;
GRANT ALL ON SCHEMA public TO idap_admin;
