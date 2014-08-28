#!/bin/bash
set -e

# set kibana config.js, fill in the port
sed -e "s/^[ \t]*elasticsearch:.*/elasticsearch: \"http:\/\/\"+window.location.hostname+\":$KIBANA_PORT\",/" -i \
  /var/elk/kibana/config.js

# change kibana_site.conf, fill in the port
sed -e "s/listen[ \t]*[*][:][[:digit:]]*;/listen *:$KIBANA_PORT;/" -i /etc/nginx/sites-enabled/kibana_site.conf

/usr/sbin/nginx -c /etc/nginx/nginx.conf
