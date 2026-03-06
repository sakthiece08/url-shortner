# Copilot Instructions: Spring Boot URL Shortener Project

## Project Overview

**Spring Boot URL Shortener** is a full-stack web application that allows users to create short aliases for long URLs. Users can register, log in, create shortened URLs, track click counts, and manage their URLs. Administrators have access to a dashboard for system management.

### Key Features
- User registration and authentication
- URL shortening with customizable short keys
- Click tracking for shortened URLs
- Public and private URL management
- User dashboard to view and manage personal URLs
- Admin dashboard for system administration
- Pagination support for large URL lists
- Role-based access control (USER, ADMIN)
- Secure password hashing using BCrypt

---

## Technology Stack

### Framework & Language
- **Framework**: Spring Boot 4.0.0
- **Java Version**: Java 21
- **Build Tool**: Maven

### Core Dependencies
- **Spring Boot Starters**:
  - `spring-boot-starter-web` - Web applications and REST APIs
  - `spring-boot-starter-data-jpa` - Database access with JPA/Hibernate
  - `spring-boot-starter-security` - Authentication and authorization
  - `spring-boot-starter-thymeleaf` - Server-side HTML templating
  - `spring-boot-starter-validation` - Bean validation
  - `spring-boot-starter-flyway` - Database migrations

- **UI & Styling**:
  - Thymeleaf 3.x - Template engine for HTML rendering
  - Thymeleaf Layout Dialect - Template inheritance support
  - Bootstrap 5.3.3 (WebJars) - Responsive CSS framework
  - Custom CSS - `src/main/resources/static/css/styles.css`

- **Database**:
  - PostgreSQL (primary database)
  - Flyway - Database version control and migrations
  - Spring Docker Compose - Automatic container management for local development

- **Security & Utilities**:
  - Spring Security 6.x - Authentication and authorization
  - Thymeleaf Extras Spring Security 6 - Security integration with templates
  - Lombok 1.18.42 - Annotation-driven code generation (getters, setters, constructors)
  - BCrypt - Password hashing

- **Testing**:
  - Spring Boot Starter Test - JUnit 5, Mockito, AssertJ
  - Spring Security Test - Security testing utilities

---

## Directory Structure

```
url-shortner/
├── src/
│   ├── main/
│   │   ├── java/com/teqmonic/urlshortner/
│   │   │   ├── UrlShortenerApplication.java        # Application entry point
│   │   │   ├── configs/                            # Configuration classes
│   │   │   ├── controller/                         # Request handlers (MVC Controllers)
│   │   │   ├── exception/                          # Custom exceptions
│   │   │   ├── exceptionhandler/                   # Global exception handling
│   │   │   ├── model/                              # DTOs and commands
│   │   │   ├── model/entities/                     # JPA entity classes
│   │   │   ├── repository/                         # Data access layer
│   │   │   ├── service/                            # Business logic layer
│   │   │   └── util/                               # Utility classes
│   │   └── resources/
│   │       ├── application.properties              # Configuration properties
│   │       ├── db/migration/                       # Flyway migration scripts
│   │       ├── templates/                          # Thymeleaf HTML templates
│   │       ├── static/css/                         # Stylesheets
│   │       └── static/js/                          # (if any) JavaScript files
│   └── test/
│       └── java/com/teqmonic/urlshortner/          # Unit and integration tests
├── pom.xml                                         # Maven configuration
├── docker-compose.yaml                            # Docker Compose for local dev
├── README.md                                       # Project documentation
├── HELP.md                                         # Spring Boot help documentation
└── mvnw / mvnw.cmd                                # Maven wrapper for build

```

---

## Module & Component Breakdown

### 1. Controllers (`controller/`)
**Purpose**: Handle HTTP requests and coordinate responses

- **HomeController** - Handles home page and public URL display
- **UserController** - Manages user registration, login, URL creation, and user dashboard
- **AdminController** - Admin dashboard and system management endpoints

**Key Endpoints**:
- `GET /` - Home page with public URLs
- `POST /register` - User registration
- `GET /login` - Login page
- `POST /my-urls` - Create new shortened URL
- `GET /my-urls` - View user's URLs (authenticated)
- `GET /admin/**` - Admin dashboard (ADMIN role required)
- `GET /s/{shortKey}` - Redirect to original URL

### 2. Repositories (`repository/`)
**Purpose**: Data access layer using Spring Data JPA

- **UserRepository** - CRUD operations for User entities
  - Custom queries for finding users by username
  - Methods: `findByName()`, etc.

- **ShortUrlRepository** - CRUD operations for ShortUrl entities
  - Pagination support: `findPagedPublicShortUrls(Pageable)`
  - Fetch join optimization to prevent N+1 queries
  - Methods for querying public/private URLs

**Key Patterns**:
- Uses `@Query` with JPQL and `join fetch` for eager loading
- Supports pagination with `Pageable` interface
- Custom query methods for business-specific searches

### 3. Services (`service/`)
**Purpose**: Business logic and domain operations

- **ShortUrlService** - URL creation, retrieval, click tracking, URL lookup
  - Generates short keys
  - Manages URL expiration
  - Tracks click counts
  - Handles public/private access control

- **UserService** - User creation, authentication, profile management
  - User registration with validation
  - Password hashing before storage
  - User role assignment

- **SecurityUserDetailService** - Implements `UserDetailsService`
  - Loads user credentials from database for Spring Security
  - Returns user with roles for authentication
  - Throws `UsernameNotFoundException` for invalid users

- **EntityMapper** - DTO conversion
  - Maps JPA entities to DTOs for API responses
  - Prevents exposing sensitive data

- **UrlExistenceValidator** - Custom validation
  - Validates whether a URL is reachable
  - Used during URL shortening

### 4. Models & DTOs (`model/`)
**Purpose**: Data transfer and command objects

**DTOs (Data Transfer Objects)**:
- **ShortUrlDto** - Represents shortened URL data for API/view responses
- **UserDto** - User information without sensitive data
- **PagedResult** - Generic pagination wrapper

**Commands (Request Objects)**:
- **CreateShortUrlCmd** - Command to create a shortened URL
- **CreateShortUrlForm** - Form binding for URL creation requests
- **CreateUserCmd** - Command to create a user
- **RegisterUserRequest** - User registration request

**Enums & Models**:
- **Role** - User role enumeration (USER, ADMIN)

### 5. Entities (`model/entities/`)
**Purpose**: JPA entity classes representing database tables

- **UserEntity**
  - Fields: id, name, email, password (hashed), role, createdAt
  - Relationships: One-to-many with ShortUrlEntity

- **ShortUrlEntity**
  - Fields: id, originalUrl, shortKey, isPrivate, clickCount, createdAt, expiresAt
  - Relationships: Many-to-one with UserEntity (createdBy)

### 6. Exception Handling (`exception/`, `exceptionhandler/`)
**Purpose**: Custom exceptions and centralized error handling

- **ShortUrlNotFoundException** - Thrown when a short URL is not found
- **GlobalExceptionHandler** - `@ControllerAdvice` for centralized exception handling
  - Returns appropriate HTTP status codes and error messages

### 7. Configuration (`configs/`)
**Purpose**: Spring and application configuration

- **WebSecurityConfig**
  - Configures Spring Security filter chain
  - Defines URL access rules (public vs. authenticated vs. admin-only)
  - Configures login/logout behavior
  - Sets up password encoder (BCrypt)
  - Defines role hierarchy

- **ApplicationProperties**
  - Custom configuration properties
  - Injected using `@ConfigurationProperties`

### 8. Utilities (`util/`)
**Purpose**: Helper functions and common operations

- **PasswordUtility** - Password hashing and verification using BCrypt
- **SecurityUtil** - Security-related utilities
  - Retrieving current authenticated user
  - Role checking

### 9. Templates (`resources/templates/`)
**Purpose**: Thymeleaf HTML templates for view rendering

- **layout.html** - Master layout template for all pages
- **index.html** - Home page (displays public URLs with pagination)
- **login.html** - Login form
- **register.html** - User registration form
- **my-urls.html** - User dashboard (create and manage URLs)
- **admin-dashboard.html** - Admin management interface
- **pagination.html** - Reusable pagination component
- **error/404.html** - 404 error page
- **error/500.html** - 500 error page

**Key Features**:
- Uses Thymeleaf layout dialect for template inheritance
- Spring Security integration for role-based view rendering
- Form binding and validation error display
- Dynamic pagination controls

### 10. Database Migrations (`resources/db/migration/`)
**Purpose**: Flyway-managed database schema evolution

- **V1__Create_url_table.sql** - Initial schema (users, short_urls tables)
- **V2__Insert_data.sql** - Sample data insertion
- **V3__Update_user_password.sql** - Schema updates for password management
- **V4__Update_user_table.sql** - Additional user table updates

**Schema Overview**:
- `users` table - Stores user credentials and roles
- `short_urls` table - Stores shortened URL mappings
- `flyway_schema_history` - Flyway migration tracking

### 11. Static Resources (`resources/static/`)
**Purpose**: CSS and JavaScript assets

- **css/styles.css** - Custom styling (complements Bootstrap)
- **js/** - (if present) Client-side JavaScript

### 12. Configuration (`resources/application.properties`)
**Purpose**: Application runtime configuration

**Common Settings**:
- `spring.datasource.*` - Database connection details
- `spring.jpa.*` - JPA/Hibernate configuration
- `spring.security.user.*` - Default credentials (dev only)
- `spring.docker.compose.lifecycle-management` - Docker Compose lifecycle
- `spring.thymeleaf.*` - Thymeleaf configuration

---

## Development Guidelines

### Adding New Features

#### Adding a New Controller
1. Create a new class in `controller/` package
2. Annotate with `@Controller` (for views) or `@RestController` (for APIs)
3. Inject required services via constructor
4. Define request mappings using `@GetMapping`, `@PostMapping`, etc.
5. Return `String` (view name) for `@Controller` or response objects for `@RestController`

#### Adding a New Service
1. Create a new class in `service/` package
2. Annotate with `@Service`
3. Inject repositories or other services via constructor
4. Implement business logic methods
5. Use transactions with `@Transactional` if needed

#### Adding a New Repository
1. Create an interface in `repository/` package
2. Extend `JpaRepository<Entity, ID>`
3. Define custom query methods using `@Query` for complex queries
4. Use `Pageable` parameter for pagination support
5. Apply `@EntityGraph` or `join fetch` to prevent N+1 queries

#### Adding a New Entity
1. Create a class in `model/entities/` package
2. Annotate with `@Entity` and `@Table`
3. Define fields with appropriate JPA annotations (`@Id`, `@GeneratedValue`, `@ManyToOne`, etc.)
4. Use Lombok annotations (`@Data`, `@NoArgsConstructor`, etc.) to reduce boilerplate
5. Create a corresponding Flyway migration script for schema changes

#### Adding a New Template
1. Create an HTML file in `resources/templates/`
2. Add Thymeleaf namespace and layout declarations:
   ```html
   <html xmlns:th="http://www.thymeleaf.org"
         xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
         layout:decorate="~{layout}">
   ```
3. Use `layout:fragment` to define content sections
4. Leverage Bootstrap classes for styling
5. Use Thymeleaf expressions (`${variable}`, `th:if`, `th:each`) for dynamic content

### Code Organization Conventions

#### Naming Conventions
- **Classes**: PascalCase (e.g., `UserService`, `ShortUrlEntity`)
- **Methods**: camelCase (e.g., `createShortUrl()`, `getUserById()`)
- **Variables**: camelCase (e.g., `userName`, `shortKey`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_URL_LENGTH`)
- **Packages**: lowercase, domain-driven (e.g., `com.teqmonic.urlshortner.service`)

#### Dependency Injection
- Use **constructor injection** (preferred) for dependencies
- Annotate with `@RequiredArgsConstructor` (Lombok) for constructor generation
- Avoid `@Autowired` field injection

#### Transaction Management
- Use `@Transactional` on service methods that modify data
- Default to read-only transactions: `@Transactional(readOnly = true)` for queries
- Let Spring manage transaction boundaries

### Security Considerations

#### Authentication & Authorization
- Spring Security is configured in `WebSecurityConfig`
- Public endpoints are whitelisted (home, login, register, static resources)
- Authenticated endpoints require login
- Admin endpoints require `ROLE_ADMIN`

#### Password Security
- Always use `PasswordUtility` for hashing passwords before storage
- Use BCrypt for password encoding (configured in `WebSecurityConfig`)
- Never store plain-text passwords

#### CSRF Protection
- CSRF is disabled in `WebSecurityConfig` (adjust if needed for production)
- Ensure forms include CSRF tokens when enabled

#### URL Access Control
- Private URLs are marked with `isPrivate` flag
- Non-authenticated users can only see public URLs
- Users can only see/edit their own URLs

### Common Patterns & Best Practices

#### N+1 Query Prevention
- Use `@Query` with `join fetch` in repository methods
- Alternative: Use `@EntityGraph` annotation to eagerly load relationships
- Always set `spring.jpa.open-in-view=false` to prevent lazy loading outside transactions

#### Pagination
- Use `Pageable` parameter in repository methods
- Return `Page<T>` for automatic pagination support
- Controller receives page number and size from request parameters

#### Exception Handling
- Throw custom exceptions for domain-specific errors (e.g., `ShortUrlNotFoundException`)
- Use `GlobalExceptionHandler` for centralized error response formatting
- Return appropriate HTTP status codes (400, 404, 500, etc.)

#### DTO Conversion
- Use `EntityMapper` to convert JPA entities to DTOs
- Prevents exposing sensitive fields and internal structure
- Decouples API contracts from entity schemas

#### Form Binding & Validation
- Use command/request objects for form binding (e.g., `CreateShortUrlCmd`)
- Apply validation annotations (`@NotNull`, `@NotEmpty`, `@Size`, etc.)
- Display validation errors in Thymeleaf templates with `th:errors`

### Building & Running

#### Build the Project
```bash
./mvnw clean package
```

#### Run the Application
```bash
./mvnw spring-boot:run
```

#### Run with Docker Compose
The project includes `docker-compose.yaml` for PostgreSQL:
```bash
./mvnw spring-boot:run
# Spring Boot automatically starts PostgreSQL container
```

#### Run Tests
```bash
./mvnw test
```

#### Build Docker Image
```bash
./mvnw spring-boot:build-image
```

### Key Files for Quick Reference

| File | Purpose |
|------|---------|
| `WebSecurityConfig.java` | Security rules and authentication config |
| `UserService.java` | User management business logic |
| `ShortUrlService.java` | URL shortening business logic |
| `ShortUrlRepository.java` | URL data access with pagination |
| `layout.html` | Master template for all pages |
| `pom.xml` | Dependencies and build configuration |
| `application.properties` | Runtime configuration |

---

## For Copilot Assistance

When working with this codebase, consider:

1. **Architecture**: The project follows a layered architecture (Controller → Service → Repository → Entity)
2. **Patterns**: Uses Spring Boot annotations extensively for configuration and auto-wiring
3. **Database**: JPA with Flyway migrations; avoid raw SQL when possible
4. **Security**: Spring Security with role-based access; always hash passwords
5. **Templating**: Thymeleaf with layout inheritance; use `layout:fragment` for content sections
6. **Error Handling**: Custom exceptions caught by `GlobalExceptionHandler`
7. **Testing**: Use Spring Boot Test, Mockito for services, and Spring Security Test for security tests

For questions about specific components, refer to the relevant class documentation or README.md.
