# 🚀 Nexus E-Commerce & High-Concurrency Inventory Backend

An enterprise-grade E-Commerce Backend API designed for high concurrency, fast response times, and asynchronous event processing.

## 🛠️ Tech Stack

- **Core:** Java 21 (Virtual Threads enabled), Spring Boot 4.x
- **Database & Migration:** PostgreSQL 16, Spring Data JPA (Hibernate 7), Flyway
- **Caching & Session:** Redis 7 (Product Cache, Shopping Cart Hash, JWT Blacklist)
- **Message Broker:** RabbitMQ 3 (Asynchronous Order Notification Worker)
- **Security:** Spring Security, JWT (Access + Refresh Token rotation), BCrypt
- **Testing & DevOps:** JUnit 5, Mockito, Docker & Docker Compose, GitHub Actions CI, OpenAPI 3 (Swagger)

## ✨ Key Technical Highlights

1. **Preventing Overselling (Concurrency Control):** Implemented **Optimistic Locking (`@Version`)** on the `inventories` table to prevent race conditions during simultaneous checkouts.
2. **Sub-millisecond Shopping Cart & Product Reads:** Built the shopping cart on **Redis Hashes** with a 7-day TTL and cached product details using `@Cacheable` + `@CacheEvict`.
3. **Stateless Security with Immediate Token Revocation:** Combined stateless JWT authentication with a **Redis Token Blacklist** to support instant user logout.
4. **Non-blocking Order Checkout:** Offloaded post-checkout tasks (confirmation emails/notifications) to **RabbitMQ** consumers so the checkout API responds immediately.

## 🚀 Getting Started

### 1. Start Infrastructure Containers

```bash
cp .env.example .env
docker compose up -d
```
