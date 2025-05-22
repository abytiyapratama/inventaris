# Tahap build
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build
COPY . /build

RUN chmod +x ./mvnw && ./mvnw clean package -Dquarkus.package.type=fast-jar -DskipTests

# Tahap runtime
FROM eclipse-temurin:21-jdk
WORKDIR /deployments
COPY --from=build /build/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /build/target/quarkus-app/app/ /deployments/app/
COPY --from=build /build/target/quarkus-app/quarkus/ /deployments/quarkus/
COPY --from=build /build/target/quarkus-app/*.jar /deployments/
EXPOSE 8080
CMD ["java", "-jar", "/deployments/quarkus-run.jar"]
