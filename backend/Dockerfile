# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# JAVA_OPTS allows runtime JVM tuning (e.g. Render sets -Xms256m -Xmx512m)
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:+$JAVA_OPTS} -jar /app/app.jar"]
