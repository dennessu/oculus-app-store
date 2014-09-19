#!/bin/bash

export MASTER_HOST=10.14.16.50
export SLAVE_HOST=10.14.20.50
export REPLICA_HOST=10.14.22.50
export BCP_HOST=10.24.22.51

export REPLICA_DATABASES=('postgres' 'billing' 'ewallet' 'fulfilment' 'order' 'payment' 'sharding' 'subscription')
