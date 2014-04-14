#!/bin/sh
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

if [ -z "$*" ]; then
    GRADLE_CMD="gradle install -x test"
else
    GRADLE_CMD="gradle $*"
fi
function run_gradle {
    if [ -x "$1/$1-spec" ]; then
        pushd $1/$1-spec
        $GRADLE_CMD
        popd
    fi
}

while read p; do
    if [ ! -z "$p" ]; then
        run_gradle $p
    fi
done < dirs
