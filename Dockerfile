# ==========================
# 🧱 Stage 1: Build với Maven + JDK 21
# ==========================
FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven config
COPY pom.xml .
COPY src ./src

# Build application (bỏ test)
RUN mvn clean package -DskipTests

# ==========================
# 🚀 Stage 2: Runtime nhẹ hơn
# ==========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy file JAR đã build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
