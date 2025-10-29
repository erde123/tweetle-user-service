FROM openjdk:17-alpine

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8083

ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app.jar"]