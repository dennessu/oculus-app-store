#!/bin/sh
if [ -z "$*" ]; then
    GRADLE_CMD="gradle install -x test"
else
    GRADLE_CMD="gradle $*"
fi
function run_gradle {
    if [ -x "$1/$1-spec" ]; then
        pushd $1/$1-spec
        $GRADLE_CMD || exit 1
        popd
    fi
}

while read p; do
    if [ ! -z "$p" ]; then
        run_gradle $p || exit 1
    fi
done < dirs