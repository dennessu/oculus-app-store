#!/bin/bash

#https://oculus.atlassian.net/wiki/pages/viewpage.action?spaceKey=SER&title=AWS-test+servers

export MASTER_HOST=10.200.16.50
export SLAVE_HOST=10.200.20.50
export REPLICA_HOST=10.200.22.50
export BCP_HOST=10.202.22.51

export REPLICA_DATABASES=('postgres' 'billing' 'ewallet' 'fulfilment' 'order' 'payment' 'sharding' 'subscription')
