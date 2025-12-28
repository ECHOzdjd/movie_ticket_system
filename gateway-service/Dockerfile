FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/gateway-service-*.jar app.jar

EXPOSE 9999

ENTRYPOINT ["java", "-jar", "app.jar"]

