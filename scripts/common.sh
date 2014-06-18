#!/bin/sh
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.

# The common script headers. Defined useful functions and helpers for cross platform stuff.
# To work around \r issue in cygwin (windows), this file should be included immediately after the #!/bin/sh line.
# for example:
# #!/bin/sh
# source $(git rev-parse --show-toplevel)/scripts/common.sh; # this comment is needed, see common.sh for detail

# The comment is needed to prevent the trailing \r screw up the bash

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

