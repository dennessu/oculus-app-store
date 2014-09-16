#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

. ./readargs.sh

# optional: ./reset-config.sh
# in case of new db servers: ./setup-dbauth.sh

./setup-couch.sh
echo "Please verify cloudant is properly updated."
read
./upgrade-dbs.sh
echo "Please verify db upgrade is properly updated."
read
echo "Please verify config is fine to be reset."
read
./reset-config.sh
echo "Reset config finished."
./upgrade-utils.sh
echo "Please verify new utils are working properly."
read
./upgrade-apps.sh
echo "Please verify new apps are taking traffic. Press enter to continue."
read
./dataloader.sh

./check-versions.sh

