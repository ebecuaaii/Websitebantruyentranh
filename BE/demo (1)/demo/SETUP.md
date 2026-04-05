# Comic Store – Backend Setup Guide

## Yêu cầu
- Java 21
- Maven
- MongoDB Atlas account

---

## Cấu hình môi trường

Project đã hỗ trợ đọc cấu hình trực tiếp từ file `.env` qua:
`spring.config.import=optional:file:.env[.properties]`

### Bước 1: Tạo file `.env`
Copy từ `.env.example`:

```bash
cp .env.example .env
```

### Bước 2: Điền giá trị thật vào `.env`
Ví dụ:
```env
MONGO_URI=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/?retryWrites=true&w=majority&appName=<appName>
MONGO_DB=comic_store
SERVER_PORT=8080
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5500,http://127.0.0.1:5500
```

> Liên hệ team lead để lấy `<username>` và `<password>` MongoDB Atlas.

---

## Chạy project

```bash
mvn spring-boot:run
```

Test API: `GET http://localhost:8080/api/test`

---

## Phân công Backend

| Module | Người phụ trách | Chức năng |
|--------|----------------|-----------|
| Authentication & User | Thuận | Đăng ký, đăng nhập, tài khoản cá nhân, quản lý user, phân quyền |
| Product & Category | Thành | Danh sách/chi tiết sản phẩm, quản lý sản phẩm & danh mục |
| Cart & Order | Tiên | Giỏ hàng, yêu thích, quản lý đơn hàng, cập nhật trạng thái |
| Checkout & Coupon | Phúc | Thanh toán, đặt hàng, quản lý coupon & mã giảm giá |
| Dashboard & Monitoring | Trúc | Đơn hàng cá nhân, dashboard admin, thống kê doanh thu |
| Content & Search | Tiến | Trang chủ, tin tức, tìm kiếm & lọc sản phẩm |

---

## Cấu trúc package

```
com.example.demo
├── controller
├── service
│   └── impl
├── repository
├── entity
├── dto
│   ├── request
│   └── response
├── config
├── security
├── exception
└── util
```
