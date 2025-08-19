FROM openjdk:21-jdk-slim
WORKDIR /app
COPY services/card-service/build/libs/*.jar card-service.jar
EXPOSE 1024
CMD ["java", "-jar", "card-service.jar"]