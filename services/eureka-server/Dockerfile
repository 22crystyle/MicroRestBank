FROM eclipse-temurin:21-jre
RUN apt-get update  \
    && apt-get install -y curl  \
    && rm -rf /var/lib/apt/lists/*  \
    && mkdir /opt/app
COPY services/eureka-server/build/libs/*.jar /opt/app/eureka-server.jar
CMD ["java", "-jar", "/opt/app/eureka-server.jar"]