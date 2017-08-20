FROM java:8-jre

ADD ./target/migration-utility-*-jar-with-dependencies.jar /app/migration-utility.jar
CMD sleep 10; java -Xmx200m -jar /app/migration-utility.jar postgres postgres ddfds283nsdjahs devModeEnabled