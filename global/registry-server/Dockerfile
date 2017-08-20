FROM openjdk:8-jre-alpine

ADD ./target/registry-server-*.jar /app/registry-server.jar
CMD java $JAVA_OPTS -jar /app/registry-server.jar --REGISTRY_SERVER_URI=$REGISTRY_URL

EXPOSE 1111