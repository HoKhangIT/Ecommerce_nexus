# Stage 1: Build with Maven and JDK 21
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with lightweight JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ENV TZ=UTC
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
