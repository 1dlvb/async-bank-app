FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/async-bank-app-0.0.1-SNAPSHOT.jar /app/async-bank-app.jar

ENTRYPOINT ["java", "-jar", "/app/async-bank-app.jar"]
