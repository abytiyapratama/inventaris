FROM registry.access.redhat.com/ubi9/openjdk-21:1.21

WORKDIR /deployments

COPY target/quarkus-app/lib/ /deployments/lib/
COPY target/quarkus-app/app/ /deployments/app/
COPY target/quarkus-app/quarkus/ /deployments/quarkus/
COPY target/quarkus-app/*.jar /deployments/

EXPOSE 8080

CMD ["java", "-jar", "/deployments/quarkus-run.jar"]
