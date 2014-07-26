#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR
set -e

export ENV=${ENV:-$1}
export ENV=${ENV:-ppe}

CRYPTO_SERVER_1=`cat $ENV/crypto-dbs.txt | sed -n 1p`
CRYPTO_SERVER_2=`cat $ENV/crypto-dbs.txt | sed -n 2p`

ROWCOUNT1=`ssh $CRYPTO_SERVER_1 'psql -d crypto -c "select count(*) from master_key;"' | egrep '^[[:blank:]]*[[:digit:]]+[[:blank:]]*$' | sed 's/[ \t]*//'`
ROWCOUNT2=`ssh $CRYPTO_SERVER_2 'psql -d crypto -c "select count(*) from master_key;"' | egrep '^[[:blank:]]*[[:digit:]]+[[:blank:]]*$' | sed 's/[ \t]*//'`

echo Found $ROWCOUNT1 master keys in $CRYPTO_SERVER_1
echo Found $ROWCOUNT2 master keys in $CRYPTO_SERVER_2

if [ "$ROWCOUNT2" -gt "$ROWCOUNT1" ]; then
    echo $CRYPTO_SERVER_2 has more rows, aborting...
    exit 1
fi

mkdir -p ~/crypto-backup
ssh $CRYPTO_SERVER_1 pg_dump crypto | gzip > ~/crypto-backup/backup1.sql.gz
ssh $CRYPTO_SERVER_2 pg_dump crypto | gzip > ~/crypto-backup/backup2.sql.gz
ssh $CRYPTO_SERVER_2 psql -d postgres << EOF
UPDATE pg_database SET datallowconn = 'false' WHERE datname = 'crypto';
SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'crypto';
DROP DATABASE crypto;
EOF
ssh $CRYPTO_SERVER_2 createdb crypto
ssh $CRYPTO_SERVER_1 pg_dump crypto | ssh $CRYPTO_SERVER_2 psql -d crypto

diff <(ssh $CRYPTO_SERVER_1 pg_dump crypto ) <(ssh $CRYPTO_SERVER_2 pg_dump crypto )
rm ~/crypto-backup/backup2.sql.gz
