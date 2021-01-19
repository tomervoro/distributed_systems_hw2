FROM gradle:6.7-jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:8-jre-slim

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/ds-hw2.jar

ENTRYPOINT ["java", "-jar", "/app/ds-hw2.jar"]