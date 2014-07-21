#!/bin/bash
while read p; do
	if [[ ! -z "$p" && ! $p == \#* ]]; then
		sudo -u silkcloud ssh -n $p "$@"
	fi
done

