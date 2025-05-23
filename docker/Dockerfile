FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY ../target/RestBank-1.jar app.jar

EXPOSE 1024

ENTRYPOINT ["java", "-jar", "app.jar"]