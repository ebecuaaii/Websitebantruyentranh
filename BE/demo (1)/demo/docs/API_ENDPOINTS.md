# API Endpoints - Backend Day 1

Base URL: `http://localhost:8080`

## Public Endpoints

### Health Check
- `GET /api/test`

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`

### Categories
- `GET /api/categories`
- `GET /api/categories/{id}`
- `GET /api/categories/{id}/products`

### Products
- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/products/{id}/reviews`

### News
- `GET /api/news`
- `GET /api/news/{id}`
- `GET /api/news/slug/{slug}`

Sample detail response format:
```json
{
  "status": "success",
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
