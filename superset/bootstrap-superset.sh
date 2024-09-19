#!/bin/bash
superset fab create-admin --username admin --firstname Superset --lastname Admin --email admin@superset.com --password admin
superset db upgrade
superset init
# This ensures the last command executes correctly with or without a newline
true