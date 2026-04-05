# ── Stage 1: Build ──────────────────────────────────────
# Dùng image Maven + JDK 21 để build project
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file pom.xml trước để cache dependencies (tối ưu build time)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code
COPY src ./src

# Build project, bỏ qua test để nhanh hơn
RUN mvn clean package -DskipTests

# ── Stage 2: Run ─────────────────────────────────────────
# Dùng image JRE nhẹ hơn để chạy (không cần Maven nữa)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy file JAR từ stage build
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Biến môi trường (override khi chạy container)
ENV MONGO_URI=""
ENV JWT_SECRET="default-secret-key-change-in-production"
ENV JWT_EXPIRATION=86400000

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
