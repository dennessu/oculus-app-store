#!/bin/bash
source "$(git rev-parse --show-toplevel)/scripts/common.sh"; # this comment is needed, see common.sh for detail
root

personalConfigFile=./apphost/config-data/src/main/resources/junbo/conf/onebox/personal.properties
# append \n to eof if not exists.
chmod +w "$personalConfigFile"
sed -i.bak -e '$a\' "$personalConfigFile"
if [[ -f "${personalConfigFile}.bak" ]]; then
    chmod +w "${personalConfigFile}.bak"
    rm "${personalConfigFile}.bak"
fi

# remove the file generated due to bug in this script.
if [[ -f "${personalConfigFile}-e" ]]; then 
    rm "${personalConfigFile}-e"
fi

if ! (curl -X HEAD --connect-timeout 3 www.facebook.com) >/dev/null 2>&1; then
    # set proxy
    if [[ -f "$personalConfigFile" ]]; then
        facebookProxy=`cat $personalConfigFile | grep '^facebook.proxy=' | awk -F= '{gsub(/^[ \t]+/, "", $2); print $2}'`
    fi
    if [[ -z "$facebookProxy" ]]; then
        echo 'facebook.proxy=http://silkcloud:#Bugs4$1@127.0.0.1:13128' >> $personalConfigFile
        facebookProxy=`cat $personalConfigFile | grep '^facebook.proxy=' | awk -F= '{gsub(/^[ \t]+/, "", $2); print $2}'`
        if [[ -z "$facebookProxy" ]]; then
            echo Error setting facebookProxy
            exit 1
        fi
    fi
    echo "facebook proxy is set to $facebookProxy"
    if (cat $personalConfigFile | grep '^[ \t]*[^ \t#].*127\.0\.0\.1:13128') >/dev/null 2>&1; then
        `rootdir`/scripts/start-proxy.sh
    fi
else
    echo "No need to set proxy"
    # remove proxy
    if [[ -f "$personalConfigFile" ]]; then
        facebookProxy=`cat $personalConfigFile | grep '^facebook.proxy=' | awk -F= '{gsub(/^[ \t]+/, "", $2); print $2}'`
    fi
    if [[ "$facebookProxy" == 'http://silkcloud:#Bugs4$1@127.0.0.1:13128' ]]; then
        sed -i.bak -e '/^facebook.proxy=/d' "$personalConfigFile"
        if [[ -f "${personalConfigFile}.bak" ]]; then
            chmod +w "${personalConfigFile}.bak"
            rm "${personalConfigFile}.bak"
        fi
        echo "facebook proxy is removed"
    fi
fi

