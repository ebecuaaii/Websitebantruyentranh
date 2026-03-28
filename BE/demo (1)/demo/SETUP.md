# Comic Store – Backend Setup Guide

## Yêu cầu
- Java 21
- Maven
- MongoDB Atlas account

---

## Cấu hình môi trường

Project dùng biến môi trường để bảo mật thông tin kết nối. `application.properties` **không được push lên git**.

### Bước 1: Tạo file `application.properties`
Tạo file tại `src/main/resources/application.properties` với nội dung sau:

```properties
spring.application.name=comic-store

# MongoDB Atlas
spring.data.mongodb.uri=${MONGO_URI}
spring.data.mongodb.database=${MONGO_DB:comic_store}

# Server
server.port=8080

# JWT
jwt.secret=${JWT_SECRET:your-secret-key-here-make-it-long-and-secure}
jwt.expiration=${JWT_EXPIRATION:86400000}

# CORS
spring.web.cors.allowed-origins=http://localhost:3000
```

### Bước 2: Set biến môi trường

**IntelliJ IDEA:**
`Run > Edit Configurations > Environment variables`, thêm:
```
MONGO_URI=mongodb+srv://<username>:<password>@webbantruyen.2amdk17.mongodb.net/?retryWrites=true&w=majority&appName=webbantruyen
MONGO_DB=comic_store
JWT_SECRET=your-secret-key
```

**Windows CMD:**
```cmd
set MONGO_URI=mongodb+srv://<username>:<password>@webbantruyen.2amdk17.mongodb.net/...
set MONGO_DB=comic_store
set JWT_SECRET=your-secret-key
```

**macOS/Linux:**
```bash
export MONGO_URI=mongodb+srv://<username>:<password>@webbantruyen.2amdk17.mongodb.net/...
export MONGO_DB=comic_store
export JWT_SECRET=your-secret-key
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
