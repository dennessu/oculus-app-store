#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "create skytool folder"
createDir $SKYTOOL_PATH
createDir $SKYTOOL_CONFIG_PATH
createDir $SKYTOOL_PID_PATH
createDir $SKYTOOL_LOG_PATH

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_leaf.ini

    echo "generate londiste3 configuration files for database [$db]"
    cat > $config <<EOF
[londiste3]
job_name = job_leaf_${db}
db = dbname=${db}
queue_name = queue_$db
logfile = $SKYTOOL_LOG_PATH/$db.log
pidfile = $SKYTOOL_PID_PATH/$db.pid
EOF

done
