# SWA Microservices Project Guide

Welcome to the SWA Microservices project! This guide will walk you through setting up and running the entire application stack, from the database to the API gateway.

## Table of Contents
1.  [Prerequisites](#1-prerequisites)
2.  [Running the Application](#2-running-the-application)
3.  [Accessing Services](#3-accessing-services)
4.  [Database Setup](#4-database-setup)
    * [PostgreSQL (pgAdmin)](#postgresql-pgadmin)
    * [MongoDB (Mongo Express)](#mongodb-mongo-express)
5.  [Kafka and Avro Schema](#5-kafka-and-avro-schema)
6.  [Project Structure & Configuration](#6-project-structure--configuration)
7.  [Testing with the API Gateway](#7-testing-with-the-api-gateway)
8.  [Important Docker Notes](#8-important-docker-notes)

---
## 1. Prerequisites

Before you begin, make sure you have the following installed:
* **Docker and Docker Compose**: For running the application containers. Ensure Docker Desktop has sufficient resources allocated (e.g., 4GB+ RAM).
* **A Java Development Kit (JDK)**: Version 17 or higher.
* **An API Client**: Such as Postman or Insomnia for testing endpoints.

---
## 2. Running the Application

The entire application stack is managed by Docker Compose.

**Start the Docker Containers**:
Once the build is complete, start all services using Docker Compose: `docker compose up -d --build`.

* The `--build` flag ensures that Docker rebuilds the images with your latest code.
* To run in the background, use `docker compose up -d --build`.

---
## 3. Accessing Services

Once all containers are running, you can access the various UIs and services:

| Service               | URL                           | Description                                     |
| --------------------- | ----------------------------- | ----------------------------------------------- |
| **pgAdmin 4** | `http://localhost:5050`       | UI for managing the PostgreSQL database.        |
| **Mongo Express** | `http://localhost:8083`       | UI for managing the MongoDB database.           |
| **Kafka UI** | `http://localhost:8080`       | UI for viewing Kafka topics and messages.       |
| **MailHog** | `http://localhost:8025`       | Catches and displays emails sent by the app.    |
| **Eureka Discovery** | `http://localhost:8761`       | Service registry dashboard.                     |

---
## 4. Database Setup

The databases are automatically created by Docker Compose, but you need to connect to them with the management tools.

### PostgreSQL (pgAdmin)

The `order-service`, `payment-service` and `restaurant-service` uses PostgreSQL. Their databases will be created automatically when you create a server with the appropriate setup below.

**How to connect in pgAdmin:**
1.  Open pgAdmin at `http://localhost:5050`.
2.  Log in with the credentials:
    * **Email**: `admin@admin.com`
    * **Password**: `admin`
3.  Right-click on **Servers** -> **Create** -> **Server...**.
4.  In the **General** tab, give it a name (e.g., `SWA_Server`).
5.  Switch to the **Connection** tab and enter the following details:
    * **Host name/address**: `postgresql` (This is the service name from `docker-compose.yml`).
    * **Port**: `5432`
    * **Username**: `kagami`
    * **Password**: `kagami`
6.  Click **Save**. You can now browse the `order-service` database and its tables.

### MongoDB (Mongo Express)

The `customer-service` and `notification-service` use MongoDB. The `customer-db` and `notification-db` will be created automatically when there is at least one "Write" operation performed to these database.

**How to access in Mongo Express:**
1.  Open Mongo Express at `http://localhost:8083`.
2.  Logging in with username/password: `admin/pass`
3.  On the left, you will see the databases created by the services, such as `customer-db` and `notification-db`. You can click on them to view and manage the collections and documents.

---
## 5. Kafka and Avro Schema

This project uses Kafka for asynchronous communication and **Avro** for schema enforcement.

**How Avro is configured:**
* The Avro schema files (`.avsc`) are located in the `src/main/resources/avro/` directory of the producing (e.g: `order-service`) and consuming (e.g: `notification-service`) services.
* **Crucially, these schema files must be identical.** The `namespace` and `name` properties combine to create a unique identifier for the data contract. If they mismatch, the consumer will fail to deserialize messages.
* The `avro-maven-plugin` in the `pom.xml` automatically generates Java classes from these schemas during the `mvn clean install` build process.

**How to view messages:**
1.  Open Kafka UI at `http://localhost:8080`.
2.  Navigate to the `order-topic`. You can view messages as they are produced by the `order-service`.
3.  Kafka UI will show the deserialized Avro payload, making it easy to inspect the data.

---
## 6. Project Structure & Configuration

Due to the project's folder structure, the main application class for each service needs to explicitly scan all required packages.

**Application File Setup:**
For each service (e.g., `OrderServiceApplication.java`), the `@SpringBootApplication` annotation is configured with `scanBasePackages` to include all necessary modules:
```java
@SpringBootApplication(scanBasePackages = {
    "com.swa.order_container",
    "com.swa.order_domain",
    "com.swa.order_infrastructure",
    "com.swa.order_application"
})
// ... other annotations
public class OrderServiceApplication { ... }
```
---
## 7. Testing with the API Gateway

All API requests should go through the API Gateway, which runs on port 8222. The gateway routes requests to the appropriate downstream service.

**Example: Creating an Order:**

1. **Endpoint:** `POST http://localhost:8222/api/v1/customers/create` to create a new user if no user available.
```
{
    "customerId": "fa156180-e05d-4c2c-97f5-7a53d0d1b8fd",
    "fullName": "Nguyen Van A",
    "email": "john@mail.com",
    "address": {
        "street": "St. Blue",
        "postalCode": "50000",
        "city": "Sydney"
    },
    "balance": 500
}
```
2. **Insert Data** from `restaurant-service.sql` to `restaurant-service` database to create restaurant data.
3. **Endpoint:** `POST http://localhost:8222/api/v1/orders/create` to create a new order from the current user.
```
{
    "customerId": "fa156180-e05d-4c2c-97f5-7a53d0d1b8fd",
    "restaurantId": "c41fa001-eb7b-4bbf-910a-50a1c6b2e1d1",
    "price": 200.0,
    "items": [
        {
            "productId": "d215b5f8-0249-4dc5-89a3-51fd148cfb48",
            "quantity": 1,
            "price": 50.00,
            "subTotal": 50.00
        },
        {
            "productId": "42cc957e-efb0-46a8-ab7d-50cf4b17641c",
            "quantity": 2,
            "price": 50.00,
            "subTotal": 100.00
        },
        {
            "productId": "b9f138af-59f0-47a2-89e7-5f80de434804",
            "quantity": 1,
            "price": 50.00,
            "subTotal": 50.00
        }
    ],
    "address":{
        "street": "123MainStreet",
        "postalCode": "1000AB",
        "city": "Amsterdam"
    }
}
```

The API Gateway will forward this request to the order-service, which will then process it.

---
## 8. Important Docker Notes

* **Data Persistence:** All database data is stored in named Docker volumes (`postgres_db_data`, `mongodb_data`, `pgadmin`). This means your data will persist even if you stop and remove the containers.
* **"Data is Gone" Illusion:** If you run `docker-compose up -d --build` and immediately refresh pgAdmin, it might look like your data is gone. It is not. This is a timing issue where pgAdmin tries to reconnect before the PostgreSQL server inside the new container is fully ready. Just wait 5-10 seconds and refresh again.
* **Initial Startup Waiting Period (Critical) ⚠️:** After running `docker-compose up -d --build`, you **must wait** for all services to be fully connected and registered in Eureka before calling the API Gateway. Although a container may show `Up` in Docker Desktop, the Spring Boot application inside needs time to connect to Kafka, register with Eureka, and finish initializing its beans. If you call an API too soon, you may receive a Service Unavailable error. Wait until the Eureka dashboard (`http://localhost:8761`) shows all services (`ORDER-SERVICE`, `NOTIFICATION-SERVICE`, etc.) as **green/available.**
* **Viewing Logs:** To see the logs for a specific service, run:
  ``` bash
  docker-compose logs -f <service_name>
  ```
  For example: `docker-compose logs -f order-service`
