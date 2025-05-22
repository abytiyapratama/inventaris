# Step 1: Build jar menggunakan Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Copy konfigurasi Maven terlebih dahulu agar dependency bisa di-cache
COPY pom.xml ./
COPY mvnw ./
COPY .mvn/ .mvn/

# 2. Pre-cache dependencies (agar cepat build dan error tidak terjadi)
RUN ./mvnw dependency:go-offline

# 3. Setelah dependencies ter-cache, copy semua source project
COPY src ./src

# 4. Jalankan build Quarkus dengan format uber-jar
RUN ./mvnw clean package -Dquarkus.package.type=uber-jar -DskipTests

# Step 2: Jalankan jar dengan JDK runtime yang lebih ringan
FROM eclipse-temurin:17-jdk
WORKDIR /work/
COPY --from=build /app/target/*-runner.jar app.jar
EXPOSE 9002
CMD ["java", "-jar", "app.jar"]
