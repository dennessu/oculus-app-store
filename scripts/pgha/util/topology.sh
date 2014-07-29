#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

role=`getServerRole`
echo "server is [$role]"

host=${role}_HOST
port=${role}_DB_PORT

psql postgres -h ${!host} -p ${!port} -c "SELECT pg_is_in_recovery();" -t > /dev/null

if [[ $? = "f" ]] ; then
    echo "server is taking live traffic"
else
    echo "server is in hot standby mode"
fi