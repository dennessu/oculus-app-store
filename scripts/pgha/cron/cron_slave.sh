#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "create cron folder $CRON_PATH"
rm -rf $CRON_PATH
mkdir $CRON_PATH

echo "generate script for sync slave archive log to master..."
cat > $CRON_PATH/sync_archive_log.sh <<EOF
#!/bin/bash
echo "sync log from slave to master..."
rsync -e "ssh -o StrictHostKeyChecking=no" -azhv $SLAVE_ARCHIVE_PATH/* $DEPLOYMENT_ACCOUNT@$MASTER_HOST:$MASTER_ARCHIVE_PATH
EOF

chmod 744 $CRON_PATH/sync_archive_log.sh 

echo "generate crontab configuration..."
cat > $CRON_PATH/slave_cron <<EOF
*/1 * * * * $CRON_PATH/sync_archive_log.sh >> $CRON_PATH/sync_archive_log.log

EOF

echo "add sync log cron job..."
crontab $CRON_PATH/slave_cron