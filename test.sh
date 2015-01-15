#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

set -e

OPTIND=1
reduce_output=0

# -r -> reduce_output:
#   will not print to console if all test cases are passing
#   if there are failures, then will print all the test log
#   this is due to log amount is too big for web tool to display
while getopts "r" opt; do
  case "$opt" in
  r) reduce_output=1
    ;;
  esac
done

t0=`date +%s`

cd `git rev-parse --show-toplevel`
./shutdown.sh || true

function cleanup {
    cd `git rev-parse --show-toplevel`
    ./shutdown.sh

    t1=`date +%s`
    echo Test Total Elapsed: $[$t1-$t0] seconds
}

function cleanupTrap {
    cleanup
    echo !!!!!!!!!!!!!!!!
    echo Test has ERRORs!
    echo !!!!!!!!!!!!!!!!
    if [[ "$reduce_output" = "1" ]]; then
      echo "Print the supercycle_suite.log:"
      cat scripts/unittests/logs/supercycle_suite.log
    fi
}

trap cleanupTrap EXIT INT TERM
. ./setupproxy.sh
./startup.sh

# make sure the error code of python will be returned
set -o pipefail

echo "apphost started, start running test cases"
cd ./scripts/unittests/
mkdir -p logs
if [[ "$reduce_output" = "1" ]]; then
  python ./supercycle_suite.py 2>&1 >logs/supercycle_suite.log
else
  python ./supercycle_suite.py 2>&1 | tee logs/supercycle_suite.log
fi

trap - EXIT INT TERM
cleanup

echo -------------------
echo Test is Successful!
echo -------------------

