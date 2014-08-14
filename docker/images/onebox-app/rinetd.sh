#!/bin/bash
pkill rinetd
rinetd -c /etc/service/rinetd/rinetd.conf -f
