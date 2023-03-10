#!/bin/bash

#https://oculus.atlassian.net/wiki/pages/viewpage.action?spaceKey=SER&title=AWS-test+servers

export MASTER_HOST=10.32.16.51
export SLAVE_HOST=10.32.20.51
export REPLICA_HOST=10.32.22.51

export REPLICA_DATABASES=('postgres' 'billing' 'ewallet' 'fulfilment' 'order' 'payment' 'sharding' 'subscription')
