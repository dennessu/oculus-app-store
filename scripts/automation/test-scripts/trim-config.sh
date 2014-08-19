#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

: ${ENV?"Need to set ENV"}

./foreach-here.sh $ENV/crypto-apps.txt $ENV/apps.txt $ENV/utils.txt << EOF
TRIMMED_CONFIG=`head -n 4 /etc/silkcloud/configuration.properties`
echo $TRIMMED_CONFIG > /etc/silkcloud/configuration.properties
cat << EOFINNER > /etc/silkcloud/configuration.properties
chmod 600 /etc/silkcloud/configuration.properties
EOF

./foreach-here.sh $ENV/crypto-dbs.txt $ENV/masters.txt $ENV/secondaries.txt $ENV/replicas.txt << EOF
TRIMMED_CONFIG=`head -n 3 /etc/silkcloud/configuration.properties`
echo $TRIMMED_CONFIG > /etc/silkcloud/configuration.properties
cat << EOFINNER > /etc/silkcloud/configuration.properties
chmod 600 /etc/silkcloud/configuration.properties
EOF
