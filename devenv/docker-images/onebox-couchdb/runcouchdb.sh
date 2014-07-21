#!/bin/sh
mkdir -p /var/run/couchdb
chown couchdb:couchdb /var/run/couchdb

exec /sbin/setuser couchdb /usr/bin/couchdb
