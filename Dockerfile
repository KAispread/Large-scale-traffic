FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./service/article/build/libs/article-*.jar app.jar 

EXPOSE 8080 

ENTRYPOINT ["java", "-jar", "app.jar"]
