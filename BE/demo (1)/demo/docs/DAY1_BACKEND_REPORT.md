# Day 1 Backend Report (2026-03-31)

## Mục tiêu hôm nay
- Rà soát backend hiện tại.
- Bổ sung API cho trang tin tức (news).
- Bổ sung API cho trang giới thiệu (about).
- Mở rộng CRUD cho admin để quản trị nội dung.
- Chuẩn hóa chạy bằng `.env`.

## Đã hoàn thành

### 1) Rà soát toàn bộ endpoint BE hiện có
- Kiểm tra mapping controller + security rules.
- Xác định public endpoints, endpoints cần đăng nhập, endpoints admin.

### 2) Xây dựng module News
- Thêm entity/repository/service/controller cho news.
- Thêm endpoint public:
  - `GET /api/news`
  - `GET /api/news/{id}`
  - `GET /api/news/slug/{slug}`
- Response detail dùng format `status: "success"` + `data`.
- Hỗ trợ field `published_at`, `related_books` theo snake_case.
- Seeder dữ liệu mẫu news id `101`.

### 3) Xây dựng module About
- Thêm entity/repository/service/controller cho about.
- Thêm endpoint public:
  - `GET /api/about`
- Seeder dữ liệu giới thiệu mẫu.

### 4) Thêm CRUD admin cho Content
- News admin:
  - `GET /api/admin/news`
  - `POST /api/admin/news`
  - `PUT /api/admin/news/{id}`
  - `DELETE /api/admin/news/{id}`
- About admin:
  - `GET /api/admin/about`
  - `POST /api/admin/about`
  - `PUT /api/admin/about/{id}`
  - `DELETE /api/admin/about/{id}`

### 5) Bảo mật và cấu hình
- Public GET được mở cho `/api/news/**` và `/api/about/**`.
- Cập nhật `.env.example`, `application-example.properties`, `SETUP.md` để chạy bằng `.env`.
- Cập nhật `.gitignore` để bỏ qua file nhạy cảm:
  - `.env`
  - `.env.*`
  - giữ lại `.env.example`
  - `src/main/resources/application.properties`

## Trạng thái kiểm thử
- Chưa chạy được compile/test trên máy hiện tại do Java runtime chưa hỗ trợ `release 21`.

## Gợi ý ngày tiếp theo
- Thêm endpoint `trang chủ` (home aggregate API).
- Thêm endpoint `tìm kiếm sản phẩm` theo keyword + pagination.
- Viết integration tests cho news/about/admin content APIs.
