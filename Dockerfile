FROM maven:3.8.3-openjdk-17-slim AS build
#COPY src /usr/src/
#COPY pom.xml /usr/src/
#COPY app /usr/src/
#COPY resources /usr/src/
COPY . /usr/src/

RUN mvn -f /usr/src/pom.xml clean install -P prod

FROM nginx:alpine

RUN apk add openjdk17

ARG CONFIG_DIR="/opt/fscdr"
ENV TZ=Asia/Ho_Chi_Minh
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV APP_KAFKA_ENABLED "true"
ENV KAFKA_BOOTSTRAP_SERVER "localhost:9092"
ENV KAFKA_INSTANCE_ID "FS-CDR"
ENV KAFKA_SECURITY_PROTOCOL "PLAINTEXT"
ENV KAFKA_SECURITY_MECHANISM "PLAIN"
ENV KAFKA_SECURITY_LOGIN_MODULE "org.apache.kafka.common.security.plain.PlainLoginModule"
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

ENV BASE_API_URL "http://localhost:8080"

COPY --from=build /usr/src/target/*.jar /usr/local/app.jar
COPY --from=build /usr/src/target/classes/static /usr/share/nginx/html
COPY --from=build /usr/src/target/classes/application.properties ${CONFIG_DIR}/
COPY --from=build /usr/src/target/classes/application-dev.properties ${CONFIG_DIR}/
COPY --from=build /usr/src/nginx.conf /usr/src/nginx.conf

COPY docker-entrypoint.sh /

RUN chmod +x docker-entrypoint.sh

RUN envsubst '${BASE_API_URL}' < /usr/src/nginx.conf > /etc/nginx/conf.d/default.conf

EXPOSE 80
EXPOSE 8080

ENV JVM_OPTS "--spring.config.location=file://${CONFIG_DIR}/application.properties"

VOLUME ["/var/data/cdr", "/opt/fscdr"]

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["java", "-jar", "/usr/local/app.jar", "${JVM_OPTS}", "--spring.profiles.active=dev"]