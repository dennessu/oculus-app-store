#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

set -e

t0=`date +%s`

if [ -z "$*" ]; then
    GRADLE_CMD="gradle --continue"
else
    GRADLE_CMD="gradle $* --continue"
fi
function run_gradle {
    pushd $1
    $GRADLE_CMD
    popd
}

cd `git rev-parse --show-toplevel`
run_gradle gradle/bootstrap
$GRADLE_CMD

t1=`date +%s`
echo Build Total Elapsed: $[$t1-$t0] seconds

