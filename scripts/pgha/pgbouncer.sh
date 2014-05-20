#!/bin/bash
source common.sh

TARGET_DB_HOST=$1
TARGET_DB_PORT=$2
PROXY_DB_HOST=$3
PROXY_DB_PORT=$4

echo "create pgbouncer home"
mkdir -p $PGBOUNCER_BASE

echo "configure database users"
cat > $PGBOUNCER_AUTH_FILE <<EOF
"silkcloud" "silkcloud"
EOF

# primary pgbouncer proxy
echo "generate primary pgbouncer configuration..."

cat > $PGBOUNCER_CONF <<EOF
[databases]
* = host=$TARGET_DB_HOST port=$TARGET_DB_PORT

[pgbouncer]
logfile = $PGBOUNCER_BASE/pgbouncer.log
pidfile = $PGBOUNCER_BASE/pgbouncer.pid
listen_addr = $PROXY_DB_HOST
listen_port = $PROXY_DB_PORT
auth_type = trust
auth_file = $PGBOUNCER_AUTH_FILE
pool_mode = session
server_reset_query = DISCARD ALL
max_client_conn = $PGBOUNCER_MAX_CONNECTIONS
default_pool_size = $PGBOUNCER_DEFAULT_POOL_SIZE
ignore_startup_parameters = extra_float_digits
EOF

echo "kill pgbouncer process with port [$PROXY_DB_HOST]..."
forceKill $PROXY_DB_HOST

echo "start pgbouncer..."
$PGBOUNCER_BIN/pgbouncer -d -v $PGBOUNCER_CONF

while ! echo exit | nc $PROXY_DB_HOST $PROXY_DB_HOST; do sleep 1 && echo "waiting for pgbouncer proxy..."; done
echo "pgbouncer proxy started successfully!"
