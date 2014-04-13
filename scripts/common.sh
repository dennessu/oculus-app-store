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

# Check the global PGSQL user name override
if [[ -z "$PGSQL_USER" ]]; then
    # set the default username
    PGSQL_USER=postgres
fi

# The psql helper function. Named it psqlh to make it possible to call the original psql exe.
# This function helps to standardize the output and behavior to psql.
#
# Usage: psqlh $db $other_inputs
#
# It automatically adds -q -X parameters.
# -q    Quiet model. Prevent annoying outputs by DDL commands
# -X    Ignore .psqlrc to prevent behavior difference caused by .psqlrc
#
# In windows, the DB and user must be put as last parameters and in linux/mac they 
# can be put before the options. psqlh standardize this by placing db as the first parameter,
# and always connect using postgres as user name.
#
# For more information refer to: 
# http://www.postgresql.org/docs/9.3/static/app-psql.html
# http://petereisentraut.blogspot.com/2010/03/running-sql-scripts-with-psql.html
function psqlh {
    db=$1
    user=$PGSQL_USER
    shift
    psql -d $db -U $user -qX "$@"
}

# Connect the postgres DB. 
# 
# Usage: psqlh_d $other_inputs
# 
# d stands for default
function psqlh_d {
    psqlh postgres "$@"
}
