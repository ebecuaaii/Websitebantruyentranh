# ── Stage 1: Build ──────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY demo\ \(1\)/demo/pom.xml .
RUN mvn dependency:go-offline -B

COPY "demo (1)/demo/src" ./src

RUN mvn clean package -DskipTests

# ── Stage 2: Run ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV MONGO_URI=""
ENV JWT_SECRET="default-secret-key-change-in-production"
ENV JWT_EXPIRATION=86400000

ENTRYPOINT ["java", "-jar", "app.jar"]
