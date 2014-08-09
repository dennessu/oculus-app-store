#!/bin/bash
ssh_script=`cat`
for var in "$@"
do
    for p in `cat $var | sed '/^#/ d'`; do
        if [[ -n "$p" ]]; then
            echo running on $p
            ssh -o "StrictHostKeyChecking no" -n $p "$ssh_script"
        fi
    done
done
