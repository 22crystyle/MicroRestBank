FROM openjdk:21-jdk-slim
WORKDIR /app
COPY services/api-gateway/build/libs/*.jar api-gateway.jar
EXPOSE 1024
CMD ["java", "-jar", "api-gateway.jar"]