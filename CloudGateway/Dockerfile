FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} cloudgateway.jar

ENTRYPOINT ["java","-jar","/cloudgateway.jar"]

ENTRYPOINT 9090