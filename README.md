# todo-api ✅

> just another api that keeps your todos.

A REST API built with Java and Spring Boot for managing todos with JWT authentication. Each user can only access and manage their own todos. Includes registration, login, BCrypt password hashing, role-based access control, and Swagger documentation with bearer auth support.

---

## Tech Stack

- **Java 17**
- **Spring Boot 4.1.0**
- **Spring Web** — REST API
- **Spring Security** — authentication and authorization
- **Spring Data JPA** — database access
- **PostgreSQL** — relational database
- **jjwt** — JWT generation and validation
- **BCrypt** — password hashing
- **Bean Validation** — input validation
- **SpringDoc OpenAPI** — Swagger UI

---

## Features

- 🔐 JWT authentication — register, login, and receive a token
- 🔒 BCrypt password hashing — passwords are never stored in plain text
- 👤 User-scoped todos — each user only sees their own todos
- 🛡️ Role-based access control — `ROLE_USER` and `ROLE_ADMIN`
- 👑 Admin endpoints — list and delete users
- 🌱 Admin user created automatically on first run
- ✅ Toggle todo completed status with a single request
- 🗑️ Cascade delete — deleting a user removes all their todos
- 🌐 Global error handling with clean JSON responses
- 📖 Swagger UI with bearer auth support

---

## Getting Started

### Prerequisites

- [Java 17+](https://adoptium.net/)
- [PostgreSQL](https://www.postgresql.org/) installed and running
- [Maven](https://maven.apache.org/)

### Installation

**1. Clone the repository**
```bash
git clone git@github.com:your-username/todo-api.git
cd todo-api
```

**2. Create the database**
```sql
CREATE DATABASE todo_api;
```

**3. Set up your configuration**
```bash
cp application.properties.example src/main/resources/application.properties
```

Open `src/main/resources/application.properties` and fill in your PostgreSQL credentials and JWT secret.

**4. Run the application**
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.  
An admin user is created automatically on first run:

| Email | Password |
|---|---|
| admin@example.com | admin123 |

---

## Authentication Flow

```
1. Register → POST /auth/register → receive JWT
2. Login    → POST /auth/login    → receive JWT
3. Use JWT  → Authorization: Bearer <token>
```

---

## API Endpoints

### Auth

#### `POST /auth/register`

Creates a new account and returns a JWT token.

**Request body**
```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

**Example response** `201 Created`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com",
  "role": "ROLE_USER"
}
```

---

#### `POST /auth/login`

Authenticates a user and returns a JWT token.

**Request body**
```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

**Example response** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com",
  "role": "ROLE_USER"
}
```

---

### Todos

> All todo endpoints require a valid JWT token in the `Authorization` header:
> `Authorization: Bearer <token>`

#### `POST /todos`

Creates a new todo.

**Request body**
```json
{
  "title": "Buy groceries",
  "description": "Milk, eggs, bread"
}
```

**Example response** `201 Created`
```json
{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, eggs, bread",
  "completed": false,
  "createdAt": "2026-06-22T10:00:00"
}
```

---

#### `GET /todos`

Returns all todos for the authenticated user.

---

#### `GET /todos/{id}`

Returns a single todo by ID.

---

#### `PUT /todos/{id}`

Updates title and description of a todo.

---

#### `PATCH /todos/{id}/toggle`

Toggles the completed status of a todo.

---

#### `DELETE /todos/{id}`

Deletes a todo. Returns `204 No Content`.

---

### Admin

> Admin endpoints require a valid JWT token from a `ROLE_ADMIN` user.

#### `GET /admin/users`

Returns a list of all registered users.

**Example response** `200 OK`
```json
[
  {
    "id": 1,
    "email": "admin@example.com",
    "role": "ROLE_ADMIN"
  },
  {
    "id": 2,
    "email": "user@example.com",
    "role": "ROLE_USER"
  }
]
```

---

#### `DELETE /admin/users/{id}`

Deletes a user and all their todos. Returns `204 No Content`.

---

### Error Responses

| Status | Description |
|---|---|
| `400` | Missing or invalid request body |
| `401` | Invalid credentials or missing token |
| `403` | Insufficient permissions |
| `404` | Todo or user not found |
| `409` | Email already in use |
| `500` | Unexpected server error |

---

## Swagger UI

Interactive API documentation with bearer auth support is available at:

```
http://localhost:8080/swagger-ui/index.html
```

Click **Authorize** and enter your JWT token to test protected endpoints directly from the browser.

---

## Project Structure

```
src/main/java/com/example/todoapi/
│
├── TodoApiApplication.java
├── DataSeeder.java
│
├── controller/
│   ├── AuthController.java
│   ├── TodoController.java
│   └── AdminController.java
│
├── service/
│   ├── AuthService.java
│   ├── TodoService.java
│   └── AdminService.java
│
├── repository/
│   ├── UserRepository.java
│   └── TodoRepository.java
│
├── model/
│   ├── User.java
│   └── Todo.java
│
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── TodoRequest.java
│   ├── TodoResponse.java
│   └── UserResponse.java
│
├── exception/
│   ├── EmailAlreadyExistsException.java
│   ├── TodoNotFoundException.java
│   ├── UserNotFoundException.java
│   └── GlobalExceptionHandler.java
│
├── security/
│   ├── JwtService.java
│   └── JwtAuthFilter.java
│
└── config/
    ├── ApplicationConfig.java
    ├── SecurityConfig.java
    └── SwaggerConfig.java
```

---

## License

Do whatever you want with it.
