FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY ../build/libs/RestBank-1.0.0-plain.jar app.jar

EXPOSE 1024

ENTRYPOINT ["java", "-jar", "app.jar"]