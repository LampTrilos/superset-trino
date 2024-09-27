# Cloud-native Trino (prestosql) + Hive + Minio + Superset
## Technologies:
### Query Engine is `Trino (PrestoSQL)`
### Metadata Store is `Apache Hive`
### Object Storage is `Minio (S3 compatable)`
### Data Viz is `Apache Superset`

## Get things running
1. Clone repo
2. Install docker + docker-compose
3. Run `docker-compose up -d`
### Not needed anymore, it was built in the Dockerfile and docler-compose 4. Run `bash bootstrap-superset.sh`
5. Done! Checkout the service endpoints:
6. Manually insert the superset client in Keycloak with superset.json
7. Manually insert users, and also remove the user action "Update Password" from the first user tab
To add the roles inside the token go: Clients -> backend-service -> Client Details -> Dedicated Scopes -> Add Mapper -> User Client Roles/roles/backend-service/roles for Token Claim Name
###Keycloak Client roles are mapped to Superset roles like this:####
##If the User is not Admin, you MUST assign either the simpleUser or the superUser or the user won't be able to log in
   "admin": ["Admin"],
   "superUser": ["Alpha"],
   "simpleUser": ["Gamma"],
   "roleA": ["roleA"],
   "roleB": ["roleB"],

####UIs are accessible at the following links:#####
Trino: `http://localhost:5542/ui/` (username can be anything) <br>
Minio: `http://localhost:9001/` (username: `minio`, password: `minio123`)<br>
Superset: `http://localhost:8088/` (username: `admin`, password: `admin`)<br>
Vue Frontend: http://localhost:8081/


 
 ## Create new User
 When a new user has been created, the backend service executes commands in order to create the neccessary minio buckets, that will be used. 
 1. A bucket with name **raw-data-XXXXX**
 2. A bucket with name **hive-warehouse-XXXXX**
 
 where XXXXX is the name of the role that the user is logged in with, (for example "sales", "solar panels" etc)
 3. At last a hive schema is created, with the same name as the role that the user is logged in with.


## Importing Data

	Through the frontend, the user can upload a .csv or a .json file that will be uploaded.
	
	If the file is a .json file, it's transformed to a .csv file and then uploaded to the raw-data-XXXXX bucket.
	A csv file is uploaded without any change to the same bucket.
	
	Then the backend executes commands in order to create an orc table that will further be used by superset.
	The orc table's name is the fileId or fileName that had been uploaded, stripped by all the non alphabetic symbols (for example fakeJson-123.json will become the fakeJson orc table)


## Connect to Trino in Superset:
1. Go to `data` dropdown and click `databases`
2. Click the `+ database` button
3. For `Select a database to connect` choose `presto`
4. In `SQLALCHEMY URI` put `trino://hive@trino-coordinator:5542/hive`
5. Switch over to `Advanced` tab
5. In `SQL Lab` select all options
5. In `Security` select `Allow data upload`
6. As Admin, create the roles needed, and grant them rights on the "Datasource.Table" that you have already created datasets from 
7. Manually insert users, and also remove the user action "Update Password" from the first user tab
   To add the roles inside the token go: Clients -> backend-service -> Client Details -> Dedicated Scopes -> Add Mapper -> User Client Roles/roles/backend-service/roles for Token Claim Name
8. Superset auto-imports the roles&permission file roles_export.json, since it is built in the image

9. As Admin, create the roles needed, and grant them rights on the "Datasource.Table" that you have already created datasets from
10. If you need to export roles and permissions run:
   docker exec -it trino-hive-superset-superset-1 flask fab export-roles -path /app/roles_export.json
   docker cp trino-hive-superset-superset-1:roles_export.json .

Maven Goal to start the Backend:
-Dmaven.test.skip clean compile quarkus:dev




#################If an exported realm cannot be inserted in Keycloak###################

For me only changing the js policy to regex policy for Keycloak 24.0.4 worked and is cleaner for this Default Policy IMO.
{
"id": "b56eebd7-8e73-4449-b110-30dfdbc77f03",
"name": "Default Policy",
"description": "A policy that grants access only for users within this realm",
"type": "js",
"logic": "POSITIVE",
"decisionStrategy": "AFFIRMATIVE",
"config": {
"code": "// by default, grants any permission associated with this policy\n$evaluation.grant();\n"
}
}

for:

{
"id": "b56eebd7-8e73-4449-b110-30dfdbc77f03",
"name": "Default Policy",
"description": "A policy that grants access only for users within this realm",
"type": "regex",
"logic": "POSITIVE",
"decisionStrategy": "AFFIRMATIVE",
"config": {
"targetContextAttributes" : "false",
"pattern" : ".*",
"targetClaim" : "sub"
}
}

Also, all secrets are converted to ***** so search for "secret":


