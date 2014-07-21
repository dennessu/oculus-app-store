#!/bin/bash
for p in `cat $1`; do
	if [[ ! -z "$p" && ! $p == \#* ]]; then
		sudo -u devops ssh $p
	fi
done

