# Step 1: Build jar menggunakan Maven
FROM maven:3.9.6-eclipse-temurin-17 as build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -Dquarkus.package.type=uber-jar -DskipTests

# Step 2: Jalankan jar dengan JDK
FROM eclipse-temurin:17-jdk
WORKDIR /work/
COPY --from=build /app/target/*-runner.jar app.jar
EXPOSE 9002
CMD ["java", "-jar", "app.jar"]
