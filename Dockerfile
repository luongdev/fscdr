ARG WORK_DIR="/usr/src/fs-cdr/src"

#FROM maven:3.8.3-openjdk-17-slim AS build
#COPY src $WORK_DIR/src
#COPY pom.xml $WORK_DIR
#RUN mvn -f  $WORK_DIR/pom.xml clean install


FROM openjdk:17-slim

ARG CONFIG_DIR="/opt/fscdr"

COPY target/*.jar /usr/local/lib/app.jar

#COPY --from=build $WORK_DIR/*.jar /usr/local/lib/app.jar
COPY resources ${CONFIG_DIR}

ENV JVM_OPTS "--spring.config.location=file://${CONFIG_DIR}/application.properties"

ENTRYPOINT [ "sh", "-c", "java -jar /usr/local/lib/app.jar ${JVM_OPTS}" ]