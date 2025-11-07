# Leave Management System

## Overview
The Leave Management System is a Spring Boot application designed to manage employee leave requests. It provides a RESTful API for employees to apply for leave, managers to approve or reject leave requests, and for tracking leave balances.

## Features
- Apply for different types of leave (Sick, Casual, Earned)
- Approve or reject leave requests
- Cancel leave requests
- View leave history for employees
- View team leave requests for managers
- Check leave balances

## Technology Stack
- Java 17
- Spring Boot 3.5.7
- Spring Data JPA
- PostgreSQL
- Maven
- Swagger/OpenAPI for API documentation
- JaCoCo for code coverage
- SonarQube for code quality analysis

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- SonarQube Server (for code quality analysis)

## Setup Instructions

### Database Setup
1. Create a PostgreSQL database named `leave_management`
2. Configure database connection in `.env` file or use the default settings in `application.yml`

### Environment Variables
The application uses the following environment variables (can be set in `.env` file):
- `DB_URL` - Database URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `DB_DRIVER` - Database driver class
- `DB_DDL_AUTO` - Hibernate DDL auto mode
- `DB_DIALECT` - Hibernate dialect

## Running the Application

### Using Maven
```bash
mvn clean install
mvn spring-boot:run
```

### Using JAR file
```bash
mvn clean package
java -jar target/leave-management-system-0.0.1-SNAPSHOT.jar
```

### Accessing the Application
- API Base URL: http://localhost:8080/api/leaves
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## API Endpoints

| Method | Endpoint                           | Description                   |
|--------|-----------------------------------|-------------------------------|
| POST   | /api/leaves                       | Apply for leave               |
| PUT    | /api/leaves/approve               | Approve or reject leave       |
| PUT    | /api/leaves/{leaveId}/cancel      | Cancel leave request          |
| GET    | /api/leaves/employee/{employeeId} | Get employee leave history    |
| GET    | /api/leaves/manager/{managerId}/team | Get team leaves for manager |
| GET    | /api/leaves/balance/{employeeId}  | Get employee leave balance    |

## Using SonarQube

### SonarQube Setup
1. Start SonarQube server (default: http://localhost:9000)
2. Create a project with key `leave-management-system`

### Running SonarQube Analysis
```bash
# Run tests with coverage
mvn clean verify

# Run SonarQube analysis
mvn clean verify sonar:sonar -Dsonar.login= <token>
```

### SonarQube Configuration
The project includes configuration for SonarQube in:
- `pom.xml` - Maven plugin configuration
- `sonar-project.properties` - SonarQube project settings

Key SonarQube settings:
- Project Key: leave-management-system
- Coverage Report: JaCoCo XML report
- Exclusions: DTOs, models, config classes, enums, Application class

## Project Structure
```
leave-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hcltech/leave/
│   │   │       ├── controller/      # REST controllers
│   │   │       ├── dto/             # Data Transfer Objects
│   │   │       ├── enums/           # Enumerations
│   │   │       ├── exception/       # Custom exceptions
│   │   │       ├── model/           # Entity models
│   │   │       ├── repository/      # Data repositories
│   │   │       ├── service/         # Business logic
│   │   │       └── LeaveManagementSystemApplication.java
│   │   └── resources/
│   │       ├── application.yml      # Application configuration
│   │       └── data.sql             # Initial data
│   └── test/
│       └── java/
│           └── com/hcltech/leave/   # Test classes
├── .env                             # Environment variables
├── pom.xml                          # Maven configuration
└── sonar-project.properties         # SonarQube configuration
```

## Testing
The application includes unit tests for controllers, services, and exception handlers.

### Running Tests
```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn verify
```

## Code Coverage
JaCoCo is configured to generate code coverage reports during the build process. The coverage report can be found at `target/site/jacoco/index.html` after running `mvn verify`.

