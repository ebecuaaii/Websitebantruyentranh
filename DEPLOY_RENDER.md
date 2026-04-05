# Hướng dẫn Deploy lên Render

## Vấn đề gặp phải

Ứng dụng đang cố kết nối MongoDB tại `localhost:27017` trên Render, nhưng không có MongoDB service nào chạy ở đó.

## Giải pháp thực hiện

### 1. Sửa Dockerfile

✅ Đã cập nhật `/BE/demo (1)/demo/Dockerfile` với multi-stage build để:

- Build Maven project trong Docker
- Không phụ thuộc vào `target/` folder có sẵn

### 2. Cấu hình Environment Variables

Khi deploy lên Render, cần set các biến môi trường sau:

```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your-long-random-secret-key-here
CORS_ORIGINS=https://your-frontend-domain.onrender.com
```

### 3. Deploy trên Render

#### Cách 1: Sử dụng Docker (Recommended)

1. Push code lên GitHub
2. Trên Render:
   - Tạo New Service → Web Service
   - Connect GitHub repository
   - Build Command: `docker build -t image-name -f BE/demo\ \(1\)/demo/Dockerfile BE/demo\ \(1\)/demo`
   - Start Command: `java -jar app.jar`
   - Open shell và set environment variables:
     ```
     SPRING_PROFILES_ACTIVE=prod
     JWT_SECRET=your-secret-here
     CORS_ORIGINS=https://your-domain.onrender.com
     ```

#### Cách 2: Deploy Spring Boot trực tiếp

1. Trên Render, chọn Web Service
2. Build Command: `cd BE/demo\ \(1\)/demo && ./mvnw clean package -DskipTests`
3. Start Command: `java -jar /opt/render/project/src/BE/demo\ \(1\)/demo/target/demo-0.0.1-SNAPSHOT.jar`
4. Environment Variables:
   ```
   SPRING_PROFILES_ACTIVE=prod
   JWT_SECRET=your-secret-here
   ```

### 4. Kiểm tra kết nối MongoDB Atlas

- MongoDB URI đã được cấu hình trong `application.properties`:
  ```
  spring.data.mongodb.uri=mongodb+srv://trucuser:Truc123@webbantruyen.2amdk17.mongodb.net/?retryWrites=true&w=majority&appName=webbantruyen
  ```
- ✅ Credentials này sẽ được sử dụng tự động từ `application.properties`

### 5. Fix CORS cho Production

- Update `application.properties` hoặc `application-prod.properties`
- Đặt `spring.web.cors.allowed-origins` đúng domain frontend của bạn trên Render

### 6. Xử lý lỗi startup

✅ Đã cập nhật `NewsSeeder.java` để:

- Bắt exception khi database không khả dụng
- Log lỗi thay vì làm crash application
- Retry seeding lần sau

## Các bước kiểm tra sau khi deploy

1. **Xem logs trên Render:**
   - Vào Web Service → Logs
   - Tìm message: "News seeding completed successfully"

2. **Test API endpoint:**

   ```bash
   curl https://your-backend.onrender.com/api/news
   ```

3. **Kiểm tra Swagger UI:**
   ```
   https://your-backend.onrender.com/swagger-ui.html
   ```

## Nếu còn gặp lỗi

1. **MongoDB connection timeout:**
   - Kiểm tra MongoDB URI có đúng không
   - Check whitelist IP trên MongoDB Atlas (thêm 0.0.0.0/0)

2. **CORS error:**
   - Cập nhật `CORS_ORIGINS` environment variable
   - Hoặc update `spring.web.cors.allowed-origins` trong properties file

3. **Port issues:**
   - Render tự động gán PORT, không cần set cứng
   - Spring Boot sẽ listen trên port được gán

4. **Build fails:**
   - Kiểm tra Java/Maven version compatibility
   - Ensure `pom.xml` có đầy đủ dependencies

## Tóm tắt các file đã sửa

- ✅ `BE/demo (1)/demo/Dockerfile` - Multi-stage build
- ✅ `BE/demo (1)/demo/src/main/resources/application-prod.properties` - Production config
- ✅ `BE/demo (1)/demo/src/main/java/com/example/demo/config/NewsSeeder.java` - Error handling
