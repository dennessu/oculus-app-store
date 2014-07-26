#!/bin/bash
ssh_script=`cat`
for var in "$@"
do
    for p in `cat $var`; do
        if [[ ! -z "$p" && ! $p == \#* ]]; then
            echo running on $p
            ssh -n $p "$ssh_script"
        fi
    done
done
