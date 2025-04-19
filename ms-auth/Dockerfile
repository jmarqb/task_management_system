FROM openjdk:21-jdk-slim
LABEL authors="jmarqb"

ARG JAR_FILE=target/ms-auth-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} ms-auth.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","/ms-auth.jar"]