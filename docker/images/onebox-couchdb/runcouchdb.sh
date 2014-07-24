#!/bin/sh
mkdir -p /var/run/couchdb
chown couchdb:couchdb /var/run/couchdb

chown -R couchdb /var/lib/couchdb
chown -R couchdb /var/log/couchdb

exec /sbin/setuser couchdb /usr/bin/couchdb
