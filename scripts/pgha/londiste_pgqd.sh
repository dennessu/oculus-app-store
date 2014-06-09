#!/bin/bash
source common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

config=$SKYTOOL_CONFIG_PATH/pgqd.ini

echo "kill pgqd process"
if [ -f $SKYTOOL_PID_PATH/pgqd.pid ]; then
    cat $SKYTOOL_PID_PATH/pgqd.pid | xargs kill -9
fi

cat > $config <<EOF
[pgqd]

logfile = $SKYTOOL_LOG_PATH/pgqd.log
pidfile = $SKYTOOL_PID_PATH/pgqd.pid
EOF