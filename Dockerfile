ARG WORK_DIR="/usr/src/fs-cdr/src"

#FROM maven:3.8.3-openjdk-17-slim AS build
#COPY src $WORK_DIR/src
#COPY pom.xml $WORK_DIR
#RUN mvn -f  $WORK_DIR/pom.xml clean install


FROM openjdk:17-slim

ARG CONFIG_DIR="/opt/fscdr"
ENV TZ=Asia/Ho_Chi_Minh
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY ./build/libs/*.jar $HOME/app.jar

ENV APP_KAFKA_ENABLED "true"
ENV KAFKA_BOOTSTRAP_SERVER "localhost:9092"
ENV KAFKA_INSTANCE_ID "FS-CDR"
ENV KAFKA_SECURITY_PROTOCOL "PLAINTEXT"
ENV KAFKA_SECURITY_MECHANISM "PLAIN"
ENV KAFKA_SECURITY_LOGIN_MODULE ""
ENV KAFKA_SECURITY_USERNAME ""
ENV KAFKA_SECURITY_PASSWORD ""

ENV SCHEMA_REGISTRY_URL "http://localhost:8081"
ENV SCHEMA_REGISTRY_CRED_SOURCE "USER_INFO"
ENV SCHEMA_REGISTRY_USER ""
ENV SCHEMA_REGISTRY_PASSWORD ""

ENV MONGO_USER "root"
ENV MONGO_PASSWD ""
ENV MONGO_DBNAME "json_cdr"
ENV MONGO_PORT 27017
ENV MONGO_HOST "localhost"
ENV MONGO_AUTHDB "admin"

ENV APP_JSON_CDR_TOPIC "json_cdr"
ENV APP_JSON_CDR_DIR "/var/data/cdr"
ENV APP_JSON_CDR_IMPORT_CRON "0/5 * * * * *"
ENV APP_JSON_CDR_REMOVE_AFTER_IMPORT "true"

COPY target/*.jar /usr/local/lib/app.jar

COPY resources ${CONFIG_DIR}

ENV JVM_OPTS "--spring.config.location=file://${CONFIG_DIR}/application.properties"

ENTRYPOINT [ "sh", "-c", "java -jar /usr/local/lib/app.jar ${JVM_OPTS}" ]