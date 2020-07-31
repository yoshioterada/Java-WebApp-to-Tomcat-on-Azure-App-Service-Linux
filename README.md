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
mvn archetype:generate \
    -DarchetypeGroupId=de.rieckpil.archetypes \
    -DarchetypeArtifactId=javaee8-jsf \
    -DarchetypeVersion=1.0.1 \
    -DgroupId=com.microsoft.azure.samples \
    -Dpackage=com.microsoft.azure.samples \
    -Dversion=0.0.1-SNAPSHOT \
    -DartifactId=azure-javaweb-app \
    -Darchetype.interactive=false \
    --batch-mode 
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

### Modify

```xml
		<maven.compiler.source>11</maven.compiler.source>
		<maven.compiler.target>11</maven.compiler.target>
```

### Delete

```xml
	<repositories>
		<repository>
			<id>prime-repo</id>
			<name>PrimeFaces Maven Repository</name>
			<url>http://repository.primefaces.org</url>
			<layout>default</layout>
		</repository>
	</repositories>
```

### Modify

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

### You can modify the name of Azure Resource Group and location

The above configuration will create and deploy to the following environment.

* Azure Resoure Group : `azure-javaweb-app-1595955014168-rg`
* Azure Resource Name : `azure-javaweb-app-1595955014168`
* Azure Resoruce Location : `westeurope`

If you would like to change the Resource Group Name, Resource Name and Location, you can change the configuration like follows.

```
        <configuration>
          <schemaVersion>V2</schemaVersion>
          <resourceGroup>azure-javaweb-app</resourceGroup>
          <appName>azure-javaweb-app</appName>
          <pricingTier>P1v2</pricingTier>
          <region>japaneast</region>
          <runtime>
```

## Modify the Java Source Code

```
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:p="http://primefaces.org/ui">

<h:head>
	<title>Hello World JSF 2.3</title>
</h:head>

<h:body>
	<p:outputPanel style="display:block">
		<h3 style="text-align: center">#{sampleBean.message}</h3>
	</p:outputPanel>
</h:body>

</html>
```

Please remove following from html tag?
`xmlns:o="http://omnifaces.org/ui"
	xmlns:of="http://omnifaces.org/functions"`


```
package sample;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class SampleBean {

	private String message;

	public String getMessage() {
		message = "Hello World";
		return message;
	}
}
```

## Deploy and Run on Local Environment

After you implemented the above code, you can deploy to the Tomcat in Local.
If you access to Tomcat server from Browser, you can see like following.s

![Application Running on Local Tomcat](./images/local-tomcat.png)


## Deploy Java Web App to Azure App Service

If you would like to deploy the Java Web App to Azure, you can execute the following command.

`mvn azure-webapp:deploy`

Following is the example of the command.

```
$ mvn clean package  azure-webapp:deploy
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------< com.microsoft.azure.samples:azure-javaweb-app >------------
[INFO] Building azure-javaweb-app 0.0.1-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ azure-javaweb-app ---
[INFO] Deleting /Users/yoterada/azure-javaweb-app/target

[INFO] --- azure-webapp-maven-plugin:1.9.1:deploy (default-cli) @ azure-javaweb-app ---
[INFO] Auth Type : AZURE_CLI, Auth Files : [/Users/yoterada/.azure/azureProfile.json, /Users/yoterada/.azure/accessTokens.json]
[INFO] [Correlation ID: 3a3d53b9-7f39-4e46-b42f-3d4c91ed34df] Instance discovery was successful
[INFO] Subscription : Microsoft Azure Internal Billing-CDA(f77aafe8-6be4-4d3d-bd9c-d0c37687ef70)
[INFO] Target Web App doesn't exist. Creating a new one...
[INFO] Creating App Service Plan 'ServicePlan2932ed54-b243-4c68'...
[INFO] Successfully created App Service Plan.
[INFO] Successfully created Web App.
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource to /Users/yoterada/azure-javaweb-app/target/azure-webapp/azure-javaweb-app-1596171354654-22bf0c41-c96b-4ef9-a96a-4b48de368374
[INFO] Trying to deploy artifact to azure-javaweb-app-1596171354654...
[INFO] Deploying the war file azure-javaweb-app.war...
[INFO] Successfully deployed the artifact to https://azure-javaweb-app-1596171354654.azurewebsites.net
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  07:55 min
[INFO] Finished at: 2020-07-31T14:23:01+09:00
[INFO] ------------------------------------------------------------------------
```

If you acces to the URL in the above logs, you can see the following screen.

```
[INFO] Successfully deployed the artifact to 
https://azure-javaweb-app-1596171354654.azurewebsites.net
```

![Java Web App Running on Azure App Service Linux Tomcat](./images/azure-tomcat.png)


## Deploy the Java Web App to Staging Environment (Deployment Slot)

If you execute the `mvn azure-webapp:config` command again, you can configure the [DeploymentSlot](https://docs.microsoft.com/azure/app-service/deploy-staging-slots?WT.mc_id=docs-github-yoterada) as follows. 

### Configure the Deployment Slot

```
$ mvn azure-webapp:config
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------< com.microsoft.azure.samples:azure-javaweb-app >------------
[INFO] Building azure-javaweb-app 0.0.1-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO] 
[INFO] --- azure-webapp-maven-plugin:1.9.1:config (default-cli) @ azure-javaweb-app ---
Please choose which part to config
1. Application
2. Runtime
3. DeploymentSlot
Enter index to use: 3
Deploy to slot?(Y/N): y
Define value for slotName(Default: azure-javaweb-app-1596171354654-slot): 
Define value for configurationSource: 
Please confirm webapp properties
AppName : azure-javaweb-app-1596171354654
ResourceGroup : azure-javaweb-app-1596171354654-rg
Region : westeurope
PricingTier : PremiumV2_P1v2
OS : Linux
RuntimeStack : TOMCAT 9.0-java11
Deploy to slot : true
Slot name : azure-javaweb-app-1596171354654-slot
ConfigurationSource : 
Confirm (Y/N)? : y
[INFO] Saving configuration to pom.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  02:15 min
[INFO] Finished at: 2020-07-31T14:44:33+09:00
[INFO] ------------------------------------------------------------------------
$ 
```

After finished the above command, you can see the following XML in the `pom.xml`.

```xml
          <deploymentSlot>
            <name>azure-javaweb-app-1596171354654-slot</name>
            <configurationSource></configurationSource>
          </deploymentSlot>
```

### Deploy the Java Web App the Deployment Slot

After the configuration, if you executed the `mvn azure-webapp:deploy` command, new `Deployment Slot` will be automatically created and deploy the Web App to it.

Please modify the source code like follows.

```java
package sample;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class SampleBean {

	private String message;

	public String getMessage() {
		message = "Hello World 2";
		return message;
	}
}
```

Then, you build the new package and deploy it to the Deployment Slot?

```bash
$ mvn clean package azure-webapp:deploy
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------< com.microsoft.azure.samples:azure-javaweb-app >------------
[INFO] Building azure-javaweb-app 0.0.1-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ azure-javaweb-app ---
[INFO] Deleting /Users/yoterada/azure-javaweb-app/target

[INFO] [Correlation ID: a533c9be-a1b8-4209-b789-a43016e5827c] Instance discovery was successful
[INFO] Subscription : Microsoft Azure Internal Billing-CDA(f77aafe8-6be4-4d3d-bd9c-d0c37687ef70)
[INFO] Updating target Web App...
[INFO] Successfully updated Web App.
[INFO] Target Deployment Slot doesn't exist. Creating a new one...
[INFO] Creating a new deployment slot and copying configuration from parent...
[INFO] Successfully created the Deployment Slot.
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource to /Users/yoterada/azure-javaweb-app/target/azure-webapp/azure-javaweb-app-bcdbe5b1-78a3-47c5-9b8f-567a4e16911e
[INFO] Trying to deploy artifact to azure-javaweb-app-1596171354654-slot...
[INFO] Deploying the war file azure-javaweb-app.war...
[INFO] Successfully deployed the artifact to https://azure-javaweb-app-azure-javaweb-app-1596171354654-slot.azurewebsites.net
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  02:15 min
[INFO] Finished at: 2020-07-31T14:52:53+09:00
[INFO] ------------------------------------------------------------------------
```

Then you can see the follogin logs.

```
[INFO] Successfully deployed the artifact to https://azure-javaweb-app-azure-javaweb-app-1596171354654-slot.azurewebsites.net
```

![Java Web App Running on Azure App Service Linux Tomcat Deployment Slot](./images/azure-tomcat-deployment-slot.png)


Azure Deployment Slot is very useful for evaluation, and you can create multiple deployment slot like follows.

![Deployment Slot](./images/azure-portal-deployment-slot.png)


# Implement DB Access code from Web Application

## Explain how to implement JSF (PrimeFaces)

TBD

## Explain how to implement JPA which access to MySQL

TBD

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


