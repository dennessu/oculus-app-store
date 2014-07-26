#!/usr/bin/env bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.
DIR="$( cd "$( dirname "$0" )" && pwd )"

usage() { echo "Usage: $0 [-p <apphost-cli|apphost-rest|apphost-crypto|apphost-jobs|apphost-identity>] [<environment>]" 1>&2; exit 1; }

while getopts ":p:" o; do
    case "$o" in
        p)
            profile=${OPTARG}
            case "${OPTARG}" in
                apphost-cli|apphost-rest|apphost-crypto|apphost-jobs|apphost-identity)
                    ;;
                *)
                    usage
                    exit 1
                    ;;
            esac
            ;;
        *)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

environment=$1

config_file=/etc/silkcloud/configuration.properties

# check environment
if [[ -z "$environment" ]]; then
    if ! grep '^environment=[a-zA-Z0-9_\-]\+' $config_file > /dev/null 2>&1; then
        echo "ERROR: environment is not set!" 1>&2
        usage
        exit 1
    else
        environment_from_config=`grep '^environment=[a-zA-Z0-9_\-]\+' $config_file | sed 's/^environment=//'`
    fi
fi

# check profile
# default to apphost-cli on onebox
effective_environment=${environment:-$environment_from_config}
if [[ "$effective_environment" == "onebox" || "effective_environment" == "int1box" ]]; then
    profile=${profile:-apphost-cli}
fi

# otherwise check startup profile from configuration.properties
if [[ -z "$profile" ]]; then
    if ! grep '^profile=[a-zA-Z0-9_\-]\+' $config_file > /dev/null 2>&1; then
        echo "ERROR: profile is not set!" 1>&2
        usage
        exit 1
    else
        profile=`grep '^profile=[a-zA-Z0-9_\-]\+' $config_file | sed 's/^profile=//'`
    fi
fi
echo Set startup profile to $profile

export APPHOST_OPTS=""

# if environment was read from command line parameter, add parameters
if [[ -n "$environment" ]]; then
    export APPHOST_OPTS="$APPHOST_OPTS -Denvironment=$environment"
fi

if [[ "$OSTYPE" == "linux-gnu" ]]; then
    mkdir -p /var/silkcloud/logs
else
    mkdir -p $DIR/logs
fi

apphost_opts_var=`echo ${profile}_OPTS | tr '[:lower:]-' '[:upper:]_'`

# customize APPHOST_*_OPTS here
export $apphost_opts_var="$APPHOST_OPTS"
exec $DIR/bin/$profile
