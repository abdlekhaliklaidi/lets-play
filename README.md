# Let's Play API

A RESTful CRUD API built with **Spring Boot**, **MongoDB**, and **JWT authentication**.

## Tech Stack

- Java 17
- Spring Boot 3.2
- Spring Security + JWT (JJWT 0.12)
- MongoDB
- Lombok
- Bucket4j (Rate Limiting)

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- MongoDB running on `localhost:27017`

### Run the App

```bash
mvn spring-boot:run
```

The server starts on `http://localhost:8080`.

---

## API Endpoints

### Auth (`/api/auth`) — Public, Rate Limited

| Method | Endpoint             | Description         |
|--------|----------------------|---------------------|
| POST   | `/api/auth/register` | Register a new user |
| POST   | `/api/auth/login`    | Login & get JWT     |

**Register Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePass123"
}
```

**Login Request:**
```json
{
  "email": "john@example.com",
  "password": "securePass123"
}
```

**Auth Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "id": "64abc...",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "USER"
}
```

---

### Products (`/api/products`)

| Method | Endpoint                      | Auth Required | Role         |
|--------|-------------------------------|---------------|--------------|
| GET    | `/api/products`               | ❌            | Public       |
| GET    | `/api/products/{id}`          | ✅            | Any          |
| GET    | `/api/products/user/{userId}` | ✅            | Any          |
| POST   | `/api/products`               | ✅            | Any User     |
| PUT    | `/api/products/{id}`          | ✅            | Owner/Admin  |
| DELETE | `/api/products/{id}`          | ✅            | Owner/Admin  |

**Create Product Request:**
```json
{
  "name": "Gaming Mouse",
  "description": "High DPI gaming mouse",
  "price": 49.99
}
```

---

### Users (`/api/users`) — Admin Only

| Method | Endpoint          | Description         |
|--------|-------------------|---------------------|
| GET    | `/api/users`      | Get all users       |
| GET    | `/api/users/{id}` | Get user by ID      |
| PUT    | `/api/users/{id}` | Update user         |
| DELETE | `/api/users/{id}` | Delete user (+ their products) |

---

## Authentication

Include the JWT token in the `Authorization` header:

```
Authorization: Bearer <your_token_here>
```

---

## Security Features

- **BCrypt (strength 12)** for password hashing
- **JWT tokens** expire after 24 hours
- **Role-based access** (`USER` vs `ADMIN`)
- **Input validation** on all request bodies
- **Global exception handling** — no unhandled 5XX errors
- **Rate limiting** on `/api/auth/**` — 20 requests/minute per IP
- **CORS** configured for `localhost:3000` and `localhost:4200`
- **Passwords never returned** in API responses (`@JsonIgnore`)

---

## HTTP Status Codes

| Code | Meaning                    |
|------|----------------------------|
| 200  | OK                         |
| 201  | Created                    |
| 204  | No Content (delete)        |
| 400  | Bad Request / Validation   |
| 401  | Unauthorized (bad token)   |
| 403  | Forbidden (wrong role)     |
| 404  | Resource Not Found         |
| 409  | Conflict (duplicate email) |
| 429  | Too Many Requests          |

---

## Project Structure

```
src/main/java/com/letsplay/
├── config/           # Security, CORS, Rate limiting
├── controller/       # REST controllers (Auth, User, Product)
├── dto/              # Request/Response data transfer objects
├── exception/        # Custom exceptions + GlobalExceptionHandler
├── model/            # MongoDB documents (User, Product)
├── repository/       # MongoRepository interfaces
├── security/         # JWT service, filter, UserDetailsService
└── service/          # Business logic (Auth, User, Product)
```

---

## Creating an Admin

To create an admin, directly insert a user with `"role": "ADMIN"` in MongoDB, or update an existing user:

```js
db.users.updateOne({ email: "admin@example.com" }, { $set: { role: "ADMIN" } })
```
