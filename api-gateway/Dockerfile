FROM openjdk:21-jdk-slim
LABEL authors="jmarqb"

ARG JAR_FILE=target/api-gateway-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} api-gateway.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/api-gateway.jar"]