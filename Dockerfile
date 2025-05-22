# Step 1: Build jar menggunakan Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Step 2: Jalankan jar dengan JDK
FROM eclipse-temurin:17-jdk
WORKDIR /work/
COPY --from=build /app/target/crud-gudang-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 9002
CMD ["java", "-jar", "app.jar"]
