#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

#remove execute permssion on purge/setup scripts for satefy
chmod 600 $DEPLOYMENT_PATH/purge/*
chmod 600 $DEPLOYMENT_PATH/setup/*