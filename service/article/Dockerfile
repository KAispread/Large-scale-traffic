FROM amazoncorretto:17-alpine

COPY build/libs/article.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "/app.jar"]