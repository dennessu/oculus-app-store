#!/bin/bash

./foreach.sh 'cd /var/silkcloud/apphost-cli-0.0.1-SNAPSHOT; ./startup.sh > /dev/null 2>&1' < appservers.txt

