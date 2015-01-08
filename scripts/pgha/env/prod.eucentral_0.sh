#!/bin/bash

export MASTER_HOST=10.110.16.50
export SLAVE_HOST=10.110.20.50
export REPLICA_HOST=10.110.22.50
export BCP_HOST=10.11.22.52

export REPLICA_DATABASES=('postgres' 'billing' 'ewallet' 'fulfilment' 'order' 'payment' 'sharding' 'subscription')

# pgsql conf for powerful machines
export MAX_CONNECTIONS=5000
export SHARED_BUFFERS='4GB'
export MAINTENANCE_WORK_MEM='1GB'
export EFFECTIVE_CACHE_SIZE='10GB'
export CHECKPOINT_SEGMENTS='64'
export CHECKPOINT_COMPLETION_TARGET='0.8'
