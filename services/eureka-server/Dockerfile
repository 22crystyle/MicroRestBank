FROM openjdk:21-jdk-slim
WORKDIR /app
COPY services/eureka-server/build/libs/*.jar eureka-server.jar
EXPOSE 8761
CMD ["java", "-jar", "eureka-server.jar"]