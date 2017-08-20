FROM openjdk:8-jre-alpine

RUN apk update && apk add wget
ENV DOCKERIZE_VERSION v0.5.0
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

ADD ./config/src/main/resources/cloud-configs/* /app-configs/
ADD ./global/config-server/target/config-server-*.jar /app/config-server.jar
CMD dockerize -wait $REGISTRY_URL/apps/ -timeout 120s ; java -Xmx50m -jar /app/config-server.jar --REGISTRY_SERVER_URI=$REGISTRY_URL --CONFIG_FILES_ROOT_LOC=/app-configs/

EXPOSE 8888