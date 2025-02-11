# Stage 1: Build (Temporary)
FROM maven:3.9.9-amazoncorretto-17 AS build
WORKDIR /app
# Copy only the necessary files to cache dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B
# Copy the rest of the project files and build the JAR
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime (Final Image)
FROM amazoncorretto:17-alpine
WORKDIR /app
# Add metadata labels
LABEL authors="draganovik"
# Copy the packaged JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar
# Expose the port the application will run on
EXPOSE 8400
# Define the entry point
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]