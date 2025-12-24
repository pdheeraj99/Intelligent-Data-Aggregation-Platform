-- PostgreSQL Initialization Script for IDAP
-- Creates databases for all services that use PostgreSQL

-- Create database for Weather Service
CREATE DATABASE idap_weather;

-- Create database for User Service
CREATE DATABASE idap_users;

-- Create database for Analytics Service
CREATE DATABASE idap_analytics;

-- Create database for Notification Service
CREATE DATABASE idap_notifications;

-- Grant all privileges to the admin user
GRANT ALL PRIVILEGES ON DATABASE idap_weather TO idap_admin;
GRANT ALL PRIVILEGES ON DATABASE idap_users TO idap_admin;
GRANT ALL PRIVILEGES ON DATABASE idap_analytics TO idap_admin;
GRANT ALL PRIVILEGES ON DATABASE idap_notifications TO idap_admin;

-- Connect to each database and set up schema permissions
\c idap_weather;
GRANT ALL ON SCHEMA public TO idap_admin;

\c idap_users;
GRANT ALL ON SCHEMA public TO idap_admin;

\c idap_analytics;
GRANT ALL ON SCHEMA public TO idap_admin;

\c idap_notifications;
GRANT ALL ON SCHEMA public TO idap_admin;

