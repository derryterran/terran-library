# Terran Library Management System

A Spring Boot application that provides REST APIs for managing a library system. The application allows registering borrowers and books, borrowing and returning books, and viewing the library catalog.

## Features

- Register borrowers with name and email
- Register books with ISBN, title, and author
- View all books in the library
- Borrow books
- Return books
- Multiple copies of books with the same ISBN are supported
- Only one borrower can borrow a specific book at a time

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL Database
- Swagger/OpenAPI for API documentation
- Spring Actuator for monitoring
- Docker for containerization
- Maven
- Support for multiple environment Dev / Prod
- H2 in-memory DB

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- Docker

### Running the Application

#### Clone the repository

```bash
# Clone the repository
git clone https://github.com/derryterran/terran-library.git
```

#### Using Docker

```bash
# After clone the repository
git clone https://github.com/derryterran/terran-library.git
cd terran-library

# Build and run with Docker Compose (includes PostgreSQL)
docker-compose up -d

# This will start both the PostgreSQL database and the application
# The application will wait for PostgreSQL to be ready before starting
```

#### Using Maven

```bash
# After clone the repository
git clone https://github.com/derryterran/terran-library.git
cd terran-library

# Build the application
mvn clean package

# Run the application
java -jar target/terran-library-0.0.1-SNAPSHOT.jar
or via spring boot
mvn spring-boot:run
```

### Environment Profiles

The application supports multiple environment profiles:

- **default**: Default configuration with PostgreSQL database (terran_library)
- **dev**: Development environment with PostgreSQL database (terran_library_dev) and detailed logging
- **prod**: Production environment with PostgreSQL
- **test**: Test environment with H2 in memory database for faster test execution

#### Configuration

The application uses YAML(not .properties) files for configuration, which provides several benefits:
- Hierarchical configuration structure for better organization
- Support for complex data types and arrays
- More readable format for nested properties
- Environment specific configurations in separate files

Configuration files:
- `application.yaml`: Common settings for all environments
- `application-dev.yaml`: Development-specific settings
- `application-prod.yaml`: Production-specific settings
- `application-test.yaml`: Test-specific settings

To run with a specific profile, example dev:

```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Using Java
java -jar target/library-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Using Docker Compose
SPRING_PROFILES_ACTIVE=dev docker-compose up -d
```

## API Documentation

The API documentation is available via Swagger UI at:

```
http://localhost:8084/swagger-ui.html
```

### API Endpoints

#### Borrower Management

- `POST /terranapi/borrowers` - Register a new borrower
- `GET /terranapi/borrowers` - Get all borrowers
- `GET /terranapi/borrowers/{id}` - Get a borrower by ID

#### Book Management

- `POST /terranapi/book` - Register a new book
- `GET /terranapi/books` - Get all books
- `GET /terranapi/books/{id}` - Get a book by ID
- `POST /terranapi/books/{id}/borrow` - Borrow a book
- `POST /terranapi/books/{id}/return` - Return a book
- `POST /terranapi/{id}/borrow` - Borrow a book (direct URL format)
- `POST /terranapi/{id}/return` - Return a book (direct URL format)

> **Note:** For borrowing and returning books, both standard format (`/terranapi/books/{id}/borrow`) and direct format (`/terranapi/{id}/borrow`) are supported.

### Example Requests

#### Register a Borrower

```http
POST /terranapi/borrowers
Content-Type: application/json

{
  "name": "Derry Terran",
  "email": "derry.terran@terranclan.com"
}
```

#### Register a Book

```http
POST /terranapi/book
Content-Type: application/json

{
  "isbn": "889900112233",
  "title": "Terran Clan of Kiseki",
  "author": "Derry Terran"
}
```

#### Borrow a Book

Standard format:
```http
POST /terranapi/books/1/borrow
Content-Type: application/json

{
  "borrowerId": 1
}
```

Direct format:
```http
POST /terranapi/1/borrow
Content-Type: application/json

{
  "borrowerId": 1
}
```

#### Return a Book

Standard format:
```http
POST /terranapi/books/1/return
```

Direct format:
```http
POST /terranapi/1/return
```

## Monitoring

The application includes Spring Actuator endpoints for monitoring:

- Health: `http://localhost:8084/actuator/health`
- Info: `http://localhost:8084/actuator/info`
- Metrics: `http://localhost:8084/actuator/metrics`

## Database

The application uses PostgreSQL as the database, which is:

- A powerful, open source object relational database system
- Provides robust data integrity and reliability
- Supports complex queries and transactions
- Highly scalable for enterprise applications
- Offers advanced features like JSON support and full-text search

### Database Setup

#### Local Development

For local development, you need to:

1. Install PostgreSQL on your machine
2. Create a database named `terran_library`
3. Configure the application to connect to your database:
    - Default settings use:
        - Host: localhost
        - Port: 5432
        - Username: terrandb
        - Password: terrandb

All database settings are configurable in the YAML configuration files:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/terran_library
    username: terrandb
    password: terrandb
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
```

You can modify these settings in the appropriate YAML file based on your environment.

#### Docker Environment

When using Docker Compose, the PostgreSQL database is automatically:
- Set up with the correct database name
- Configured with the credentials specified in docker-compose.yml
- Linked to the application container
- Persisted using a Docker volume

The production YAML configuration uses environment variables with defaults:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:postgres}:${DB_PORT:5432}/${DB_NAME:terran_library}
    username: ${DB_USERNAME:terrandb}
    password: ${DB_PASSWORD:terrandb}
```

These environment variables are set in the docker-compose.yml file:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - DB_HOST=postgres
  - DB_PORT=5432
  - DB_NAME=terran_library
  - DB_USERNAME=terrandb
  - DB_PASSWORD=terrandb
```

#### Testing

For testing, the application uses H2 in-memory database for faster execution and isolation. The test configuration is defined in `application-test.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create
    generate-ddl: true
```

This configuration ensures that:
- Tests run with an isolated in-memory database
- Database schema is automatically created before tests
- No external database is required for testing
- Tests run faster with minimal setup

## Assumptions and Design Decisions

1. **ISBN Handling**: Books with the same ISBN are considered the same title but different physical copies,so have to use same Title and Author.
2. **Borrowing Rules**: A book can only be borrowed by one borrower at a time.
3. **Email Uniqueness**: Borrower emails must be unique in the system.
4. **Data Validation**: Basic validation is implemented for all inputs.
5. **Error Handling**: Appropriate error responses are returned for invalid requests.
6. **ID Generation**: Using GenerationType.AUTO for database compatibility.

## License

This project is licensed under the TerranClan License - see the LICENSE file for details.
