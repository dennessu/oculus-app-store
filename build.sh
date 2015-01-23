#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

set -e

t0=`date +%s`

if [ -z "$*" ]; then
    GRADLE_CMD="gradle --no-daemon silkcloud --continue"
elif [ "$1" == "-f" ]; then
    # fast mode
    shift
    GRADLE_CMD="gradle --no-daemon -x build -x distZip publishToMavenLocal silkcloud $* --continue"
else
    GRADLE_CMD="gradle --no-daemon $* --continue"
fi
function run_gradle {
    pushd $1
    $GRADLE_CMD
    popd
}

cd `git rev-parse --show-toplevel`
pushd gradle/bootstrap

lastbuildcommit=
currentcommit=$(git rev-list -1 HEAD -- ".")
if [[ -f ".lastbuildcommit" ]]; then
    lastbuildcommit=`cat .lastbuildcommit`
fi
if [[ "$currentcommit" != "$lastbuildcommit" ]]; then
    gradle --no-daemon -x build
    echo $currentcommit>.lastbuildcommit
fi
popd

# build other projects
$GRADLE_CMD

t1=`date +%s`
echo Build Total Elapsed: $[$t1-$t0] seconds

