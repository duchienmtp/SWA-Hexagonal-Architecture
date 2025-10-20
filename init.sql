CREATE DATABASE "order-service";
CREATE DATABASE "payment-service"; 
CREATE DATABASE "restaurant-service";

-- PostgreSQL doesn't use server-level logins, users are global
-- The kagami user is already created by the POSTGRES_USER environment variable

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE "order-service" TO kagami;
GRANT ALL PRIVILEGES ON DATABASE "payment-service" TO kagami;
GRANT ALL PRIVILEGES ON DATABASE "restaurant-service" TO kagami;