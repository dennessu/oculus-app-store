#!/bin/bash
source set_env.sh
source common.sh

function genConfig {
    role=$1
    pgbouncer_host=$2
    pgbouncer_port=$3

	echo "configure $role configuration..."
	cat >> $PGBOUNCER_BASE/${role}_pgbouncer.conf <<EOF
[databases]
* = host=$ACTIVE_DATABASE_SERVER port=$ACTIVE_DATABSE_PORT

[pgbouncer]
logfile = $PGBOUNCER_BASE/${role}_pgbouncer.log
pidfile = $PGBOUNCER_BASE/${role}_pgbouncer.pid
listen_addr = $pgbouncer_host
listen_port = $pgbouncer_port
auth_type = trust
auth_file = $PGBOUNCER_AUTH_FILE
pool_mode = session
server_reset_query = DISCARD ALL
max_client_conn = $PGBOUNCER_MAX_CONNECTIONS
default_pool_size = $PGBOUNCER_DEFAULT_POOL_SIZE
ignore_startup_parameters = extra_float_digits
EOF
}

# main
echo "create pgbouncer folder"
rm -rf $PGBOUNCER_BASE
mkdir $PGBOUNCER_BASE

echo "configure database users"
cat >> $PGBOUNCER_AUTH_FILE <<EOF
"postgres" "postgres"
EOF

ACTIVE_DATABASE_SERVER=$1
ACTIVE_DATABSE_PORT=$2

# primary pgbouncer proxy
echo "generate primary pgbouncer configuration..."
genConfig 'primary' $PRIMARY_PGBOUNCER_HOST $PRIMARY_PGBOUNCER_PORT

echo "start primary pgbouncer proxy..."
forceKill $PRIMARY_PGBOUNCER_PORT

$PGBOUNCER_BIN/pgbouncer -d -v $PGBOUNCER_BASE/primary_pgbouncer.conf

while ! echo exit | nc $PRIMARY_PGBOUNCER_HOST $PRIMARY_PGBOUNCER_PORT; do sleep 1 && echo "Waiting for primary pgbouncer proxy..."; done
echo "start primary pgbouncer proxy successfully!"

# secondary pgbouncer proxy
echo "generate secondary pgbouncer configuration..."
genConfig 'secondary' $SECONDARY_PGBOUNCER_HOST $SECONDARY_PGBOUNCER_PORT

echo "start secondary pgbouncer proxy..."
forceKill $SECONDARY_PGBOUNCER_PORT

$PGBOUNCER_BIN/pgbouncer -d -v $PGBOUNCER_BASE/secondary_pgbouncer.conf

while ! echo exit | nc $SECONDARY_PGBOUNCER_HOST $SECONDARY_PGBOUNCER_PORT; do sleep 1 && echo "Waiting for secondary pgbouncer proxy..."; done
echo "start secondary pgbouncer proxy successfully!"

