[![CircleCI](https://circleci.com/gh/edud69/cloud-backend/tree/master.svg?style=svg)](https://circleci.com/gh/edud69/cloud-backend/tree/master)

# Introduction

This project is a cloud infrastructure based on the [spring-cloud](https://github.com/spring-cloud) projects.

# Contributing

Please see the [CONTRIBUTING](https://github.com/edud69/cloud-backend/blob/master/.github/CONTRIBUTING.md) file for guidelines.

# Submitting an issue or feature request

Please see the [ISSUES](https://github.com/edud69/cloud-backend/blob/master/.github/ISSUE_TEMPLATE.md) file for guidelines.

# Table of Contents

- [Introduction](#introduction)
- [Contributing](#contributing)
- [Submitting an issue or feature request](#submitting-an-issue-or-feature-request)
- [How to start](#how-to-start)
- [Environment](#environment)
- [Docker](#docker)
  + [Debugging a service locally with a Docker setup running](#debugging-a-service-locally-with-a-docker-setup-running)
  + [Building production images](#building-production-images)
  
Coding tips:
- [Adding a new micro-service](#adding-a-new-micro-service)
- [Database migration](#database-migration)
  + [Running the migration utility](#running-the-migration-utility)
  + [Script for dev environment](#script-for-dev-environment)
  + [Add a new migration script](#add-a-new-migration-script)
  + [Editing the security matrix](#editing-the-security-matrix)
- [Websockets](#websockets)
  + [Adding websocket support](#adding-websocket-support)
  + [Monitoring websocket client connections](#monitoring-websocket-client-connections)
  + [Destinations and mapping to queues](#destinations-and-mapping-to-queues)
  + [Send a message to a queue or topic from backend](#send-a-message-to-a-queue-or-topic-from-backend)
- [Creating a rest endpoint](#creating-a-rest-endpoint)
- [JPA and multitenancy support](#jpa-and-multitenancy-support)

- [Architecture](#architecture)
  + [Spring-cloud architecture overview](#spring-cloud-architecture-overview)
  + [Micro-Service architecture overview](#micro-Service-architecture-overview)

Related repositories:
- [Frontend Sources](https://github.com/edud69/cloud-angular2-frontend)

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
5. Import CheckStyle XML config (Failed! TODO: resolve issue)
    
### Formatter file / checkstyle file and other configuration files for dev ###

-Look at the `config-files` project `/dev-configs` folder for configuration files (sql init script, checkstyle file, maven file, etc.).

Make sure you go over the [Defining the APIs keys](#defining-the-apis-keys) section.

# Environment
## Defining the APIs keys ##
1. Navigate the `config-files/src/main/resources/cloud-configs/PROFILE_IN_USE(dev by default)/`.
2. Search for all values with `**CHANGE_THIS**`.
3. Replace the values found in 2. by an account that can access the API.
```properties
  spring.mail.host=mailtrap.io
  spring.mail.port=2525
  spring.mail.username=**CHANGE_THIS**
  spring.mail.password=**CHANGE_THIS**
```

## Arguments that can be defined provided to .jar (JVM arg -DtheProperty) ##

- Use `--` as a prefix in front of any JVM argument instead of '-D', spring application will convert it to an argument. Ex: `--server.port=9000` or `--PORT=9000`
- To launch an application on linux, use the following command: 'java -Djava.security.egd=file:/dev/./urandom -jar an-application-server.jar [--server.port=2222 --anotherArg=anothervalue ...]

### Arguments for all instances ###

spring.profiles.active = activates configuration files for a specific profile (default : dev)
`REGISTRY_SERVER_URI` = uri of the registry server (default : http://localhost:1111/eureka/).
`PORT` = the server port (Can be defined on any server).

### Arguments for Config-Server ###

`CONFIG_FILES_ROOT_LOC` = location of config server files (classpath: ..., file:///..., etc.) (can only be defined on the config-server)


# Docker

The project is supports Docker containers. You can launch the whole project by doing so:
1) Install Docker on your host machine

2) Increase the Docker VM memory and CPU (for windows right-click on Docker icon in the tray area and click settings, then go on Advanced).

3) Build the project with maven.

4) In a cmd prompt use `docker-compose build` (this will build all docker images including the infrastructure such as SQL, Message broker, etc.)

5) Launch the whole cloud with `docker-compose up`

- RabbitMQ management : http://localhost:15672
- Sql management : http://localhost:8081
- Rest-api : http://localhost:8080

## Debugging a service locally with a Docker setup running ##

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
   
## Building production images ##

1) In the root of the source run `docker-compose --build`

2) Make sure that the `docker-compose.prod.yml` file is copied to the prod machine.

3) Export the builded images with `docker save theshire > /some/path/to/an/output.tar`.

4) Copy the file to the production machine.

5) On the machine that has the source code run the following commands in a BASH window (GIT bash if on Windows) :
```bash
  # Delete every Docker containers
  # Must be run first because images are attached to containers
  docker rm -f $(docker ps -a -q)

  # Delete every Docker image
  docker rmi -f $(docker images -q)
```

6) On the production machine run `docker load -i /path/to/the/copied/output.tar`

7) On the production machine run `docker-compose -f /path/to/docker-compose.prod.yml up -d`
    

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

10) Add the new Dockerfile to the project.

11) Configure the `docker-compose.yml` file with the new project.

12) Configure the `docker-compose.prod.yml` file.

# Database migration
## Running the migration utility ##

- Create a simple java application launcher on the main of the Launcher class of migration-utility project.
- Provide the following arguments `mainDbUsername mainDbPassword dbDataEncryptionKey (optional: devModeEnabled)` (by default for dev use: `postgres postgres ddfds283nsdjahs (optional : devModeEnabled -> this option will execute dev scripts to setup dev env)`).
* Here the value `ddfds283nsdjahs` must `match the app.cloud.security.database.encryption.key` from the `application-dev.properties` file (found in config-giles/src/main/resources/cloud-configs/).

## Script for dev environment ##
It is possible to execute scripts dedicated for dev environment. The migration-utility will execute the scripts only in devmode.
To add a new script go to  `migration-utility/src/main/resources/sql-dev-scripts/`. Add your script to an existing `.sql` file or add a new file and put your script there.

## Add a new migration script ##
To add a new migration SQL script, you can go to `migration-utility/src/main/resources/sql-scripts`.
You will find a list of folders there, each of them represents the script for the different components of the application.

If you go deeper in the directory structure:
```
.
├── admin-service/ <- scripts for the administrative panel
├── auth-service/ <- scripts for the authorization / authentication component
│   ├── configurations/  <- scripts for the configuration schema
│   ├── sso/  <- scripts for sso schema
│   └── template/   <- scripts applied to all tenant database and the template for tenant creation
├── micro-services/  <- docker-compose file for production environment
│   ├── micro-service-name/  <- scripts for the micro-service
│   |   ├── configurations/  <- scripts for the configuration schema
│   |   └── template/   <- scripts applied to all tenant database and the template for tenant creation
...
```

Adding a new script must follow the following convention: `YYYY.MM.DD_HH.MM__ScriptDescription.sql`.

## Editing the security matrix ##
If you want to change/add or remove a role/permission, you can browse the `migration-utility/src/main/resources/security/roles_permissions-mapper.csv` and edit the .csv file.

# Websockets #
## Adding websocket support ##
Add a reference to *common-websocket*.

## Monitoring websocket client connections ##
Create a bean that implments *WebsocketSessionListener* class from *common-websocket* and events will be automatically triggered inside your class

## Destinations and mapping to queues ##
When subscribing or sending message to a MQ destination the following prefix rules are applied:

- Sending a message that are destination-prefixed with */app* on client-side will go through server otherwise it will be forwarded directly to message broker.

- Subscribing to a queue from client-side should look like */user/queue/tenant.TENANTID-QUEUENAME*

- Subscribing to a queue from client-side should look like */topic/tenant.tenant.TENANTID-TOPICNAME*

## Send a message to a queue or topic from backend ##
1. Inject the stomp service.
```java
  @Autowired
  private StompMessagingTemplate stompMessagingTemplate;
```

2. Create a tenant-aware route (topic or queue).

```java
  private static final StompMessageDestination USER_CHAT_QUEUE_DESTINATION =
      new StompMessageDestination("/queue/tenant.?-chat");

 
  private static final StompMessageDestination CHAT_TOPIC_DESTINATION_PREFIX =
      new StompMessageDestination("/topic/tenant.?-chat");
```

3. Send the message to a particular user or to a general topic.

```java
    // user queue
    stompMessagingTemplate.sendToUser(username, USER_CHAT_QUEUE_DESTINATION, message);

    // general topic
    final StompMessageDestination destination = new StompMessageDestination(CHAT_TOPIC_DESTINATION_PREFIX.getDestination() + "-" + chatMsg.getChannelName());
    stompMessagingTemplate.send(destination, message);
```

# Creating a rest endpoint
1. Create a *@RestController* and extend *ManagedRestEndpoint* class.
2. Make sure you call the method *buildResponse()* from *ManagedRestEndpoint* if you want to send a message to the client.
3. You should always send an object that extends *TransportMessage* (*io.theshire.common.utils.transport.message*).
4. *buildResponse()* will handle the case where a *null* is provided, it will return a *HTTP 404*.
  

# JPA and multitenancy support

-To add a multi-tenant supported JPA repository, make sure to add the suffix *JpaRepository.java

```java

public interface AccountJpaRepository extends JpaSpringRepository<Account>
```


-To add a single-tenant (will connect to the service default database) JPA repository, make sure to add the suffix *JpaSingleTenantRepository.java


```java
public interface UserAuthenticationJpaSingleTenantRepository extends JpaSpringRepository<UserAuthentication>
```

-To inject an entity manager with the multitenancy suppport, do the following:
```java
	@PersistenceContext(unitName = JpaPersistenceUnitConstants.MULTI_TENANT_PERSISTENCE_CONTEXT)
	private EntityManager em;
```


-To inject an entity manager with the multitenancy suppport, do the following:
```java
	@PersistenceContext(unitName = JpaPersistenceUnitConstants.SINGLE_TENANT_PERSISTENCE_CONTEXT)
	private EntityManager em;
```

# Architecture
## Database structure ##
-Each micro-services that needs SQL should be structured as followed:

1. A database for the master tenant and all shared configurations (should be *servicename_master*).

2. A database for tenant creation. It uses this template database when creating a new tenant (should be *servicename_template*).

3. Several databases for tenant schema (tenant data), each of these database can contain a maximum amount of tenants (they are named *servicename_slaveXXX).

## Spring-cloud architecture overview ##
![backend-architecture-high-lvl.png](https://image.ibb.co/jfBQcQ/3911591992_backend_architecture_high_lvl.png)

## Micro-Service architecture overview ##
![backendmicroservice.png](https://image.ibb.co/bU6fBk/4215674392_backendmicroservice.png)
