FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY services/auth-service/build/libs/*.jar auth-service.jar
EXPOSE 1024
CMD ["java", "-jar", "auth-service.jar"]