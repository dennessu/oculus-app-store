#!/bin/bash
source common.sh

#master
ssh $MASTER_ADDRESS << ENDSSH
mkdir -p $DEPLOYMENT_PATH
rm -rf $DEPLOYMENT_PATH/*
ENDSSH

scp common.sh $MASTER_DEPLOYMENT_PATH
scp purge_master.sh $MASTER_DEPLOYMENT_PATH
scp setup_master.sh $MASTER_DEPLOYMENT_PATH
scp base_backup.sh $MASTER_DEPLOYMENT_PATH
scp pg_hba.conf.template $MASTER_DEPLOYMENT_PATH

scp pgbouncer.sh $MASTER_DEPLOYMENT_PATH
scp pgbouncer_master.sh  $MASTER_DEPLOYMENT_PATH/pgbouncer_master.sh
scp pgbouncer_slave.sh  $MASTER_DEPLOYMENT_PATH/pgbouncer_slave.sh

#slave
ssh $SLAVE_ADDRESS << ENDSSH
mkdir -p $DEPLOYMENT_PATH
rm -rf $DEPLOYMENT_PATH/*
ENDSSH

scp common.sh $SLAVE_DEPLOYMENT_PATH
scp purge_slave.sh $SLAVE_DEPLOYMENT_PATH
scp setup_slave.sh $SLAVE_DEPLOYMENT_PATH
scp pg_hba.conf.template $SLAVE_DEPLOYMENT_PATH

scp pgbouncer.sh $SLAVE_DEPLOYMENT_PATH
scp pgbouncer_master.sh  $SLAVE_DEPLOYMENT_PATH/pgbouncer_master.sh
scp pgbouncer_slave.sh  $SLAVE_DEPLOYMENT_PATH/pgbouncer_slave.sh