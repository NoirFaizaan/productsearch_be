# Product Search Application

## Project Overview
This is a Spring Boot application that loads product data from an external API into an in-memory H2 database and provides RESTful endpoints for searching and retrieving product information upon initialization.

## Tech Stack
- **Java Version**: 17
- **Spring Boot Version**: 3.3.4
- **Maven Version**: 3.9.5
- **Database**: H2 In-Memory Database
- **External API**: https://dummyjson.com/products

## Features
1. **Automatic Data Loading**: 
   - Automatically loads product data from external API on application startup
   - Supports adding new products and updating existing ones

2. **Product Search Endpoints**:
   - Search products by title or description (minimum 3 characters)
   - Retrieve product by ID or SKU
   - Input validation and error handling

3. **Test Coverage**:
   - JaCoCo plugin integrated for comprehensive test coverage analysis

4. **Key Technologies**:
   - Spring Boot REST API
   - JPA for database operations
   - Global exception handling
   - Logging with SLF4J
   - Data transfer objects (DTOs)
   - Swagger/OpenAPI documentation

## Prerequisites
- JDK 17
- Maven 3.9.5

## Setup and Installation

### Clone the Repository
```bash
git clone <repository-url>
cd product-search-application
```

### Build the Application
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

## Configuration
Key configuration files:
- `application.properties`: Contains database and API configurations
- `pom.xml`: Maven dependencies and project configuration

### H2 Database Console
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:testdb
- **Username**: sa
- **Password**: (leave blank)

## API Endpoints

### 1. Load Products
- **URL**: `/api/products/load`
- **Method**: POST
- **Description**: Manually trigger product data loading from external API

### 2. Search Products
- **URL**: `/api/products/search`
- **Method**: GET
- **Parameters**: `query` (minimum 3 characters)
- **Description**: Search products by title or description

### 3. Get Product by ID/SKU
- **URL**: `/api/products/{idOrSku}`
- **Method**: GET
- **Description**: Retrieve a specific product

## Swagger Documentation
- **URL**: http://localhost:8080/swagger-ui.html

## Testing and Code Coverage

### Run Unit Tests
```bash
mvn test
```

### Generate Test Coverage Report
```bash
mvn jacoco:report
```

#### Test Coverage Report Location
- **HTML Report**: `target/site/jacoco/index.html`
- **XML Report**: `target/site/jacoco/jacoco.xml`

### JaCoCo Configuration Highlights
- Tracks code coverage across:
  - Line coverage
  - Branch coverage
  - Complexity coverage
- Configurable minimum coverage thresholds
- Generates detailed reports for analysis

## Error Handling
- Custom exception classes
- Global exception handler
- Detailed error responses

## Logging
- Uses SLF4J with logback
- Logs stored in `logs/application.log`

## Security Considerations
- Input validation
- Sanitization of search queries
- Exception handling to prevent information leakage

## Performance Optimizations
- Efficient database queries
- Optimized data loading process
- Minimal external API calls


## Troubleshooting
- Check application logs
- Verify external API connectivity
- Ensure correct Java and Maven versions

## Future Enhancements
- Implement advanced caching mechanisms
- Expand search capabilities
- Add pagination support
- Implement more comprehensive error reporting
