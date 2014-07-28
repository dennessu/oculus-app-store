#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "[LONDISTE] kill skytools instance"
forceKillPid $SKYTOOL_PID_PATH

echo "[LONDISTE] create skytool folder"
createDir $SKYTOOL_PATH
createDir $SKYTOOL_CONFIG_PATH
createDir $SKYTOOL_PID_PATH
createDir $SKYTOOL_LOG_PATH

role=`cat $PGHA_BASE/role.conf`
host=${role}_HOST
port=${role}_DB_PORT

for db in ${REPLICA_DATABASES[@]}
do
    config=$SKYTOOL_CONFIG_PATH/${db}_root.ini

    echo "[LONDISTE] generate londiste3 configuration files for database [$db]"
    cat > $config <<EOF
[londiste3]
job_name = job_root_${db}
db = dbname=${db} port=${!port} host=${!host}
queue_name = queue_${db}
logfile = $SKYTOOL_LOG_PATH/$db.log
pidfile = $SKYTOOL_PID_PATH/$db.pid
EOF

done
