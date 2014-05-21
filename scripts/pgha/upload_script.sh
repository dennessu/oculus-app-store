#!/bin/bash
source common.sh

#master
ssh $MASTER_ADDRESS << ENDSSH
rm -rf $DEPLOYMENT_PATH
mkdir $DEPLOYMENT_PATH
mkdir $DEPLOYMENT_PATH/test
ENDSSH

scp common.sh $MASTER_DEPLOYMENT_PATH
scp purge_master.sh $MASTER_DEPLOYMENT_PATH
scp setup_master.sh $MASTER_DEPLOYMENT_PATH
scp base_backup.sh $MASTER_DEPLOYMENT_PATH
scp pg_hba.conf.template $MASTER_DEPLOYMENT_PATH

scp pgbouncer.sh $MASTER_DEPLOYMENT_PATH
scp pgbouncer_master.sh  $MASTER_DEPLOYMENT_PATH
scp pgbouncer_slave.sh  $MASTER_DEPLOYMENT_PATH

scp master_cron.sh  $MASTER_DEPLOYMENT_PATH
scp ./test/master2slave.sh $MASTER_DEPLOYMENT_PATH/test/master2slave.sh

#slave
ssh $SLAVE_ADDRESS << ENDSSH
rm -rf $DEPLOYMENT_PATH
mkdir $DEPLOYMENT_PATH
mkdir $DEPLOYMENT_PATH/test
ENDSSH

scp common.sh $SLAVE_DEPLOYMENT_PATH
scp purge_slave.sh $SLAVE_DEPLOYMENT_PATH
scp setup_slave.sh $SLAVE_DEPLOYMENT_PATH
scp pg_hba.conf.template $SLAVE_DEPLOYMENT_PATH

scp pgbouncer.sh $SLAVE_DEPLOYMENT_PATH
scp pgbouncer_master.sh  $SLAVE_DEPLOYMENT_PATH
scp pgbouncer_slave.sh  $SLAVE_DEPLOYMENT_PATH

scp slave_cron.sh  $SLAVE_DEPLOYMENT_PATH
scp ./test/master2slave.sh $SLAVE_DEPLOYMENT_PATH/test/master2slave.sh