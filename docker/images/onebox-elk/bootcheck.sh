#!/bin/bash

if [ "$KIBANA_PORT" = "9200" ]; then
  echo "9200 is used by elasticsearch, please use another port."
  exit 1
fi

if ! [[ $KIBANA_PORT =~ ^[1-9][0-9]*$ ]] ; then
  echo "KIBANA_PORT is invalid: $KIBANA_PORT"
  exit 1
fi

$LOGSTASH_CONFIG_FILE=/config/logstash.conf
if [ ! -f $LOGSTASH_CONFIG_FILE ]; then
  echo "$LOGSTASH_CONFIG_FILE not found, please check your volume settings."
  exit 1
fi
