FROM openjdk:8-jre-alpine

RUN apk update && apk add wget
ENV DOCKERIZE_VERSION v0.5.0
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

ADD ./target/turbine-server-*.jar /app/turbine-server.jar
CMD dockerize -wait $REGISTRY_URL/apps/CONFIG-SERVICE -timeout 120s ; java $JAVA_OPTS -jar /app/turbine-server.jar --REGISTRY_SERVER_URI=$REGISTRY_URL

EXPOSE 29501 8989