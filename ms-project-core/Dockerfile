FROM openjdk:21-jdk-slim
LABEL authors="jmarqb"

ARG JAR_FILE=target/ms-project-core-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} ms-project-core.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","/ms-project-core.jar"]