FROM eclipse-temurin:21-jre
RUN apt-get update  \
    && apt-get install -y curl  \
    && rm -rf /var/lib/apt/lists/*  \
    && mkdir /opt/app
COPY services/customer-service/build/libs/*.jar /opt/app/customer-service.jar
CMD ["java", "-jar", "/opt/app/customer-service.jar"]