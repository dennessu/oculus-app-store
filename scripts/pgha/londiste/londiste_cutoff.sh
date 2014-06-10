#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
source ${DIR}/../util/common.sh

echo "promote replcia database to cut off streaming replication..."
touch $PROMOTE_TRIGGER_FILE