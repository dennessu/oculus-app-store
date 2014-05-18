#!/bin/bash
source common.sh

ssh $MASTER_ADDRESS << ENDSSH
mkdir -p $DEPLOYMENT_PATH
rm -rf $DEPLOYMENT_PATH/*
ENDSSH

scp common.sh $MASTER_DEPLOYMENT_PATH
scp purge_master.sh $MASTER_DEPLOYMENT_PATH
scp setup_master.sh $MASTER_DEPLOYMENT_PATH

scp pg_hba.conf.template $MASTER_DEPLOYMENT_PATH

#scp common.sh $SLAVE_DEPLOYMENT_PATH
#scp purge.sh $SLAVE_DEPLOYMENT_PATH