#!/bin/bash

$LOGSTASH_CONFIG_FILE=/config/logstash.conf
if [ ! -f $LOGSTASH_CONFIG_FILE ]; then
  echo "$LOGSTASH_CONFIG_FILE not found, please check your volume settings."
  exit 1
fi
