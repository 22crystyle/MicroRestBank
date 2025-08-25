FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY services/eureka-server/build/libs/*.jar eureka-server.jar
EXPOSE 8761
CMD ["java", "-jar", "eureka-server.jar"]