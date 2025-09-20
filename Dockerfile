# Use the official Maven image as the base image
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Sao chép pom.xml từ thư mục hiện tại (./src) vào /app
COPY pom.xml /app

# Sao chép thư mục src (bên trong ./src) vào /app/src
COPY src /app/src

# Build the application
RUN mvn clean package -Dmaven.test.skip=true

# Create a new image for running the application
FROM openjdk:17-jdk

# Set the working directory
WORKDIR /app

# Copy the built JAR file từ stage build
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]