FROM apache/superset:4.0.2
# Switching to root to install the required packages
USER root
# Example: installing the MySQL driver to connect to the metadata database
# if you prefer Postgres, you may want to use `psycopg2-binary` instead
RUN pip install mysqlclient
RUN pip install sqlalchemy-trino
#This is needed for Keycloak interoperability
RUN pip install Flask-OAuthlib Authlib
# Switching back to using the `superset` user
USER superset

COPY /superset_config.py /app/superset_config.py
COPY /bootstrap-superset.sh /app/bootstrap-superset.sh
