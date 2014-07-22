#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR
set -e

CRYPTO_SERVER_1=10.24.34.10
CRYPTO_SERVER_2=10.24.38.10

ROWCOUNT1=`ssh $CRYPTO_SERVER_1 'psql -d crypto -c "select count(*) from master_key;"' | egrep '^[[:blank:]]*[[:digit:]]+[[:blank:]]*$' | sed 's/[ \t]*//'`
ROWCOUNT2=`ssh $CRYPTO_SERVER_2 'psql -d crypto -c "select count(*) from master_key;"' | egrep '^[[:blank:]]*[[:digit:]]+[[:blank:]]*$' | sed 's/[ \t]*//'`

echo Found $ROWCOUNT1 master keys in $CRYPTO_SERVER_1
echo Found $ROWCOUNT2 master keys in $CRYPTO_SERVER_2

if [ "$ROWCOUNT2" -gt "$ROWCOUNT1" ]; then
    echo $CRYPTO_SERVER_2 has more rows, aborting...
    exit 1
fi

ssh $CRYPTO_SERVER_1 pg_dump crypto | gzip > backup1.sql.gz
ssh $CRYPTO_SERVER_2 pg_dump crypto | gzip > backup2.sql.gz
ssh $CRYPTO_SERVER_2 psql -d postgres << EOF
UPDATE pg_database SET datallowconn = 'false' WHERE datname = 'crypto';
SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'crypto';
DROP DATABASE crypto;
EOF
ssh $CRYPTO_SERVER_2 createdb crypto
ssh $CRYPTO_SERVER_1 pg_dump crypto | ssh $CRYPTO_SERVER_2 psql -d crypto

diff <(ssh $CRYPTO_SERVER_1 pg_dump crypto ) <(ssh $CRYPTO_SERVER_2 pg_dump crypto )
rm backup2.sql.gz

