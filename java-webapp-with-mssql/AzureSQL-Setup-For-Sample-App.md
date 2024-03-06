# Create Azure SQL database

## Create database

Use AZ CLI commands from [Quickstart: Create a single database - Azure SQL Database](https://learn.microsoft.com/en-us/azure/azure-sql/database/single-database-create-quickstart?view=azuresql&tabs=azure-portal) to create Azure SQL Database.

Set varaibles.

```bash
# Variable block
let "randomIdentifier=$RANDOM*$RANDOM"
LOCATION="japaneast"
MSSQL_RG_NAME="MSSQL_RG"
TAG="azure-web-application"
SERVER_NAME="webappsqlserver0305"
DATABASE_NAME="world"
LOGIN_NAME="azureuser"
PASSWORD="Pa$$w0rD-$randomIdentifier"
# allow access from azure service
START_IP=0.0.0.0
END_IP=0.0.0.0

echo "Using resource group $MSSQL_RG_NAME with login: $LOGIN_NAME, password: $PASSWORD..."
```

Create a resource group.

```bash
echo "Creating $MSSQL_RG_NAME in $LOCATION..."
az group create --name $MSSQL_RG_NAME --location "$LOCATION" --tags $TAG
```

Create a server.

```bash
echo "Creating $SERVER_NAME in $LOCATION..."
az sql server create --name $SERVER_NAME --resource-group $MSSQL_RG_NAME --location "$LOCATION" --admin-user $LOGIN_NAME --admin-password $PASSWORD
```

Configure firewall.

```bash
echo "Configuring firewall..."
az sql server firewall-rule create --resource-group $MSSQL_RG_NAME --server $SERVER_NAME -n AllowYourIp --start-ip-address $START_IP --end-ip-address $END_IP
```

Create a database.

```bash
echo "Creating $DATABASE_NAME in serverless tier"
az sql db create --resource-group $MSSQL_RG_NAME --server $SERVER_NAME --name $DATABASE_NAME --edition GeneralPurpose --compute-model Serverless --family Gen5 --capacity 2
```

## Import data

Follow [Query the database](https://learn.microsoft.com/en-us/azure/azure-sql/database/single-database-create-quickstart?view=azuresql&tabs=azure-cli#query-the-database) to execute script in [world.sql](mssql/src/main/resources/world.sql).

Note, the DB script is used for a demo, please do not use in production environment.