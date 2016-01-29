PROPERTIES BEFORE LAUNCHING A SERVER (.JAR)  (JVM arg -DtheProperty)
---------------------------------------------------------------------
PORT = the server port
CONFIG_SERVER_URI = uri of the config server (http://config-server:port/)
REGISTRY_SERVER_URI = uri of the registry server (default : http://localhost:1111/eureka/)
CONFIG_FILE_LOC = location of config server files (classpath: ..., file:///..., etc.)


USEFUL URL
----------------------------------------------------------------------
-Service registries : http(s)://the-service-registry-server:port/ (default : http://localhost:1111/)
-Hystrix dashboard : http(s)://the-rest-api-gateway:port/hystrix (default: http://localhost:2222/hystrix)


LAUNCH SEQUENCE
----------------------------------------------------------------------
1) Registry server
2) Config server
3) Gateway server
4) Any micro-service server


ADD A MICRO-SERVICE
----------------------------------------------------------------------
1) Create projects
2) Copy a launcher class from another service and put it in the ***-server project. Change the line of code to match this property 'spring.config.name' (the service name).
3) Copy bootstrap.properties and  **.yml (rename this yml with the same name as declared in step 2) in the src/main/resources classpath.
4) Edit the .yml file and edit the spring.application.name to the new service name.
5) In the config-server project add an entry in application.properties (src/main/resources/cloud-configs) app.cloud.service.YOUR_SERVICE.serviceName.
6) Add a new file next to the application.properties (step 5) with the name of your service (this is the properties for your service).
