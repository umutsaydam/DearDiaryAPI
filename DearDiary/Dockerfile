FROM openjdk:17-jdk-slim

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app.jar"]

EXPOSE 8080