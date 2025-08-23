FROM openjdk:21-jdk-slim
WORKDIR /app
COPY services/customer-service/build/libs/*.jar customer-service.jar
EXPOSE 1024
CMD ["java", "-jar", "customer-service.jar"]