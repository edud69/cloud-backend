[![CircleCI](https://circleci.com/gh/edud69/cloud-backend/tree/master.svg?style=svg)](https://circleci.com/gh/edud69/cloud-backend/tree/master)

# How to start
## Install Java Cryptography Extension (JCE) Unlimited Strength (used for OAuth2 Social Login strong encryption)
1. Download the Java JCE from oracle : [Oracle](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).
2. Extract the files in *JDK_HOME/jre/lib/security* or *JRE_HOME/lib/security*.

## Setting up the Project in an IDE ##

### In Eclipse ###

Recommended plugins to install:

* EclEmma: Code Coverage
* MoreUnit: Helps to generate unit test code
* Pmd-plugin: Code analysis checks
* Findbugs: Helps to find potential bugs
* AnyEditTool: Export / Import working sets
* Jautodoc: Generates javadoc
* Checkstyle: Code format
* Spring-Tool-Suite: will drastically improve the debugging and startup of applications 

### In IntelliJ IDEA ###

Note: Emma is installed by default on IntelliJ IDEA

1. Install CheckStyle-IDEA
2. Install PMDPlugin
3. Install Findbugs-IDEA
4. Install the [Lombok plug-in by Michail Plushnikov](https://github.com/mplushnikov/lombok-intellij-plugin)
4. Import CheckStyle XML config (Failed! TODO: resolve issue)
5. Create the following databases in PostgreSQL 9.5 (postgres/postgres):
    * authentication_master
    * document_master
    * account_master
6. Clone the backend repository somewhere, and open in IntelliJ IDEA
7. Create a new Maven configuration, to build all the projects:
    * **Name**: Build (ALL)
    * **Working directory**: `<your backend root folder>`
    * **Command line**: `clean install`
8. Run the new Maven configuration "Build (ALL)"
9. Create a new Application Configuration, to migrate the database:
    * **Name**: Database migration
    * **Main class**: `io.theshire.migration.Launcher`
    * **Program arguments**: `postgres postgres ddfds283nsdjahs (optional : devModeEnabled -> this option will execute dev scripts to setup dev env)`
    * **Working directory**: `<your backend root folder>\migration-utility`
    * **Use classpath of module**: `migration-utility`
10. Run the Application Configuration "Database migration"
11. Follow the procedure described in the SQL scripts:
    * `<your backend root folder>\config\dev_configs\dev_sql_scripts\init_scripts.sql`
12. Create 4 new Maven configurations:
    * Registration server:
        * **Name**: Registration server
        * **Working directory**: `<your backend root folder>\global\registry-server`
        * **Command line**: `spring-boot:run`
    * Configuration server:
        * **Name**: Configuration server
        * **Working directory**: `<your backend root folder>\global\config-server`
        * **Command line**: `spring-boot:run`
    * Gateway server:
        * **Name**: Gateway server
        * **Working directory**: `<your backend root folder>\global\gateway-server`
        * **Command line**: `spring-boot:run`
    * Authorization server:
        * **Name**: Authorization server
        * **Working directory**: `<your backend root folder>\global\authorization-server`
        * **Command line**: `spring-boot:run`
13. Launch the 4 new Maven configurations in the following order:
    * Registration server
    * Configuration server
    * Gateway server
    * Authorization server

# Properties to be defined as args before launching the servers (.JAR)  (JVM arg -DtheProperty)

- Use `--` as a prefix in front of any JVM argument instead of '-D', spring application will convert it to an argument. Ex: `--server.port=9000` or `--PORT=9000`
- To launch an application on linux, use the following command: 'java -Djava.security.egd=file:/dev/./urandom -jar an-application-server.jar [--server.port=2222 --anotherArg=anothervalue ...]

### For all servers ###

spring.profiles.active = activates configuration files for a specific profile (default : dev)

`REGISTRY_SERVER_URI` = uri of the registry server (default : http://localhost:1111/eureka/).


### Micro-Services servers Properties ###

`PORT` = the server port (Can be defined on any server).


### Config-Server Properties ###

`CONFIG_FILES_ROOT_LOC` = location of config server files (classpath: ..., file:///..., etc.) (can only be defined on the config-server)


# Docker
The project is configured supports Docker containers. You can launch the whole project by doing so:
1) Install Docker on your host machine

2) Increase the Docker VM memory and CPU (for windows right-click on Docker icon in the tray area and click settings, then go on Advanced).

3) Build the project with maven.

4) In a cmd prompt use `docker-compose build` (this will build all docker images including the infrastructure such as SQL, Message broker, etc.)

5) Launch the whole cloud with `docker-compose up`

- RabbitMQ management : http://localhost:15672
- Sql management : http://localhost:8081
- Rest-api : http://localhost:8080

To debug a micro-service while other instances are running in Docker, you need to do the following steps:

1) Open the host file (Windows is C:\Windows\System32\drivers\etc\hosts)

2) Open the `docker-compose.yml` file at the root of the source and check for the `hostname` field, the value will be the one that you need to make a redirect to 127.0.0.1.

3) Add new entries so that the eureka instances are mapped to your localhost (normally you only need the config-service and auth-service).
    To get the host name you can open http://localhost:1111/ and mouse over the micro-service instance, the link should give you the name.
    The entry in host should look like that `127.0.0.1 docker-config`
    
4) You can run the command `docker-composer up -d`, it will start the containers in background, then you can stop the service you want to debug `docker-compose stop your-docker-container`

5) Start the micro-service you want to debug from your IDE.

6) Some services needs to speak with others, so if you want to debug a micro-service from your machine and your environment is in docker. You need to edit the local `src/main/resources/bootstrap.yml` file and provide two new entries:
   The first one is `eureka.instance.preferIpAddress: true` and the second one is `eureka.instance.ipAddress: THE_IP_OF_YOUR_LOCALHOST_MACHING_ON_THE_DOCKER_BRIDGE_INTERFACE`. Docker by default creates a network bridge interface between the containers and your host,
   adding the ip to the configuration file will provide the registry server the good ip to contact from other services.
   
Building production images:

1) In the root of the source run `docker-compose --build`

2) Make sure that the `docker-compose.prod.yml` file is copied to the prod machine.

3) Export the builded images with `docker save theshire > /some/path/to/an/output.tar`.

4) Copy the file to the production machine.

5) On the machine that has the source code run the following commands in a BASH window (GIT bash if on Windows) :
```
#!bash
  # Delete every Docker containers
  # Must be run first because images are attached to containers
  docker rm -f $(docker ps -a -q)

  # Delete every Docker image
  docker rmi -f $(docker images -q)
```

6) On the production machine run `docker load -i /path/to/the/copied/output.tar`

7) On the production machine run `docker-compose -f /path/to/docker-compose.prod.yml up -d`
    


# Useful links (without Docker)

- Service registries : http(s)://the-service-registry-server:port/ (default : http://localhost:1111/)
- Rest-Api URL: (defaults on http://localhost:8080/)
- Rest-Api Hystrix dashboard: http(s)://the-rest-api-server:port/hystrix (default : http://localhost:8080/hystrix), to monitor queries on the rest-api use this link in the dashboard http(s)://the-rest-api-server:port/hystrix.stream
- Monitor servers status: http(s)://a-local-server:port/health
- Administrative panel: http://localhost:14900/ or http://the-rest-api-server:theport/


# Adding a new micro-service

1) Create projects

2) Copy a launcher class from another service and put it in the ***-server project. Change the line of code to match this property 'spring.config.name' (the service name).

3) Copy bootstrap.properties and  **.yml (rename this yml with the same name as declared in step 2) in the src/main/resources classpath.

4) Edit the .yml file and edit the spring.application.name to the new service name.

5) In the config-server project add an entry in application.properties (src/main/resources/cloud-configs) app.cloud.service.YOUR_SERVICE.serviceName.

6) Add a new file next to the application.properties (step 5) with the name of your service (this is the properties for your service).

7) In the file created from step 4, make sure you add a section with MQ-BINDINGS (check other service config-file as example).

8) Add the new service-id to the *turbine.appConfig* field in the *admin-server.yml* from admin-server project (*src/main/resources*).

9) Implement and give *@Component* and *@Primary* (spring-managed) annotation to the a class on your new project (YOURSERVICE-server project) that extends -> *TenantInitializerDefault* class and *TenantDestroyerDefault* class.

10) Add the new Dockerfile

11) Configure the docker-compose.yml file with the new project.

# Database structure
-Each micro-services that needs SQL should be structured as followed:

1. A database for the master tenant and all shared configurations (should be *servicename_master*).

2. A database for tenant creation. It uses this template database when creating a new tenant (should be *servicename_template*).

3. Several databases for tenant schema (tenant data), each of these database can contain a maximum amount of tenants (they are named *servicename_slaveXXX).



# Running the migration utility

- Create a simple java application launcher on the main of the Launcher class of migration-utility project.
- Provide the following arguments `mainDbUsername mainDbPassword dbDataEncryptionKey (optional: devModeEnabled)` (by default for dev use: `postgres postgres ddfds283nsdjahs (optional : devModeEnabled -> this option will execute dev scripts to setup dev env)`).


# Dev environment

-Look at the config-files project /dev-configs folder for configuration files (sql init script, checkstyle file, maven file, etc.).

# Websocket backend #
Add project *common-websocket*

## Monitoring websocket client connection / disconnects ##
Create a bean that implments *WebsocketSessionListener* class from *common-websocket* and events will be automatically triggered inside your class

## Destination construction ##
When subscribing or sending message to a MQ destination the following prefix rules are applied:

- Sending a message that are destination-prefixed with */app* on client-side will go through server otherwise it will be forwarded directly to message broker.

- Subscribing to a queue from client-side should look like */user/queue/tenant.TENANTID-QUEUENAME*

- Subscribing to a queue from client-side should look like */topic/tenant.tenant.TENANTID-TOPICNAME*

## Send a message to a queue or topic from backend **
1. Inject the stomp service.
```
#!java
  @Autowired
  private StompMessagingTemplate stompMessagingTemplate;
```

2. Create a tenant-aware route (topic or queue).

```
#!java

 
  private static final StompMessageDestination USER_CHAT_QUEUE_DESTINATION =
      new StompMessageDestination("/queue/tenant.?-chat");

 
  private static final StompMessageDestination CHAT_TOPIC_DESTINATION_PREFIX =
      new StompMessageDestination("/topic/tenant.?-chat");
```

3. Send the message to a particular user or to a general topic.

```
#!java
    // user queue
    stompMessagingTemplate.sendToUser(username, USER_CHAT_QUEUE_DESTINATION, message);

    // general topic
    final StompMessageDestination destination = new StompMessageDestination(CHAT_TOPIC_DESTINATION_PREFIX.getDestination() + "-" + chatMsg.getChannelName());
    stompMessagingTemplate.send(destination, message);
```

# Creating a rest endpoint #
1. Create a *@RestController* and extend *ManagedRestEndpoint* class.
2. Make sure you call the method *buildResponse()* from *ManagedRestEndpoint* if you want to send a message to the client.
3. You should always send an object that extends *TransportMessage* (*io.theshire.common.utils.transport.message*).
  


# JPA #
----------------------------------------------------------------------

-To add a multi-tenant supported JPA repository, make sure to add the suffix *JpaRepository.java


```
#!java

public interface AccountJpaRepository extends JpaSpringRepository<Account>
```


-To add a single-tenant (will connect to the service default database) JPA repository, make sure to add the suffix *JpaSingleTenantRepository.java


```
#!java

public interface UserAuthenticationJpaSingleTenantRepository extends JpaSpringRepository<UserAuthentication>
```

-To inject an entity manager with the multitenancy suppport, do the following:
```
#!java
	@PersistenceContext(unitName = JpaPersistenceUnitConstants.MULTI_TENANT_PERSISTENCE_CONTEXT)
	private EntityManager em;
```


-To inject an entity manager with the multitenancy suppport, do the following:
```
#!java
	@PersistenceContext(unitName = JpaPersistenceUnitConstants.SINGLE_TENANT_PERSISTENCE_CONTEXT)
	private EntityManager em;
```

# Keys to change outside dev env. #

## application.properties ##

Remove (if present):

```
#!properties

eureka.instance.leaseRenewalIntervalInSeconds=2
eureka.client.registryFetchIntervalSeconds=2
ribbon.serverListRefreshInterval=2
```

Change:


```
#!properties

app.cloud.security.oauth.client.internal.secret=dfs3@@Df21%!ssA3
app.cloud.auth.specialUser.user.sub.password=98m!23i9i9sda%kd&
spring.mail.host=mailtrap.io
spring.mail.port=2525
spring.mail.username=7c2050445fbd32
spring.mail.password=6072f06968d8a9
spring.mail.properties.mail.smtp.auth=true
```


## auth-service.properties ##

Change:

```
#!properties


app.cloud.security.oauth.jwt.privateKey.password=kjas3!gh4?213J
app.cloud.security.oauth.jwt.privateKey.keystore.password=kjas3!gh4?213J
app.cloud.security.oauth.encryptor.password=3j4@#$x!!!43s
app.cloud.security.oauth.redirectUrl=http://localhost:84/
app.cloud.security.oauth.applicationUrl=http://localhost:8080/api/auth/
app.cloud.security.oauth.client.public.web.allowedOrigins=*
spring.social.facebook.appId=618582951561087
spring.social.facebook.appSecret=ea3841adadc84dc26a9c1f4f2b63f86c
```


Make sure all SQL and elasticsearch configs are up to date.

# Cloud architecture overview
![backend-architecture-high-lvl.png](https://bitbucket.org/repo/bX8krX/images/3911591992-backend-architecture-high-lvl.png)

# Micro-Service architecture overview
![backendmicroservice.png](https://bitbucket.org/repo/bX8krX/images/4215674392-backendmicroservice.png)
