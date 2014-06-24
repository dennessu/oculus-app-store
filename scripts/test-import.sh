#!/bin/bash
(set -o igncr) 2>/dev/null && set -o igncr; # ignore \r in windows. The comment is needed.

maximum=10 # max tasks

myfifo=$(mktemp --dry-run)
mkfifo --mode=0700 $myfifo
exec 3<>$myfifo # hook pipe up to descriptor 3 for read/write
rm -f $myfifo # pipe remains intact until closed

running=0
while read line ; do
    while (( running >= maximum )) ; do
        if read -u 3 cpid ; then
            wait $cpid
            (( --running ))
        fi
    done
    ( # child process
        curl -v -X POST -d "$line" -H 'Content-Type: application/json' 'http://localhost:8080/v1/imports'
        echo $BASHPID 1>&3
    ) &
    (( ++running ))
done
wait

