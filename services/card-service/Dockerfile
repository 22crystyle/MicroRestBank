FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY services/card-service/build/libs/*.jar card-service.jar
EXPOSE 1024
CMD ["java", "-jar", "card-service.jar"]