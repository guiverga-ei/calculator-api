# Use Eclipse Temurin's OpenJDK 17 base image with Alpine Linux for minimal footprint
FROM eclipse-temurin:17-jdk-alpine

# Define the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the host into the container image
COPY target/rest-0.0.1-SNAPSHOT.jar app.jar

# Define the default command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
