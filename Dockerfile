FROM openjdk:8-jre-alpine

MAINTAINER Sami Bourouis <sami.bourouis.pro@gmail.com>

LABEL collect_logs_with_filebeat="true" \
      decode_log_event_to_json_object="true"

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
