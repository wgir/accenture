## Architecture
- **Layering**: Controller -> Service -> Repository. Never bypass the Service layer.
- **DTOs**: Controllers must never return Entities. Map Entities to ResponseDTOs.
- **Dependency Injection**: Always use Constructor Injection (no @Autowired on fields).
- **Exception Handling**: Use a GlobalExceptionHandler with ProblemDetails.
- **Logging**: Use SLF4J with Logback.
- **Testing**: Use JUnit 5 for unit tests and Mockito for mocking.
- **Versioning**: Use API versioning (e.g., /v1).
- **Reactive Programming**: Use Project Reactor for reactive programming.
- **Observability**: Use Spring Boot Actuator for metrics/health.

## Coding Standards
- **Build Tool**: Maven. (Do not use Gradle).
- **Spring Boot Version**: 3.4.1
- **Java Version**: 21
- **Database Engine**: Mysql 8
- **Lombok**: Use @Data, @RequiredArgsConstructor, and @Builder.
- **Error Handling**: Use a GlobalExceptionHandler with ProblemDetails.
- **Testing**:
  - Unit tests for Services (Mockito).
  - Integration tests for Controllers (@WebMvcTest or @SpringBootTest).
  - Use AssertJ for assertions.
## Naming Conventions
- Interfaces: Do not use `I` prefix (e.g., `UserService`, not `IUserService`).
- Implementations: Append `Impl` only if necessary, otherwise rely on the interface name if strictly needed (Spring generally encourages class-based injection if no multiple impls exist).
- Use camelCase for variable and method names.
- Use PascalCase for class names.
- Use snake_case for file names.
- Use snake_case for package names.

## Code Organization
- Use the following package structure:
  - `com.accenture.api`: REST controllers.
  - `com.accenture.service`: Business logic.
  - `com.accenture.repository`: Data access.
  - `com.accenture.model`: Domain models.
  - `com.accenture.config`: Configuration classes.
  - `com.accenture.security`: Security configuration.
  - `com.accenture.util`: Utility classes.

## Key Principles:
- Convention over Configuration
- Standalone, production-grade applications
- Opinionated 'starter' dependencies
- Dependency Injection (IoC)
- Aspect-Oriented Programming (AOP)
- Show SQL queries in console

## Core Annotations:
- @SpringBootApplication: Main entry point
- @RestController / @Controller: Web layer
- @Service: Business logic layer
- @Repository: Data access layer
- @Component: Generic bean

## Data Access:
- Spring Data JPA for relational DBs
- Hibernate as JPA implementation
- Repository interfaces (JpaRepository)
- Transaction management (@Transactional)
- Flyway/Liquibase for migrations

## Configuration:
- application.yml
- Profiles (dev, test, prod)
- @ConfigurationProperties for type-safe config
- @Value for simple injection
- Externalized configuration

## Security (Spring Security):
- Authentication and Authorization
- JWT or Session-based auth
- Method-level security (@PreAuthorize)
- CORS and CSRF configuration
- OAuth2 / OIDC integration

## Observability:
- Spring Boot Actuator for metrics/health
- Micrometer for metrics export
- Distributed tracing (Zipkin/Otel)
- Structured logging

## Best Practices:
- Use constructor injection (avoid @Autowired on fields)
- Handle exceptions globally (@ControllerAdvice)
- Validate inputs (@Valid, @NotNull)
- Write integration tests (@SpringBootTest)
- Use Lombok to reduce boilerplate
