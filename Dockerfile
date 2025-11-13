FROM amazoncorretto:17-alpine-jdk

# Set working directory
WORKDIR /app

# Copy local jar to container
COPY target/document-management-service-challenge-0.0.1-SNAPSHOT-LOCAL.jar /app/management-service.jar

# Expose port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "management-service.jar"]