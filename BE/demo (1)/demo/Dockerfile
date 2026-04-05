# Dùng JRE nhẹ để chạy app (JAR đã được build bởi CI trước đó)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR từ target/ (context = BE/demo (1)/demo)
COPY target/*.jar app.jar

EXPOSE 8080

ENV MONGO_URI=""
ENV JWT_SECRET="default-secret-key-change-in-production"
ENV JWT_EXPIRATION=86400000

ENTRYPOINT ["java", "-jar", "app.jar"]
