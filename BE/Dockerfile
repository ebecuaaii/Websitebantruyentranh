FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# context = ./BE, copy JAR từ subfolder
COPY demo\ \(1\)/demo/target/*.jar app.jar

EXPOSE 8080

ENV MONGO_URI=""
ENV JWT_SECRET="default-secret-key-change-in-production"
ENV JWT_EXPIRATION=86400000

ENTRYPOINT ["java", "-jar", "app.jar"]
