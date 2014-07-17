#!/bin/bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    echo "ERROR: script ${BASH_SOURCE[0]} is not being sourced ..." 1>&2
    exit 1
fi

# Stop the execution whenever there is an error
set -e

# Get root dir of current git working copy
function rootdir {
    echo "$(git rev-parse --show-toplevel)"
}
function root {
    cd `rootdir`
}

