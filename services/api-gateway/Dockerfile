FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY services/api-gateway/build/libs/*.jar api-gateway.jar
EXPOSE 1024
CMD ["java", "-jar", "api-gateway.jar"]