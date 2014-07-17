#!/bin/bash
ssh_script=`cat`
for p in `cat $1`; do
	if [[ ! -z "$p" && ! $p == \#* ]]; then
		echo running on $p
		sudo -u silkcloud ssh -n $p "$ssh_script"
	fi
done

