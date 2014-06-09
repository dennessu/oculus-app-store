#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

#chmod pgbouncer auth file
chmod 600 $PGBOUNCER_AUTH_FILE

TARGET_DB_HOST=$1
TARGET_DB_PORT=$2

echo "create pgbouncer home"
createDir $PGBOUNCER_BASE

# primary pgbouncer proxy
echo "generate primary pgbouncer configuration..."

cat > $PGBOUNCER_CONF <<EOF
[databases]
* = host=$TARGET_DB_HOST port=$TARGET_DB_PORT

[pgbouncer]
logfile = $PGBOUNCER_BASE/pgbouncer.log
pidfile = $PGBOUNCER_BASE/pgbouncer.pid
listen_addr = *
listen_port = $PGBOUNCER_PORT
auth_type = md5
auth_file = $PGBOUNCER_AUTH_FILE
pool_mode = session
server_reset_query = DISCARD ALL
max_client_conn = $PGBOUNCER_MAX_CONNECTIONS
default_pool_size = $PGBOUNCER_DEFAULT_POOL_SIZE
ignore_startup_parameters = extra_float_digits
EOF

echo "kill pgbouncer process with port [$PGBOUNCER_PORT]..."
forceKill $PGBOUNCER_PORT

echo "remove pgbouncer socket and pid files"
rm -f $PGBOUNCER_SOCKET_PATH/.s.PGSQL.$PROXY_DB_PORT
rm -f $PGBOUNCER_PID

echo "start pgbouncer..."
$PGBOUNCER_BIN/pgbouncer -d -v $PGBOUNCER_CONF

while ! echo exit | nc localhost $PGBOUNCER_PORT; do sleep 1 && echo "waiting for pgbouncer proxy..."; done
echo "pgbouncer proxy started successfully!"
