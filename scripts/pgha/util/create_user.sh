#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

USER=$1
DB_HOST=$2
DB_PORT=$3

echo "create postgres user [$USER]"
$PGBIN_PATH/psql postgres -h $DB_HOST -p $DB_PORT -c "CREATE USER ${USER};"
$PGBIN_PATH/psql postgres -h $DB_HOST -p $DB_PORT -c "ALTER USER ${USER} SET default_transaction_read_only = on;"
$PGBIN_PATH/psql postgres -h $DB_HOST -p $DB_PORT -c "GRANT SELECT ON ALL TABLES IN SCHEMA public TO ${USER};"
