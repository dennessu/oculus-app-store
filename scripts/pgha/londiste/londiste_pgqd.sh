#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

config=$SKYTOOL_CONFIG_PATH/pgqd.ini

echo "[LONDISTE] kill pgqd process"
if [ -f $SKYTOOL_PID_PATH/pgqd.pid ]; then
    cat $SKYTOOL_PID_PATH/pgqd.pid | xargs kill -9 || echo 'process does not exist'
fi

echo "[LONDISTE] generate pgqd configuration"
cat > $config <<EOF
[pgqd]

logfile = $SKYTOOL_LOG_PATH/pgqd.log
pidfile = $SKYTOOL_PID_PATH/pgqd.pid
EOF

echo "[LONDISTE] start pgqd ticker process"
pgqd $config  > /dev/null 2>&1 &