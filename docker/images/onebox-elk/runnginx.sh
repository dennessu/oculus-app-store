#!/bin/bash
set -e

# set kibana config.js, fill in the port
sed -e "s/^[ \t]*elasticsearch:.*/elasticsearch: \"http:\/\/\"+window.location.hostname+\":$KIBANA_PORT\",/" -i \
  /var/elk/kibana/config.js

/usr/sbin/nginx -c /etc/nginx/nginx.conf
