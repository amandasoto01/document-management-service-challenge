FROM maven:3.8.8-amazoncorretto-17 AS builder

# define space to work
WORKDIR /app

# Copy .pom.xml to get dependences first
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy code
COPY . .

# contruct spring image
RUN mvn clean package -DskipTests


FROM amazoncorretto:17-alpine-jdk

# Set working directory
WORKDIR /app

# Copy local jar to container
COPY --from=builder /app/target/document-management-service-challenge-0.0.1-SNAPSHOT-LOCAL.jar /app/management-service.jar

# Expose port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "management-service.jar"]