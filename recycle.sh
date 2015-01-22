#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail
set -e

function gradleWithRetry {
    $1 || (gradle --stop; $1)
}

t0=`date +%s`

gradleWithRetry "gradle --daemon -x clean -x build"
`rootdir`/shutdown.sh &
find . -name '*.jar' -exec cp {} `rootdir`/apphost/apphost-cli/build/install/apphost-cli/lib \;

wait

if [[ "$1" == "-p" ]]; then
    # patch mode
    echo Patch apphost-cli done.
else
    `rootdir`/startup.sh -f
fi

t1=`date +%s`
echo Quick Recycle Total Elapsed: $[$t1-$t0] seconds
