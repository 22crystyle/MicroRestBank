FROM openjdk:21-jdk-slim
WORKDIR /app
COPY services/auth-service/build/libs/*.jar auth-service.jar
EXPOSE 1024
CMD ["java", "-jar", "auth-service.jar"]