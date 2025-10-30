FROM gradle:8.8-jdk21

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8003

ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app.jar"]