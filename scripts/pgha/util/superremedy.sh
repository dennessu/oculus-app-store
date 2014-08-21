#!/bin/bash
sudo mkdir -p /var/run/postgresql
sudo chown -R silkcloud:silkcloud /var/run/postgresql
exec su -l silkcloud -c "/var/silkcloud/pgha/util/remedy.sh"
