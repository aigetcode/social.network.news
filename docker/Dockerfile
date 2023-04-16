FROM openjdk:17

RUN mkdir /app
COPY news-service.jar /app
WORKDIR /app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "news-service.jar"]
