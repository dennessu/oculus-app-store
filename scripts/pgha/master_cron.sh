#!/bin/bash
source common.sh

#check running under specified account
checkAccount $DEPLOYMENT_ACCOUNT

echo "create cron folder $CRON_PATH"
rm -rf $CRON_PATH
mkdir $CRON_PATH

echo "generate script for sync master archive log to slave..."
cat > $CRON_PATH/sync_archive_log.sh <<EOF
#!/bin/bash
echo "sync log from master to slave..."
rsync -azhv $MASTER_ARCHIVE_PATH/* $DEPLOYMENT_ACCOUNT@$SLAVE_HOST:$SLAVE_ARCHIVE_PATH
EOF

chmod 744 $CRON_PATH/sync_archive_log.sh 

echo "generate crontab configuration..."
cat > $CRON_PATH/master_cron <<EOF
*/1 * * * * $CRON_PATH/sync_archive_log.sh >> $CRON_PATH/sync_archive_log.log

EOF

echo "add sync log cron job..."
crontab $CRON_PATH/master_cron