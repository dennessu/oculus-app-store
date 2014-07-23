#!/bin/bash
set -e

POSTGRESQL_USER=postgres
POSTGRESQL_PASS=${PSQL_PASS:-"password1234"}

POSTGRESQL_BIN=/usr/lib/postgresql/9.3/bin/postgres
POSTGRESQL_CONFIG_FILE=/etc/postgresql/9.3/main/postgresql.conf
POSTGRESQL_DATA=/var/lib/postgresql/9.3/main

POSTGRESQL_SINGLE="sudo -u postgres $POSTGRESQL_BIN --single --config-file=$POSTGRESQL_CONFIG_FILE"

$POSTGRESQL_SINGLE <<< "ALTER USER $POSTGRESQL_USER WITH PASSWORD '$POSTGRESQL_PASS';" > /dev/null

exec setuser postgres $POSTGRESQL_BIN -D $POSTGRESQL_DATA --config-file=$POSTGRESQL_CONFIG_FILE
