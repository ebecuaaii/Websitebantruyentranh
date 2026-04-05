# API Endpoints - Backend Day 1

Base URL: `http://localhost:8080`

## Public Endpoints

### Health Check
- `GET /api/test`

### Home
- `GET /api/home`
- `GET /api/home?categoryLimit={n}&productLimit={n}&newsLimit={n}`

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`

### Categories
- `GET /api/categories`
- `GET /api/categories/{id}`
- `GET /api/categories/{id}/products`

### Products
- `GET /api/products`
- `GET /api/products?keyword={keyword}`
- `GET /api/products?categoryId={categoryId}`
- `GET /api/products?keyword={keyword}&categoryId={categoryId}`
- `GET /api/products/{id}`
- `GET /api/products/{id}/reviews`

### News
- `GET /api/news`
- `GET /api/news/{id}`
- `GET /api/news/slug/{slug}`

Sample detail response format:
```json
{
  "status": 200,
  "message": "Success",
  "data": {
    "id": 101,
    "title": "Top 5 cuốn sách kinh tế đáng đọc nhất đầu năm 2026",
    "slug": "top-5-sach-kinh-te-2026",
    "thumbnail": "https://cdn.example.com/images/news-01.jpg",
    "summary": "Điểm qua những đầu sách giúp bạn thay đổi tư duy tài chính trong năm mới.",
    "content": "<html>... nội dung bài viết ...</html>",
    "category": {
      "id": 1,
      "name": "Review Sách"
    },
    "author": {
      "name": "Nguyễn Văn A",
      "avatar": "https://cdn.example.com/avatars/user-a.jpg"
    },
    "published_at": "2026-03-30T10:00:00Z",
    "related_books": [
      {
        "id": "book-99",
        "title": "Tư Duy Thịnh Vượng",
        "price": 150000,
        "image": "https://cdn.example.com/books/book-99.jpg"
      }
    ],
    "tags": ["kinh tế", "tài chính", "sách hay 2026"]
  }
}
```

### About
- `GET /api/about`

## Authenticated Endpoints (USER / ADMIN)
- `GET /api/users/me`
- `PUT /api/users/me`
- `POST /api/products/{id}/reviews`
- `POST /api/checkout/preview-coupon`
- `POST /api/checkout`

## Payment Callbacks (Public)
- `POST /api/payments/momo/ipn`
- `GET /api/payments/momo/return`
- `GET /api/payments/vnpay/return`

## Coupons
- `GET /api/coupons/active`

## Admin Endpoints (ADMIN only)

### Users
- `GET /api/admin/users`
- `GET /api/admin/users/{id}`
- `POST /api/admin/users`
- `PUT /api/admin/users/{id}`
- `DELETE /api/admin/users/{id}`

### Products
- `POST /api/admin/products`
- `PUT /api/admin/products/{id}`
- `DELETE /api/admin/products/{id}`

### Categories
- `POST /api/admin/categories`
- `PUT /api/admin/categories/{id}`
- `DELETE /api/admin/categories/{id}`

### Coupons (CRUD)
- `GET /api/admin/coupons`
- `GET /api/admin/coupons/{id}`
- `POST /api/admin/coupons`
- `PUT /api/admin/coupons/{id}`
- `DELETE /api/admin/coupons/{id}`

### News (CRUD)
- `GET /api/admin/news`
- `POST /api/admin/news`
- `PUT /api/admin/news/{id}`
- `DELETE /api/admin/news/{id}`

### About (CRUD)
- `GET /api/admin/about`
- `POST /api/admin/about`
- `PUT /api/admin/about/{id}`
- `DELETE /api/admin/about/{id}`

## Environment (.env)
Project reads env from `.env` using:
- `spring.config.import=optional:file:.env[.properties]`

Required keys:
- `MONGO_URI`
- `MONGO_DB`
- `SERVER_PORT`
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `CORS_ALLOWED_ORIGINS`
- `CHECKOUT_RESULT_URL`
- `MOMO_ENDPOINT`
- `MOMO_PARTNER_CODE`
- `MOMO_ACCESS_KEY`
- `MOMO_SECRET_KEY`
- `MOMO_REDIRECT_URL`
- `MOMO_IPN_URL`
- `VNPAY_PAY_URL`
- `VNPAY_TMN_CODE`
- `VNPAY_HASH_SECRET`
- `VNPAY_RETURN_URL`
