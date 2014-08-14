#!/bin/sh

MAX_MEM=${MAX_MEM:-"64"}
MAX_CONN=${MAX_CONN:-"1024"}

exec /sbin/setuser memcache /usr/bin/memcached -m $MAX_MEM -l 0.0.0.0 -c $MAX_CONN
