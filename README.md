# REST API backend for e-commerce system.

## Tech Stack
- Java **21**
- Spring Boot **3.5.0**
- Spring Web (REST)
- Spring Security + JWT (JJWT)
  - Access token: `Authorization: Bearer <token>`
  - Refresh token: **HttpOnly Cookie** (`refresh-token`)
- Spring Data JPA (Hibernate)
- MySQL (default)
- Validation (Jakarta), ModelMapper
- **AWS S3** (product image upload via AWS SDK v2)
- VNPay (sandbox config)

## Prerequisites
- JDK **21**
- Maven (or Maven Wrapper `./mvnw`)
- MySQL 8+
- AWS S3 bucket (for image upload)

## Configuration

### Required environment variables
Backend requires these env vars:

**Database**
- `DB_PASSWORD` — MySQL password

**Security**
- `JWT_SECRET` — secret key for signing JWT

**AWS S3**
- `AWS_ACCESS_KEY`
- `AWS_SECRET_KEY`

## Auth & Access Rules

Public: /api/auth/**, /api/public/**, GET /api/payments/vnpay-return

Admin only: /api/admin/**

Admin or Seller: /api/seller/**

Others: authenticated user

## Upload & Images

File upload (product images) to AWS S3 (AWS SDK v2)

## VNPay (Sandbox)

Return URL: http://localhost:8080/api/payments/vnpay-return

## API Endpoints (Backend)
### Public

POST /api/auth/signup

POST /api/auth/login

POST /api/auth/refresh

POST /api/auth/logout

GET /api/auth/user

GET /api/auth/username

GET /api/public/categories

GET /api/public/products

GET /api/payments/vnpay-return

Admin only (/api/admin/**)

GET /api/admin/seller

POST /api/admin/seller

### Categories

POST /api/admin/categories

PUT /api/admin/categories/{id}

DELETE /api/admin/categories/{id}

### Products

GET /api/admin/products

POST /api/admin/categories/{categoryId}/product

PUT /api/admin/products/{productId}

DELETE /api/admin/products/{productId}

PUT /api/admin/products/{productId}/image

### Orders

GET /api/admin/orders

PUT /api/admin/orders/status

### Analytics

GET /api/admin/analytics

Seller or Admin (/api/seller/**)

GET /api/seller/products

POST /api/seller/categories/{categoryId}/product

PUT /api/seller/products/{productId}

DELETE /api/seller/products/{productId}

PUT /api/seller/products/{productId}/image

### Orders

GET /api/seller/orders

PUT /api/seller/orders/status

## Authenticated (user)

### Cart

GET /api/carts

GET /api/carts/users/cart

POST /api/carts/products/{productid}/quantity/{quantity}

PUT /api/carts/items

DELETE /api/carts/items

DELETE /api/carts/items/{productId}

PUT /api/carts/sync

### Orders

GET /api/orders

POST /api/orders

GET /api/orders/{orderId}

DELETE /api/orders/{orderId}

### Address

GET /api/addresses

POST /api/addresses

GET /api/addresses/{addressId}

PUT /api/addresses/{addressId}

DELETE /api/addresses/{addressId}

GET /api/users/addresses

### Payment

POST /api/payments/{paymentId}/refund

GET /api/payment-methods
