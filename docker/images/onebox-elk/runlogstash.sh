#!/bin/bash
cd /var/elk/logstash && /var/elk/logstash/bin/logstash agent -f /config/logstash.conf
