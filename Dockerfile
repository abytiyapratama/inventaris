# Step 1: Build jar using Maven Wrapper
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven wrapper, configuration, and pom.xml
COPY pom.xml ./
COPY mvnw ./
COPY .mvn/ .mvn/

# Pre-cache dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy source code
COPY src/ ./src/

# Build Quarkus as uber-jar
RUN ./mvnw clean package -Dquarkus.package.type=uber-jar -DskipTests

# Step 2: Run app using JDK base
FROM eclipse-temurin:17-jdk
WORKDIR /work/
COPY --from=build /app/target/*-runner.jar app.jar

EXPOSE 9002
CMD ["java", "-jar", "app.jar"]
