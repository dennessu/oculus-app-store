#!/bin/bash
set -x
while read p; do
	if [[ ! -z "$p" && ! $p == \#* ]]; then
		sudo -u silkcloud rsync -rtvu ~/main/apphost/apphost-cli/build/install/apphost-cli/lib/ $p:/var/silkcloud/apphost-cli-0.0.1-SNAPSHOT/lib/
	fi
done < appservers.txt

