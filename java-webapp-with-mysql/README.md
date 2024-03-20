# Introduction

Follow the steps to run this application.

1. Create Azure SQL database and import data following [Create DB for MySQL on Azure](https://github.com/yoshioterada/microprofile-samples/blob/master/MySQL/Azure-MySQL-Setup-For-Sample-App.md).

2. Build and deploy this application to Azure WebApp.

    Change webapp config in pom.xml file, make sure the `appName` is a unique  name, e.g.:

    ```xml
    <resourceGroup>azure-javaweb-app</resourceGroup>
    <appName>azure-javaweb-app-0306</appName>
    <pricingTier>P1v2</pricingTier>
    <region>japaneast</region>
    ```

    Build and deploy the app.

    ```bash
    mvn clean package  azure-webapp:deploy
    ```

    Please note that, the database is not set yet, the application is not accessible.

3. Configure database settings and restart the application following [MySQL Settings in the Web Application](MySQL-Settings-For-Sample-App.md).

4. You can access the application with the URL from output. It may takes several minutes to connect to the database with correct settings.