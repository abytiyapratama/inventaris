FROM quay.io/quarkus/quarkus-maven:3.2.5 AS build
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN ./mvnw clean package -Dquarkus.package.type=fast-jar -DskipTests

FROM registry.access.redhat.com/ubi9/openjdk-21:1.21
WORKDIR /deployments
COPY --from=build /usr/src/app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /usr/src/app/target/quarkus-app/app/ /deployments/app/
COPY --from=build /usr/src/app/target/quarkus-app/quarkus/ /deployments/quarkus/
COPY --from=build /usr/src/app/target/quarkus-app/*.jar /deployments/
EXPOSE 8080
CMD ["java", "-jar", "/deployments/quarkus-run.jar"]
