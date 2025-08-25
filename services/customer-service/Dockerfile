FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY services/customer-service/build/libs/*.jar customer-service.jar
EXPOSE 1024
CMD ["java", "-jar", "customer-service.jar"]