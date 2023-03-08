FROM gradle:jdk11-alpine AS build
ENV HOME=/app
WORKDIR $HOME

COPY . .
RUN ["gradle", "bootJar"]

#
# Package stage
#
FROM gradle:jdk11-alpine
ENV HOME=/app
WORKDIR $HOME

ENV TZ=Asia/Ho_Chi_Minh
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY --from=build $HOME/build/libs/*.jar $HOME/app.jar

# Run app from jar file
ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080
