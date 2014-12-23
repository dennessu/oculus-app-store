##PostgreSQL Bibble

###Emergency Issue
_Emergency Contacts_

- Shu Zhang（18616833301）
- Lirui Mao (13764630278)
- Eden Xia （13524324099）

_What's emergency issue?_
    
- Master is down, auto failover does not take effect 
- Master is online, however, API failed due to database connection issue 
- Issue occurred during failover/failback

###Database Role
`MASTER` `SLAVE` `BCP` `REPLICA`

- MASTER => SLAVE (Streaming replication + Archive log shipping)

- MASTER => BCP (Streaming replication + Archive log shipping)

- MASTER => REPLICA (Londiste replication) 

###PGBouncer

- Primary PGBouncer (run on MASTER server)
- Secondary PGBouncer (run on SLAVE server)
- `cat /var/ops/pgbouncer` shows which server the pgbouncer points to

###Log

__PostgreSQL Log__
    
    /var/silkcloud/logs/postgresql/postgresql-*.log 
    
__PGBouncer Log__
    
    /var/silkcloud/postgresql/pgbouncer-*.log 
    
__Londiste Log__
    
    /var/silkcloud/logs/*.log 
    
###Configuration File
__PostgreSQL Main Configuration__
    
    /var/silkcloud/postgresql/data/postgres.conf

__PostgreSQL HBA Configuration__
    
    /var/silkcloud/postgresql/data/pg_hba.conf

__PGBouncer Configuration__
    
    /var/silkcloud/postgresql/pgbouncer/*.conf
    
__Longdiste Configuration__
    
    /var/silkcloud/postgresql/skytool/config/*.ini

__Server Role__
   
    /var/silkcloud/postgresql/role.conf

###Health Check

__PostgreSQL Health Check__
    
- lsof -i tcp:5432

__PGBouncer Health Check__
    
- lsof -i tcp:6543

__Smoke Test__

- Normal state
    - /var/silkcloud/pgha/test/test_master2slave.sh

- Failover state
    - /var/silkcloud/pgha/test/test_master2slave.sh

###Restart Service

__Restart All Service__

 - /var/silkcloud/pgha/remedy/remedy_master.sh
 - /var/silkcloud/pgha/remedy/remedy_slave.sh
 - /var/silkcloud/pgha/remedy/remedy_bcp.sh
 - /var/silkcloud/pgha/remedy/remedy_replica.sh

__Restart PGBouncer Service__

- Point to MASTER database 
    - /var/silkcloud/pgha/pgbouncer/pgbouncer_master.sh

- Point to SLAVE database
    - /var/silkcloud/pgha/pgbouncer/pgbouncer_slave.sh

###Auto Failover
    Zabbix detects MASTER datbase 5432 port periodically. If it doesn't feel well, will take action as follows:
    
    - [MASTER] /var/silkcloud/pgha/pgbouncer/pgbouncer_slave.sh
    - [SLAVE] /var/silkcloud/pgha/pgbouncer/pgbouncer_slave.sh
    
    After that, all traffic will be redirected to SLAVE database which is still in standby mode.
    
    All READ calls still work
    All WRITE calls failed


###Planned Failover
- [SLAVE] /var/silkcloud/pgha/switchover/failover.sh
- [SLAVE] /var/silkcloud/pgha/test/test_slave2master.sh

###Unplanned Failover
- [SLAVE] /var/silkcloud/pgha/util/unplanned_failover.sh

###Repair Master
- [MASTER] /var/silkcloud/pgha/util/repair_master.sh

###Repair Replica
- [REPLICA] /var/silkcloud/pgha/util/repair_replica.sh

###Repair Slave
- [MASTER] /var/silkcloud/pgha/util/base_backup.sh
- [SLAVE] /var/silkcloud/pgha/setup/setup_slave.sh

###Repair BCP
- [MASTER] /var/silkcloud/pgha/util/base_backup.sh
- [BCP] /var/silkcloud/pgha/setup/setup_bcp.sh

###Remedy Strategy
__MASTER remedy__

- master active & slave unknown (maybe down) 
    - start master db
    - start londiste
    - pgbouncer => master

- master active & slave active (split brain)
    - don't start master db
    - don't start londiste
    - pgbouncer => slave

- master active & slave standby (normal case)
    - start master db
    - start londiste
    - pgbouncer => master

- master standby & slave unknown (rare case)
    - start master db
    - don't start londiste
    - don't start pgbouncer

- master standby & slave active (normal failover case)
    - start master db
    - don't start londiste
    - pgbouncer => slave

- master standby & slave standby (rare case)
   - start master db
   - don't start londiste
   - don't start pgbouncer

__SLAVE remedy__

   - slave active & master unknown (maybe down)
   - start slave db
   - start londiste
   - pgbouncer => slave

- slave active & mater active (split brain)
    - start slave db
    - don’t start londiste
    - pgbouncer => slave

- slave active & master standby (normal failover case)
    - start slave db
    - start londiste
    - pgbouncer => slave

- slave standby & master unknown (rare case)
    - start slave db
    - don't start londiste
    - don't start pgbouncer

- slave standby & mater active (normal case)
    - start slave db
    - don't start londiste
    - pgbouncer => master

- slave standby & master standby (rare case)
    - start slave db
    - don't start londiste
    - don't start pgbouncer