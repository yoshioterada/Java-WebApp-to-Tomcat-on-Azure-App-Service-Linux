# Azure SQL Settings in the Web Application

## Configure the environment variable values in Application settings

```bash
# repalce the webapp name with yours
WEB_APP_NAME="azure-javaweb-app"
# repalce the resource group name with yours
WEB_APP_RG_NAME="WebApp"
```

```azurecli
 az webapp config appsettings set \
     --resource-group ${WEB_APP_RG_NAME} \
     --name ${WEB_APP_NAME} \
     --settings JDBC_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver"
```

```azurecli
 az webapp config appsettings set \
     --resource-group ${WEB_APP_RG_NAME} \
     --name ${WEB_APP_NAME} \
     --settings JDBC_URL="jdbc:sqlserver://webappsqlserver0305.database.windows.net:1433;database=world;"
```

```azurecli
 az webapp config appsettings set \
     --resource-group ${WEB_APP_RG_NAME} \
     --name ${WEB_APP_NAME} \
     --settings DB_USER="azureuser@webappsqlserver0305"
```

```azurecli
# repalce the password with yours
 az webapp config appsettings set \
     --resource-group ${WEB_APP_RG_NAME} \
     --name ${WEB_APP_NAME} \
     --settings DB_PASSWORD="PASSWORD"
```

List the config.

```azurecli
$ az webapp config appsettings list --name ${WEB_APP_NAME} -g ${WEB_APP_RG_NAME}
[
  {
    "name": "JDBC_DRIVER",
    "slotSetting": false,
    "value": "com.microsoft.sqlserver.jdbc.SQLServerDriver"
  },
  {
    "name": "JDBC_URL",
    "slotSetting": false,
    "value": "jdbc:sqlserver://webappsqlserver0305.database.windows.net:1433;database=world;"
  },
  {
    "name": "DB_USER",
    "slotSetting": false,
    "value": "azureuser@webappsqlserver0305"
  },
  {
    "name": "DB_PASSWORD",
    "slotSetting": false,
    "value": "mypassword"
  }
]
```

## Configure in the persistence.xml

Input the real password value of `$password`. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1" xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    <persistence-unit name="JPAWorldDatasourcePU" transaction-type="RESOURCE_LOCAL">
        <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
        <class>com.microsoft.azure.samples.entities.City</class>
        <class>com.microsoft.azure.samples.entities.Country</class>
        <exclude-unlisted-classes>true</exclude-unlisted-classes>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="com.microsoft.sqlserver.jdbc.SQLServerDriver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:sqlserver://webappsqlserver0305.database.windows.net:1433;database=world;"/>
            <property name="javax.persistence.jdbc.user" value="azureuser@webappsqlserver0305"/>
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

## Restart WebApp to enable database setting

Run the following command to deploy the app again.

```bash
mvn clean package azure-webapp:deploy
```
