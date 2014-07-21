#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail

if [ -z "$*" ]; then
    GRADLE_CMD="gradle clean build install"
else
    GRADLE_CMD="gradle $*"
fi
function run_gradle {
    pushd $1
    $GRADLE_CMD
    popd
}

while read p; do
    if [[ ! -z "$p" && ! $p == \#* ]]; then
        run_gradle $p
    fi
done < dirs
