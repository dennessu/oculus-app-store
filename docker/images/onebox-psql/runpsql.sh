#!/bin/bash
set -e

POSTGRESQL_USER=postgres
POSTGRESQL_PASS=${PSQL_PASS:-"password1234"}

POSTGRESQL_BIN=/usr/lib/postgresql/9.3/bin/postgres
POSTGRESQL_CONFIG_FILE=/etc/postgresql/9.3/main/postgresql.conf
POSTGRESQL_LOG_DIR=/var/log/postgresql
POSTGRESQL_DATA_ORIG=/var/lib/postgresql/9.3/main
POSTGRESQL_DATA=/data

if [[ ! -d $POSTGRESQL_DATA ]]; then
  mkdir -p $POSTGRESQL_DATA
fi

# if the data folder is empty, init the db by copying default generated db
if [[ ! "$(ls -A $POSTGRESQL_DATA)" ]]; then
  echo "Initializing PostgreSQL at $POSTGRESQL_DATA"
  cp -R $POSTGRESQL_DATA_ORIG/* $POSTGRESQL_DATA
fi

# workaround for boot2docker bug: https://github.com/boot2docker/boot2docker/issues/581
usermod -u 1000 postgres

chown -R postgres $POSTGRESQL_LOG_DIR
chown -R postgres $POSTGRESQL_DATA
# BUG: does not work in boot2docker+mac now(issue 581), need to chmod on mac as well
chmod -R 700 $POSTGRESQL_DATA

# workaround the snakeoil key issue
sudo -u postgres ls /etc/ssl/private/ssl-cert-snakeoil.key || {
  echo "user postgres cannot access snakoil.key, try to workaround AUFS bug"
  mv /etc/ssl/private /etc/ssl/private~ && cp -pr /etc/ssl/private~ /etc/ssl/private && rm -rf /etc/ssl/private~
}

POSTGRESQL_SINGLE="sudo -u postgres $POSTGRESQL_BIN --single --config-file=$POSTGRESQL_CONFIG_FILE"

# change user postgres' password
$POSTGRESQL_SINGLE <<< "ALTER USER $POSTGRESQL_USER WITH PASSWORD '$POSTGRESQL_PASS';" > /dev/null

exec setuser postgres $POSTGRESQL_BIN -D $POSTGRESQL_DATA -c config-file=$POSTGRESQL_CONFIG_FILE
