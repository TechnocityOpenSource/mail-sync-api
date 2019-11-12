FROM openjdk:8-jre-alpine

MAINTAINER Sami Bourouis <sami.bourouis.pro@gmail.com>

ARG SPRING_PROFILES_ACTIVE=dev
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
ENV JAVA_OPTS=""

VOLUME /tmp

ADD target/*.jar app.jar

ENTRYPOINT exec java $JAVA_OPTS \
 -Djava.security.egd=file:/dev/./urandom \
 -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
 -noverify -XX:TieredStopAtLevel=1 \
 -jar app.jar

EXPOSE 80
