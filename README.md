# Deploy Java Web App to Tomcat9 on Azure App Service on Linux 


## Overview of this Application

This Java Web Application is not Microservices Application but standard Java Web Application which is wrote by using Java EE 7 technologies.  

At first, you can select and expand the Continent (North America, Asia, Africa, Europe, South America, Oceania, Antarctica) in the left side of the menu, then you can see the countries in the Continent. Then you can select the one country, then you can see the cities where has the number of the population over 1,000,000 in right side of the screen like follows.  
All of the data is coming from Managed [Azure Database for MySQL](https://docs.microsoft.com/azure/mysql/?WT.mc_id=docs-github-yoterada).


![](./images/screenshot.jpg)


## Runtime selection of Azure App Service on Linux for Java

In order to confirm which Java Runtime we can use on [Azure App Service on Linux](https://docs.microsoft.com/azure/app-service/containers/app-service-linux-intro?WT.mc_id=docs-github-yoterada), you can execute the following command.  

`az webapp list-runtimes --linux`

```azurecli
az webapp list-runtimes --linux 
  "TOMCAT|8.5-jre8",
  "TOMCAT|9.0-jre8",
  "JAVA|8-jre8",
  "WILDFLY|14-jre8",
  "TOMCAT|8.5-java11",
  "TOMCAT|9.0-java11",
  "JAVA|11-java11",
```

For example, if you select the `JAVA`, you can deploy and run the Executable JAR file like Spring Boot or MicroProfile and it will run on Docker Container. And if you select the `TOMCAT` or `WILDFLY`, you can deploy and run the `***.war` file and it will run on Linux. You can also select the Java Runtime between Java 8 or Java 11.  

In this application, we will use the `TOMACT 9.0` and `Java11`.


## More Detail Runtime Environment

This appication will run on Tomcat 9 with JDK 11. In this application, I used following technologies.

* JDK 11
* Servlet (4.0.1)
* JavaServer Faces (2.4)
* PrimeFaces (8.0)
* JSP Standard Tag Library-JSTL (1.2)
* Weld-CDI (2.4.8.Final)
* Mysql Connector (8.0.21)
* Java Persistence API-JPA (2.2)
* EcliseLink (2.7.7)
* Bean Validation (1.1.0.Final)
* Jakarta JSON-B (1.0.2)

On Azure Web App Tomcat on Linux environment, following JDK (Zulu OpenJDK) is installed.  

```bash
# java -version
openjdk version "11.0.3" 2019-04-16 LTS
OpenJDK Runtime Environment 19.4-(Zulu-11.31+11-linux-musl-x64)-Microsoft-Azure-restricted (build 11.0.3+7-LTS)
OpenJDK 64-Bit Server VM 19.4-(Zulu-11.31+11-linux-musl-x64)-Microsoft-Azure-restricted (build 11.0.3+7-LTS, mixed mode)
```
[Java long-term support and medium-term support on Azure and Azure Stack](https://docs.microsoft.com/azure/developer/java/fundamentals/java-jdk-long-term-support?WT.mc_id=docs-github-yoterada)

## Setup MySQL Server before creating the Java Web App

In order to run this application, you need to install and configure the [Azure Database for MySQL ](https://docs.microsoft.com/azure/mysql/?WT.mc_id=docs-github-yoterada) before the deploy.
In order to install and create the MySQL, please refer to the following documents?  

[Create DB for MySQL on Azure](https://github.com/yoshioterada/microprofile-samples/blob/master/MySQL/Azure-MySQL-Setup-For-Sample-App.md) for preparation of this Java Web App.


## Create Maven Project for Java Web App

```bash
mvn archetype:generate -DarchetypeGroupId=de.rieckpil.archetypes \
    -DarchetypeArtifactId=javaee8-jsf \
    -DarchetypeVersion=1.0.1 \
	-DgroupId=com.microsoft.azure.samples \
     -Dpackage=com.microsoft.azure.samples \
     -Dversion=0.0.1-SNAPSHOT \
	-DartifactId=azure-javaweb-app \
	-Darchetype.interactive=false \
	--batch-mode \
```

```bash  
ls
azure-javaweb-app
$ cd azure-javaweb-app/
$ ls
pom.xml	src
```

```text
├── Dockerfile (delete)
├── buildAndRun.bat (delete)
├── buildAndRun.sh (delete)
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── sample
    │   │       └── SampleBean.java
    │   ├── resources
    │   │   └── META-INF
    │   │       ├── microprofile-config.properties (delete)
    │   │       └── persistence.xml
    │   └── webapp
    │       ├── WEB-INF
    │       │   ├── beans.xml 
    │       │   ├── payara-web.xml  (delete)
    │       │   └── web.xml
    │       └── index.xhtml
    └── test
        └── java
```

The above directory structure will be automatically created and it is created to run on Payara on Docker. However we will deploy the Web Application to Tomcat 9 not Docker in this time. So please delete some files?

## Modify the pom.xml file

```
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">  
    <modelVersion>4.0.0</modelVersion>  
    <groupId>com.microsoft.azure.samples</groupId>  
    <artifactId>azure-javaweb-app</artifactId>  
    <version>0.0.1-SNAPSHOT</version>  
    <packaging>war</packaging>  
    <properties> 
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding> 
    </properties>  
    <dependencies> 
        <dependency> 
            <groupId>javax.servlet</groupId>  
            <artifactId>javax.servlet-api</artifactId>  
            <version>4.0.1</version>  
            <scope>provided</scope> 
        </dependency>  
        <dependency> 
            <groupId>org.glassfish</groupId>  
            <artifactId>javax.faces</artifactId>  
            <version>2.4.0</version> 
        </dependency>  
        <dependency> 
            <groupId>javax.faces</groupId>  
            <artifactId>javax.faces-api</artifactId>  
            <version>2.3</version> 
        </dependency>  
        <!-- https://mvnrepository.com/artifact/org.primefaces/primefaces -->  
        <dependency> 
            <groupId>org.primefaces</groupId>  
            <artifactId>primefaces</artifactId>  
            <version>8.0</version> 
        </dependency> 
        <dependency> 
            <groupId>jstl</groupId>  
            <artifactId>jstl</artifactId>  
            <version>1.2</version> 
        </dependency>  
        <dependency> 
            <groupId>org.jboss.weld.servlet</groupId>  
            <artifactId>weld-servlet</artifactId>  
            <version>2.4.8.Final</version> 
        </dependency>  
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.21</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api -->
        <dependency>
            <groupId>javax.persistence</groupId>
            <artifactId>javax.persistence-api</artifactId>
            <version>2.2</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/javax.validation/validation-api -->
        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
            <version>2.0.1.Final</version>
        </dependency>

        <dependency>
            <groupId>org.eclipse.persistence</groupId>
            <artifactId>eclipselink</artifactId>
            <version>2.7.7</version>
        </dependency>
        <dependency>
            <groupId>javax.transaction</groupId>
            <artifactId>javax.transaction-api</artifactId>
            <version>1.3</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/jakarta.json.bind/jakarta.json.bind-api -->
        <dependency>
            <groupId>jakarta.json.bind</groupId>
            <artifactId>jakarta.json.bind-api</artifactId>
            <version>1.0.2</version>
        </dependency>

    </dependencies>  
    <!-- Plugin Manven e Java -->  
    <build> 
        <finalName>azure-javaweb-app</finalName>  
        <plugins> 
            <plugin> 
                <artifactId>maven-compiler-plugin</artifactId>  
                <version>3.3</version>  
                <configuration> 
                    <source>11</source>  
                    <target>11</target>  
                    <downloadJavadocs>true</downloadJavadocs> 
                </configuration> 
            </plugin>  
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>2.3</version>
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>
        </plugins> 
    </build> 
</project>

```

## Modify web.xml file

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="4.0"
         xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd">
    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <context-param>
        <param-name>primefaces.THEME</param-name>
        <param-value>nova-light</param-value>
    </context-param>

    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.xhtml</url-pattern>
    </servlet-mapping>

    <welcome-file-list>
        <welcome-file>index.xhtml</welcome-file>
    </welcome-file-list>

    <context-param>
        <param-name>javax.faces.PROJECT_STAGE</param-name>
        <param-value>Development</param-value>
    </context-param>
</web-app>
```

## Configure the Maven plugin for Azure App Service

In order to deploy the Java Web App to Azure App Service, if you configure the [Maven plugin for Azure App Service](https://docs.microsoft.com/java/api/overview/azure/maven/azure-webapp-maven-plugin/readme?view=azure-java-stable&WT.mc_id=docs-github-yoterada), you can create the Azure Resources and deploy the Web App with very easily. 
Please execute the following command?

```azurecli
mvn com.microsoft.azure:azure-webapp-maven-plugin:1.9.1:config
```

If you execute the command, it will show following question. If you answer all of the qeustion, the plugin will automatically added the configuration to deploy to Azure.

```azurecli
$ mvn com.microsoft.azure:azure-webapp-maven-plugin:1.9.1:config
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------< com.microsoft.azure.samples:azure-javaweb-app >------------
[INFO] Building azure-javaweb-app 0.0.1-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO] 
[INFO] --- azure-webapp-maven-plugin:1.9.1:config (default-cli) @ azure-javaweb-app ---

Define value for OS(Default: Linux): 
1. linux [*]
2. windows
3. docker
Enter index to use: 1
Define value for javaVersion(Default: Java 8): 
1. Java 11
2. Java 8 [*]
Enter index to use: 1
Define value for runtimeStack(Default: TOMCAT 8.5): 
1. TOMCAT 9.0
2. TOMCAT 8.5 [*]
Enter index to use: 1
Please confirm webapp properties
AppName : azure-javaweb-app-1595955014168
ResourceGroup : azure-javaweb-app-1595955014168-rg
Region : westeurope
PricingTier : PremiumV2_P1v2
OS : Linux
RuntimeStack : TOMCAT 9.0-java11
Deploy to slot : false
Confirm (Y/N)? : Y
[INFO] Saving configuration to pom.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:31 min
[INFO] Finished at: 2020-07-29T01:51:38+09:00
[INFO] ------------------------------------------------------------------------
```

After finished the command, you will be able to see following additional configuration was added to the `pom.xml` file.

```
      <plugin>
        <groupId>com.microsoft.azure</groupId>
        <artifactId>azure-webapp-maven-plugin</artifactId>
        <version>1.9.1</version>
        <configuration>
          <schemaVersion>V2</schemaVersion>
          <resourceGroup>azure-javaweb-app-1595955014168-rg</resourceGroup>
          <appName>azure-javaweb-app-1595955014168</appName>
          <pricingTier>P1v2</pricingTier>
          <region>westeurope</region>
          <runtime>
            <os>linux</os>
            <javaVersion>java11</javaVersion>
            <webContainer>TOMCAT 9.0</webContainer>
          </runtime>
          <deployment>
            <resources>
              <resource>
                <directory>${project.basedir}/target</directory>
                <includes>
                  <include>*.war</include>
                </includes>
              </resource>
            </resources>
          </deployment>
        </configuration>
      </plugin>
```


## Deploy Java Web App to Azure App Service

-------------------
TODO: Execute Again for this
-------------------

```
https://azure-javaweb-app.azurewebsites.net/
```



### If you configure the `Deployment Slot`, it will be following result

If you configure the `Deployment Slot`, following XML will be added to the above XML definition.

```xml
          <deploymentSlot>
            <name>staging-slot</name>
            <configurationSource></configurationSource>
          </deploymentSlot>
```

And If you executed the `mvn azure-webapp:deploy` command, new `Deployment Slot` will be automatically created and deploy the Web App to it.

```bash
$ mvn azure-webapp:deploy
[INFO] Target Deployment Slot doesn't exist. Creating a new one...
[INFO] Creating a new deployment slot and copying configuration from parent...
[INFO] Successfully created the Deployment Slot.
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource to /private/tmp/TMP/azure-javaweb-app/target/azure-webapp/azure-javaweb-app-8c4fb3f9-d8e5-4d62-85ab-8ffe6248387f
[INFO] Trying to deploy artifact to staging-slot...
[INFO] Deploying the war file azure-javaweb-app.war...
[INFO] Successfully deployed the artifact to https://azure-javaweb-app-staging-slot.azurewebsites.net
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:46 min
[INFO] Finished at: 2020-07-29T02:01:11+09:00
[INFO] ------------------------------------------------------------------------
```



## Final Directory Structure of this Project

```text
.
├── README.md
├── images
│   └── screenshot.jpg
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── microsoft
    │   │           └── azure
    │   │               └── samples
    │   │                   ├── IndexBackingBean.java (JSF Backing Bean)
    │   │                   ├── entities
    │   │                   │   ├── City.java (JPA Entity class)
    │   │                   │   └── Country.java (JPA Entity class)
    │   │                   ├── services
    │   │                   │   └── CityService.java (Business Logic Service)
    │   │                   └── utils
    │   │                       ├── ConfigurationBean.java (JSF Configuration)
    │   │                       └── EntityManagerUtil.java (JPA Utilitiy Class)
    │   └── webapp
    │       ├── META-INF
    │       │   └── context.xml (Tomcat context)
    │       ├── WEB-INF
    │       │   ├── beans.xml (CDI configuration)
    │       │   ├── classes
    │       │   │   └── META-INF
    │       │   │       └── persistence.xml (JPA Configuration)
    │       │   ├── faces-config.xml (JSF Configuration)
    │       │   └── web.xml (Web Application Configuration)
    │       └── index.xhtml (Top Page of this Application)
    └── test
        └── java
```



## MySQL Settings in the Web Application


### Configure the environment variable values in Application settings

```azurecli
 az webapp config appsettings set \
     --resource-group WebApp \
     --name yoshiowebapp \
     --settings JDBC_DRIVER="com.mysql.cj.jdbc.Driver"
```

```azurecli
 az webapp config appsettings set \
     --resource-group WebApp \
     --name yoshiowebapp \
     --settings JDBC_URL="jdbc:mysql://my-mysqlserver.mysql.database.azure.com:3306/world?
       useSSL=true&requireSSL=false&serverTimezone=JST"
```

```azurecli
 az webapp config appsettings set \
     --resource-group WebApp \
     --name yoshiowebapp \
     --settings DB_USER="USER"
```

```azurecli
 az webapp config appsettings set \
     --resource-group WebApp \
     --name yoshiowebapp \
     --settings DB_PASSWORD="PASSWORD"
```

### Configure in the persistence.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1" xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    <persistence-unit name="JPAWorldDatasourcePU" transaction-type="RESOURCE_LOCAL">
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
        <class>com.microsoft.azure.samples.entities.City</class>
        <class>com.microsoft.azure.samples.entities.Country</class>
        <exclude-unlisted-classes>true</exclude-unlisted-classes>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:mysql://my-mysqlserver.mysql.database.azure.com:3306/world?useSSL=true&amp;requireSSL=false&amp;serverTimezone=JST"/>
            <property name="javax.persistence.jdbc.user" value="USER"/>
            <property name="javax.persistence.jdbc.password" value="PASSWORD"/> 

            <property name="eclipselink.cache.shared.default" value="false" />
<!--            <property name="eclipselink.ddl-generation" value="create-tables" /> 
            <property name="eclipselink.ddl-generation.output-mode"
                      value="database" /> -->
            <property name="eclipselink.logging.level" value="SEVERE" />
        </properties>
    </persistence-unit>
</persistence>

```

***Note:***  
In the *jdbc.url* you need to escape the "&" as "&amp";


