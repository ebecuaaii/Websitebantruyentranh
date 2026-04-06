FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# context = BE/demo_clean (symlink tới BE/demo (1)/demo)
COPY target/*.jar app.jar

EXPOSE 8080

ENV MONGO_URI=""
ENV JWT_SECRET="default-secret-key-change-in-production"
ENV JWT_EXPIRATION=86400000

ENTRYPOINT ["java", "-jar", "app.jar"]
