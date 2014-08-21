#!/bin/bash

export MASTER_HOST=10.20.16.50
export SLAVE_HOST=10.20.20.50
export REPLICA_HOST=10.20.22.50
export BCP_HOST=10.11.22.51

export REPLICA_DATABASES=('postgres' 'billing' 'ewallet' 'fulfilment' 'order' 'payment' 'sharding' 'subscription')
