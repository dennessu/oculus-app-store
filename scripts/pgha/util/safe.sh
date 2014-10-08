#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#remove execute permssion on purge/setup scripts for satefy
chmod 600 $DEPLOYMENT_PATH/purge/*
chmod 600 $DEPLOYMENT_PATH/setup/*

#remove execute permssion on test scripts for satefy
chmod 600 $DEPLOYMENT_PATH/test/*
chmod 700 $DEPLOYMENT_PATH/test/test_master2slave.sh
chmod 700 $DEPLOYMENT_PATH/test/test_slave2master.sh
