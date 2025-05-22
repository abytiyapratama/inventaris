# Gunakan base image Java 17
FROM eclipse-temurin:17-jdk

# Buat direktori kerja
WORKDIR /work/

# Copy file hasil build dari Maven
COPY target/crud-gudang-1.0.0-SNAPSHOT.jar app.jar

# Expose port sesuai konfigurasi Quarkus (default 8080 atau 9002)
EXPOSE 9002

# Jalankan aplikasi
CMD ["java", "-jar", "app.jar"]
