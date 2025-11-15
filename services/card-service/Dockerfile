FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN mkdir /opt/app
COPY services/card-service/build/libs/*.jar /opt/app/card-service.jar
CMD ["java", "-jar", "/opt/app/card-service.jar"]