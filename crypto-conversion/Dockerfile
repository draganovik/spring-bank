# Use the official Maven image to build the JAR
FROM maven:3.8.5-openjdk-17-slim AS build
LABEL authors="draganovik"
# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . .

# Build the Spring Boot JAR
RUN mvn package

# Use the official OpenJDK base image
FROM openjdk:17-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port that your Spring Boot application uses
EXPOSE 8500

# Define the entry point to run your Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]