# Franchise Management API

A reactive Spring Boot application designed to manage franchises, their branches, and product inventory. This project demonstrates high-performance, non-blocking I/O using Spring WebFlux, R2DBC, and MySQL.

## 🚀 Features

- **Franchise Management**: Create, update, and list franchises.
- **Branch Management**: Add branches to franchises and update branch details.
- **Product Inventory**: 
  - Add products to specific branches.
  - Update product stock levels.
  - Update product names.
  - Delete products.
- **Top Products Analysis**: Retrieve the product with the most stock for each branch within a franchise.
- **Reactive Architecture**: Fully non-blocking stack from the controller down to the database.
- **Real-time Streaming**: Supports Server-Sent Events (SSE) for franchise listing.
- **Global Error Handling**: Standardized RFC 7807 Problem Details for 404, 409, and validation errors.

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring WebFlux** (Reactive Web)
- **Spring Data R2DBC** (Reactive Database Access)
- **MySQL** (Database)
- **Lombok** (Boilerplate reduction)
- **Maven** (Dependency Management)

## 📋 Prerequisites

- Java 21 JDK
- Maven 3.9+
- MySQL 8.0+

## ⚙️ Configuration

The application uses `src/main/resources/application.yml` for configuration. You can override database settings via environment variables:

- `RDS_HOSTNAME`: Database host (default: `127.0.0.1`)
- `RDS_PORT`: Database port (default: `3306`)
- `RDS_DB_NAME`: Database name (default: `franchise`)
- `RDS_DB_USERNAME`: Database user (default: `admin`)
- `RDS_DB_PASSWORD`: Database password

## 🛠️ How to Compile and Run

### 1. Clone the repository
```bash
git clone git@github.com:wgir/accenture.git
cd accenture
```

### 2. Compile and Build
```bash
mvn clean install
```

### 3. Run the Application
Create database `franchise` in MySQL

```bash
mvn spring-boot:run
```
The server will start on `http://localhost:8080` (or the configured port).

### 4. Run Tests
```bash
mvn test
```

## 🔌 API Endpoints

[Swagger](http://ec2-13-218-78-102.compute-1.amazonaws.com:8080/webjars/swagger-ui/index.html)

### Franchises
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/v1/franchises` | List all franchises (Event Stream) |
| `POST` | `/v1/franchises` | Create a new franchise |
| `PUT` | `/v1/franchises/{id}` | Update franchise name |
| `POST` | `/v1/franchises/{id}/branches` | Add a branch to a franchise |
| `GET` | `/v1/franchises/{id}/top-products` | Get top products per branch in a franchise |

### Branches
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `PUT` | `/v1/branches/{id}` | Update branch name |
| `POST` | `/v1/branches/{id}/products` | Add a product to a branch |

### Products
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `DELETE` | `/v1/products/{id}` | Remove a product |
| `PATCH` | `/v1/products/{id}/stock` | Update product stock |
| `PATCH` | `/v1/products/{id}/name` | Update product name |

## 🏗️ Design Decisions

- **Uniqueness Constraints**: Branch names must be unique within a single franchise. This is validated at the service level using reactive checks.
- **Reactive Streams**: Used `Flux` and `Mono` for all operations to ensure the application can scale under high load and handle long-standing connections (like SSE).
- **Standardized Errors**: 
  - `404 Not Found`: Returned when resources or routes do not exist.
  - `409 Conflict`: Returned for duplicate names or database constraint violations.
  - `400 Bad Request`: Returned for validation failures.
