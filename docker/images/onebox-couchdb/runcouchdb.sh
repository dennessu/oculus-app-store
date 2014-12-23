#!/bin/sh
mkdir -p /var/run/couchdb

# workaround for boot2docker bug: https://github.com/boot2docker/boot2docker/issues/581
usermod -u 1000 couchdb

chown couchdb:couchdb /var/run/couchdb

chown -R couchdb /var/lib/couchdb
chown -R couchdb /var/log/couchdb

exec /sbin/setuser couchdb /usr/bin/couchdb
