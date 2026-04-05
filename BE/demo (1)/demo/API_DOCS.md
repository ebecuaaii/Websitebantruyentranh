# Comic Store – API Docs

Base URL: `http://localhost:8080/api`

Auth dùng JWT Bearer Token: `Authorization: Bearer <token>`

---

## 2.1 Auth & User – Thuận

### Auth
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| POST | `/auth/register` | ❌ | Đăng ký tài khoản |
| POST | `/auth/login` | ❌ | Đăng nhập, trả về JWT |
| GET | `/auth/me` | ✅ | Lấy thông tin cá nhân |
| PUT | `/auth/me` | ✅ | Cập nhật thông tin cá nhân |

### Admin – User Management
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/admin/users` | ✅ ADMIN | Danh sách tất cả user |
| GET | `/admin/users/{id}` | ✅ ADMIN | Chi tiết user |
| POST | `/admin/users` | ✅ ADMIN | Tạo user mới |
| PUT | `/admin/users/{id}` | ✅ ADMIN | Cập nhật user |
| DELETE | `/admin/users/{id}` | ✅ ADMIN | Xóa user |
| PUT | `/admin/users/{id}/role` | ✅ ADMIN | Phân quyền User/Admin |

---

## 2.2 Product & Category – Thành

### Product (Public)
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/products` | ❌ | Danh sách sản phẩm (có phân trang) |
| GET | `/products/{id}` | ❌ | Chi tiết sản phẩm |
| GET | `/products/{id}/reviews` | ❌ | Đánh giá của sản phẩm |
| POST | `/products/{id}/reviews` | ✅ | Thêm đánh giá |

### Category (Public)
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/categories` | ❌ | Danh sách danh mục |
| GET | `/categories/{id}/products` | ❌ | Sản phẩm theo danh mục |

### Admin – Product & Category
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| POST | `/admin/products` | ✅ ADMIN | Thêm sản phẩm |
| PUT | `/admin/products/{id}` | ✅ ADMIN | Sửa sản phẩm |
| DELETE | `/admin/products/{id}` | ✅ ADMIN | Xóa sản phẩm |
| POST | `/admin/categories` | ✅ ADMIN | Thêm danh mục |
| PUT | `/admin/categories/{id}` | ✅ ADMIN | Sửa danh mục |
| DELETE | `/admin/categories/{id}` | ✅ ADMIN | Xóa danh mục |

---

## 2.3 Cart & Order – Tiên

### Cart
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/cart` | ✅ | Lấy giỏ hàng |
| POST | `/cart` | ✅ | Thêm sản phẩm vào giỏ |
| PUT | `/cart/{productId}` | ✅ | Cập nhật số lượng |
| DELETE | `/cart/{productId}` | ✅ | Xóa sản phẩm khỏi giỏ |
| DELETE | `/cart` | ✅ | Xóa toàn bộ giỏ hàng |

### Wishlist
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/wishlist` | ✅ | Danh sách yêu thích |
| POST | `/wishlist/{productId}` | ✅ | Thêm vào yêu thích |
| DELETE | `/wishlist/{productId}` | ✅ | Xóa khỏi yêu thích |

### Admin – Order
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/admin/orders` | ✅ ADMIN | Danh sách tất cả đơn hàng |
| GET | `/admin/orders/{id}` | ✅ ADMIN | Chi tiết đơn hàng |
| PUT | `/admin/orders/{id}/status` | ✅ ADMIN | Cập nhật trạng thái đơn |

---

## 2.4 Checkout & Coupon – Phúc

### Checkout
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| POST | `/orders/checkout` | ✅ | Đặt hàng |
| GET | `/orders/{id}` | ✅ | Thông tin đơn hàng |
| POST | `/orders/apply-coupon` | ✅ | Áp dụng mã giảm giá |

### Admin – Coupon
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/admin/coupons` | ✅ ADMIN | Danh sách coupon |
| POST | `/admin/coupons` | ✅ ADMIN | Tạo coupon |
| PUT | `/admin/coupons/{id}` | ✅ ADMIN | Sửa coupon |
| DELETE | `/admin/coupons/{id}` | ✅ ADMIN | Xóa coupon |

---

## 2.5 Dashboard & Order Monitoring – Trúc

### Khách hàng
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/orders` | ✅ | Danh sách đơn hàng cá nhân |
| GET | `/orders/{id}` | ✅ | Chi tiết đơn hàng cá nhân |

### Admin – Dashboard
| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/admin/dashboard` | ✅ ADMIN | Thống kê tổng quan |
| GET | `/admin/dashboard/revenue` | ✅ ADMIN | Thống kê doanh thu |
| GET | `/admin/dashboard/orders` | ✅ ADMIN | Thống kê số lượng đơn hàng |

---

## 2.6 Content & Search – Tiến

| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/content/home` | ❌ | Dữ liệu trang chủ |
| GET | `/content/about` | ❌ | Trang giới thiệu |
| GET | `/news` | ❌ | Danh sách tin tức |
| GET | `/news/{id}` | ❌ | Chi tiết tin tức |
| GET | `/products/search?q=` | ❌ | Tìm kiếm sản phẩm |
| GET | `/products?category=&minPrice=&maxPrice=&sort=` | ❌ | Lọc sản phẩm |

---

## FE Setup – Vanilla HTML

Team FE dùng **Live Server** để có hot reload khi code HTML/CSS/JS.

**VSCode:**
1. Cài extension `Live Server` (Ritwick Dey)
2. Chuột phải vào file `index.html` > `Open with Live Server`
3. Mặc định chạy ở `http://127.0.0.1:5500`

**Hoặc dùng npm:**
```bash
npm install -g live-server
live-server FE/
```

**CORS:** Vì FE chạy ở port khác, cần update `application.properties` BE:
```properties
spring.web.cors.allowed-origins=http://127.0.0.1:5500
```

Hoặc cho phép tất cả khi dev:
```properties
spring.web.cors.allowed-origins=*
```

**Gọi API từ HTML:**
```js
const BASE_URL = 'http://localhost:8080/api'

// Không cần auth
fetch(`${BASE_URL}/products`)
  .then(res => res.json())
  .then(data => console.log(data))

// Cần auth (kèm JWT)
fetch(`${BASE_URL}/auth/me`, {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  }
})
  .then(res => res.json())
  .then(data => console.log(data))
```

---

## Response Format

Tất cả API trả về format thống nhất:

```json
{
  "status": 200,
  "message": "Success",
  "data": { }
}
```

## Order Status

`PENDING` → `CONFIRMED` → `SHIPPING` → `DELIVERED` → `CANCELLED`
