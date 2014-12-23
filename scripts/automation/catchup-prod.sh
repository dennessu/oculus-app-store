#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
pushd $DIR

set -e

. ./readargs.sh

# optional: ./reset-config.sh
# in case of new db servers: ./setup-dbauth.sh

# used to catch-up green/blue with the other part
# db is already setup at this stage, so upgrade apps only

./reset-config.sh
./upgrade-apps.sh
echo "Please verify new apps are taking traffic. Press enter to continue."
read

./check-versions.sh
