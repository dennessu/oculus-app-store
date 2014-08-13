#!/bin/bash
set -e

function error_exit {
    echo
    echo "$@"
    exit 1
}

trap "error_exit 'Error happened, failed to init dbs'" ERR
shopt -s expand_aliases
alias die='error_exit "Error ${0}(@`echo $(( $LINENO - 1 ))`):"'

# start rinetd if it is not running, this is useful in on-demand docker run
if ! pgrep rinetd; then
  /etc/service/rinetd/run&
  sleep 2
fi
# check if the database ports can be reached
# this cannot be put into checkdockerenv.sh because that script would be executed before rinetd.sh
nc -z 127.0.0.1 11211 || die "cannot reach memcached via port 11211, cannot continue"
nc -z 127.0.0.1 5432 || die "cannot reach psql via port 5432, cannot continue"
nc -z 127.0.0.1 5984 || die "cannot reach couchdb via port 5984, cannot continue"

source /etc/my_init.d/checkdockerenv.sh

echo "#### preparing databases....."
SC_APP_DIR=/var/silkcloud/apphost

python $SC_APP_DIR/dbsetup/memcache/flush-all.py
echo "## memcached cleared."

$SC_APP_DIR/dbsetup/liquibase/createdb.sh -env:$SC_ENVIRONMENT -key:D58BA755FF96B35A6DABA7298F7A8CE2
$SC_APP_DIR/dbsetup/liquibase/updatedb.sh -env:$SC_ENVIRONMENT -key:D58BA755FF96B35A6DABA7298F7A8CE2
echo "## psql dbs created."

python $SC_APP_DIR/dbsetup/couchdb/couchdbcmd.py createdbs $SC_ENVIRONMENT --prefix=$SC_CLOUDANT_PREFIX --yes --key=D58BA755FF96B35A6DABA7298F7A8CE2
echo "## couchdb/cloudant dbs created."

/var/silkcloud/apphost/dataloader.sh masterkey
echo "## masterkey loaded."
/var/silkcloud/apphost/dataloader.sh
echo "## domain data loaded."

echo "#### finished preparing databases."
