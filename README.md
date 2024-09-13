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
   "admin": ["Admin"],
   "superUser": ["Alpha"],
   "simpleUser": ["Gamma"],
   "sales": ["sales"],
   "logistics": ["logistics"],

####UIs are accessible at the following links:#####
Trino: `http://localhost:5542/ui/` (username can be anything) <br>
Minio: `http://localhost:9001/` (username: `minio`, password: `minio123`)<br>
Superset: `http://localhost:8088/` (username: `admin`, password: `admin`)<br>


 
 ## Create MinIO buckets
1. Create bucket with name **raw-data**
2. Create bucket with name **hive-warehouse**

## Connect to Trino via a Client (like DBeaver)
Create a DB Connection (Trino) as `jdbc:trino://localhost:5542` - user "admin", pwd: none

## Create schemas in Trino
1. Create schema for hive connector: 
	```sql
	CREATE SCHEMA IF NOT EXISTS hive.hive_schema WITH (location = 's3a://hive-warehouse/');
	```
## Importing Data

The dataset which will be used for demo purposes, is the `ais_mapped` which resides in this folder. It is a CSV with AIS data.
Smaller file: trino/Sample.csv

1. Upload the CSV to a **ais** inside the `raw-data` bucket in MinIO. We use the directory /ais for this examle.
 
2. Create an external temporary table pointing to that directory.
```sql
CREATE TABLE hive.hive_schema.ais_temp (
    ship_id         VARCHAR,
    status          VARCHAR,
    status_name     VARCHAR,
    speed           VARCHAR,
    lon             VARCHAR,
    lat             VARCHAR,
    course          VARCHAR,
    heading         VARCHAR,
    "timestamp"     VARCHAR,
    shiptype        VARCHAR,
    type_name       VARCHAR,
    flag_code       VARCHAR,
    "length"        VARCHAR,
    width           VARCHAR
) 
WITH (
    format = 'CSV', 
    csv_separator=',', 
    external_location='s3a://raw-data/ais/', 
    skip_header_line_count=1);

#####  Smaller file:

CREATE TABLE hive.hive_schema.ais_temp (
Name         VARCHAR,
Age          VARCHAR
)
    WITH (
        format = 'CSV',
        csv_separator=',',
        external_location='s3a://raw-data/ais/',
        skip_header_line_count=1);

```

3. Check if table was successfully imported with a simple query.
```sql
SELECT * FROM hive.hive_schema.ais_temp LIMIT 10;
```

5. Create a Hive-managed table.
```sql
CREATE TABLE hive.hive_schema.ais (
    ship_id         INTEGER,
    status          INTEGER,
    status_name     VARCHAR,
    speed           INTEGER,
    lon             DOUBLE,
    lat             DOUBLE,
    course          INTEGER,
    heading         INTEGER,
    "timestamp"     TIMESTAMP(3),
    shiptype        INTEGER,
    type_name       VARCHAR,
    flag_code       INTEGER,
    "length"        INTEGER,
    width           INTEGER
) 
WITH (
    format = 'ORC'
);

#### Smaller file:
CREATE TABLE hive.hive_schema.ais (
Name         VARCHAR,
Age          INTEGER
)
    WITH (
        format = 'ORC'
        );
```

6. Import data to Hive-managed table.
```sql
INSERT INTO hive.hive_schema.ais 
SELECT 
       try_cast(ship_id AS INTEGER),
       try_cast(status AS INTEGER),
       status_name,
       try_cast(speed AS INTEGER),
       try_cast(lon AS DOUBLE),
       try_cast(lat AS DOUBLE),
       try_cast(course AS INTEGER),
       try_cast(heading AS INTEGER),
       try_cast(timestamp AS TIMESTAMP),
       try_cast(shiptype AS INTEGER),
       type_name,
       try_cast(flag_code AS INTEGER),
       try_cast(length AS INTEGER),
       try_cast(width AS INTEGER)
FROM hive.hive_schema.ais_temp;

##### Smaller File:
INSERT INTO hive.hive_schema.ais
SELECT
    Name,
    try_cast(Age AS INTEGER)
FROM hive.hive_schema.ais_temp;
```

7. Check if import was successful.
```sql
SELECT * FROM hive.hive_schema.ais LIMIT 20;
```


## Connect to Trino in Superset:
1. Go to `data` dropdown and click `databases`
2. Click the `+ database` button
3. For `Select a database to connect` choose `presto`
4. In `SQLALCHEMY URI` put `trino://hive@trino-coordinator:5542/hive`
5. Switch over to `Advanced` tab
5. In `SQL Lab` select all options
5. In `Security` select `Allow data upload`
6. As Admin, create the roles needed, and grant them rights on the "Datasource.Table" that you have already created datasets from 

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
},

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


