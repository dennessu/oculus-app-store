# New Region Configuration and Deployment Guide

## Setup New Machines

Follow the New DC build out Guide to setup new machines in new region.

This step will output a server list in the new region.

For example:

| Role      | Group     | IP             |
| -------   | --------- | -------------- |
| app       | green     | 10.110.8.50    |
| app       | green     | 10.110.8.52    |
| app       | green     | 10.110.8.54    |
| app       | green     | 10.110.8.56    |
| app       | green     | 10.110.12.50   |
| app       | green     | 10.110.12.52   |
| app       | green     | 10.110.12.54   |
| app       | blue      | 10.110.8.51    |
| app       | blue      | 10.110.8.53    |
| app       | blue      | 10.110.8.55    |
| app       | blue      | 10.110.12.51   |
| app       | blue      | 10.110.12.53   |
| app       | blue      | 10.110.12.55   |
| app       | blue      | 10.110.12.56   |
| cryptoapp | green     | 10.110.32.10   |
| cryptoapp | green     | 10.110.32.12   |
| cryptoapp | green     | 10.110.32.14   |
| cryptoapp | green     | 10.110.36.10   |
| cryptoapp | green     | 10.110.36.12   |
| cryptoapp | blue      | 10.110.32.11   |
| cryptoapp | blue      | 10.110.32.13   |
| cryptoapp | blue      | 10.110.36.11   |
| cryptoapp | blue      | 10.110.36.13   |
| cryptoapp | blue      | 10.110.36.14   |
| memcached |           | 10.110.10.50   |
| memcached |           | 10.110.10.51   |
| utility   |           | 10.110.11.50   |
| cryptodb  |           | 10.110.34.10   |
| cryptodb  |           | 10.110.38.10   |
| dbmaster  |           | 10.110.16.50   |
| dbslave   |           | 10.110.20.50   |
| dbreplica |           | 10.110.22.50   |
| dbbcp     |           | 10.11.22.52    |

**Notes**:
  1. In PROD there is green/blue groups for app servers and crypto-app servers. Sewer will point to one of these two regions as the PROD and another as PREPROD. In staging there is no grouping in the app roles.
  1. The setup above includes one shared in the region. dbslave is readonly warm standby of the dbmaster. dbreplica is the replication of dbmaster. The difference is that you are able to create indexes and/or temp tables in dbreplica and perform complex SQL queries for livesite investigation.
  1. dbbcp is built in another region to have a backup of the DB out of the region. Used in disaster recovery.

This step will also output a ELB URL for the new region. For example (the URL format may not match the example below):
  * JVM Blue ELB: `https://jvm-prd-eu1-blu.oculus.com`
  * JVM Green ELB: `https://jvm-prd-eu1-grn.oculus.com`
  * Crypto Blue ELB: `https://crypto-prd-eu1-blu.oculus.com`
  * Crypto Green ELB: `https://crypto-prd-eu1-grn.oculus.com`

These values will be used in the configurations below.

## Configuration Change
Before the binary can run in new region, the configuration for the environment needs to be changed. The changes are in [junbo/main repo](http://github.com/junbo/main). This repo is mirroed to [OculusVR/shanghai-main](https://github.com/OculusVR/shanghai-main)

This section takes Europe as an example new region. The region name in the configurations is a string. We'll use `europe` in the example.

In order to make configuration changes, you need to know the `CRYPTO_KEY` of the environment.

In your terminal session, load `CRYPTO_KEY` in a secure way:
```
read -s CRYPTO_KEY
```
There is no prompt and just input the `CRYPTO_KEY` for the environment. It is available as `$CRYPTO_KEY`. Remember to exit your terminal/ssh session and clear the command history after the work is done.

The configuration files are found in the following directory in the source code:
[`apphost/config-data/src/main/resources/junbo/conf`](https://github.com/junbo/main/blob/master/apphost/config-data/src/main/resources/junbo/conf)
The commands below assumes you are at the root of the git repository you cloned locally. There are some other locations for configuration files. In that case the path will be prefixed with `$ROOT` to indicate this is from the root of the repository.

When the configuration change is done, commit and put the change but don't deploy to existing regions until the new region is deployed.

### Modify Cloudant DB Configurations

###### Modify [`prod/cloudant.properties`](https://github.com/junbo/main/blob/master/apphost/config-data/src/main/resources/junbo/conf/prod/cloudant.properties)

The URLs contains password and is encrypted. To modify the data, use `CRYPTO_KEY` to decrypt the value and re-encrypt.

1. Decrypt the cloudant URL. Find the `common.cloudant.url.encrypted` from the configuration file. Name it `encrypted_value` and run the following command to decrypt it.
  ```
  cd scripts
  ./AESCipher.py decrypt $CRYPTO_KEY $encrypted_value
  ```
  The command will print the decrypted value. It is something like:
  ```
 https://$api_key:oculus-cni:$api_secret@oculus003.cloudant.com;us-west,https://$api_key:oculus-cni:$api_secret@oculus001.cloudant.com;us-east
  ```
  The $api_key and $api_secret is the actual API key used to access the cloudant. We hide them in this document. Please use the real value from the command output.

  **Note**: there are 4 configuration keys:
  * `common.cloudant.url.encrypted`
  * `common.cloudantWithSearch.url.encrypted`
  * `crypto.userkey.cloudant.url.encrypted`
  * `crypto.itemCryptoKey.cloudant.url.encrypted`

  They are of same value in staging and prod environments. They showed different values because when the configuration was first generated, we used automation to encrypt the value multiple times and got different results.

1. Modify the cloudant URL to include the new region. Assume the new region's cloudant URL is `oculus002.cloudant.com`, append the new region configuration `,https://$api_key:oculus-cni:$api_secret@oculus002.cloudant.com;europe`. The new configuration value is:
  ```
  https://$api_key:oculus-cni:$api_secret@oculus003.cloudant.com;us-west,https://$api_key:oculus-cni:$api_secret@oculus001.cloudant.com;us-east,https://$api_key:oculus-cni:$api_secret@oculus002.cloudant.com;europe
  ```
  Put the new value to the environment variable `decrypted_value`. Remember to properly escape the string if there is special characters.

1. Encrypt the value and replace the existing value in the configuration file.
  ```
  ./AESCipher.py encrypt $CRYPTO_KEY $decrypted_value
  ```
  The 4 configuration keys can be assigned the same encrypted value.


For staging, modify [`staging/cloudant.properties`](https://github.com/junbo/main/blob/master/apphost/config-data/src/main/resources/junbo/conf/staging/cloudant.properties) and note the cloudant account is `oculus-cni-ppe`.

###### Modify [`$ROOT/couchdb/conf/prod.json`](https://github.com/junbo/main/blob/master/couchdb/conf/prod.json)

Open the file modify the `authdbs` property to include the new oculus002 node for Europe.
```
{
  "dbs": {
    // ...
  },
  "apikeys": [
    // ...
  ],
  "authdbs": {
    "oculus-cni": [
    "oculus001", "oculus002", "oculus003"
    ]
  }
}
```
**Note**: The Europe node `oculus002` already exists in the file, so no edit for this file necessary. However if we are adding Asia or other region the new node needs to be included.

For staging, modify [`$ROOT/couchdb/conf/staging.json`](https://github.com/junbo/main/blob/master/couchdb/conf/staging.json) and note the cloudant account is `oculus-cni-ppe`.

### Postgre SQL Configuration

###### Modify [prod/db.properties](https://github.com/junbo/main/blob/master/apphost/config-data/src/main/resources/junbo/conf/prod/db.properties)

For example, change the billing DB from:

```
billing.db.jdbcUrls=\
jdbc:postgresql://10.11.16.50:6543|10.11.20.50:6543/billing;public;0..0;us-east,\
jdbc:postgresql://10.20.16.50:6543|10.20.20.50:6543/billing;public;0..0;us-west
```

To the value including Europe:

```
billing.db.jdbcUrls=\
jdbc:postgresql://10.11.16.50:6543|10.11.20.50:6543/billing;public;0..0;us-east,\
jdbc:postgresql://10.20.16.50:6543|10.20.20.50:6543/billing;public;0..0;us-west,\
jdbc:postgresql://10.110.16.50:6543|10.110.20.50:6543/billing;public;0..0;europe,\
```

Use the IP addresses of `dbmaster` and `dbslave` provided in **Setup New Machines** to build the URL.

Do the change to all other DBs except `crypto.db.jdbcUrls`.

For `crypto.db.jdbcUrls`, use the IP addresses of `cryptodb` provided in **Setup New Machines**.

**Note**: When adding the new values, please take care on the difference for each DBs. For example, the DB name is different and some DBs use shard range `0..255`.


For staging, modify [staging/db.properties](https://github.com/junbo/main/blob/master/apphost/config-data/src/main/resources/junbo/conf/staging/db.properties)

###### Add new region to [$ROOT/liquibase/conf](https://github.com/junbo/main/blob/master/liquibase/conf)

Add a new folder with name `<env>.<region>` in the folder above. For example, if we add europe to PROD, the folder will be `prod.europe`. For staging, the folder will be `staging.europe`.

Add confs for all DBs exist in other region. For example, you can copy from `prod.useast` as your template.

Modify config files other than `crypto` and update the `jdbc_url_0` to use the `dbmaster` IP address provided in **Setup New Machines**.

For example, the `billing.conf` become:

```
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = <encrypted-password>
jdbc_url_0 = jdbc:postgresql://10.110.16.50:6543/billing;public

```

For `crypto.conf`, use the `cryptodb` IP address for `jdbc_url_0` and `jdbc_url_1`.


###### Modify [$ROOT/liquibase/conf/prod.replica](https://github.com/junbo/main/blob/master/liquibase/conf/prod.replica)

We run liquibase against replica first to make sure new tables and columns are created in replica first. To do this the replica configuration in liquibase is needed.

Change the files except `crypto.conf`, `dualwrite.conf` and `index.conf`. These 3 DBs are not replicated.

For example, the current `billing.conf` is:

```
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..1
login_username = silkcloud
login_password.encrypted = <encrypted-password>

jdbc_url_0 = jdbc:postgresql://10.11.22.50:6543/billing;public
jdbc_url_1 = jdbc:postgresql://10.20.22.50:6543/billing;public

```

Make two changes:
  1. Increase shard_range by 1. Note the shard here doesn't mean the same thing as in the JVM configuration. It just means how many URLs exist in the jdbc_url_x configuration below. It can be removed in the future.
  1. Add another line and point to the replica DB in the new region. `jdbc_url_2 =  jdbc:postgresql://10.110.22.50:6543/billing;public`

The result is:
```

[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..2
login_username = silkcloud
login_password.encrypted = <encrypted-password>

jdbc_url_0 = jdbc:postgresql://10.11.22.50:6543/billing;public
jdbc_url_1 = jdbc:postgresql://10.20.22.50:6543/billing;public
jdbc_url_2 =  jdbc:postgresql://10.110.22.50:6543/billing;public
```

For staging, modify [$ROOT/liquibase/conf/staging.replica](https://github.com/junbo/main/blob/master/liquibase/conf/staging.replica)


###### Add new region to [$ROOT/scripts/pgha/env](https://github.com/junbo/main/blob/master/scripts/pgha/env)

PGHA is used to setup slave and replication.

Add several new files in the folder above:
  * `<env>.<region>_<shard>.sh`
  * `<env>.<region>_crypto_0.sh`
  * `<env>.<region>_crypto_1.sh`
For example, if we add Europe to PROD with 1 shard, the files will be:
  * `prod.europe_0.sh`
  * `prod.europe_crypto_0.sh`
  * `prod.europe_crypto_1.sh`

You can start by copying from `prod.useast*.sh` and modify the IP addresses.

  * `prod.europe_0.sh`
    1. Update `MASTER_HOST` to the `dbmaster` IP address provided in **Setup New Machines**.
    1. Update `SLAVE_HOST` to the `dbslave` IP address provided in **Setup New Machines**.
    1. Update `REPLICA_HOST` to the `dbreplica` IP address provided in **Setup New Machines**.
    1. Update `BCP_HOST` to the `dbbcp` IP address provided in **Setup New Machines**.
  * `prod.europe_crypto_0.sh`
    1. Update `MASTER_HOST` to the **first** `cryptodb` IP address provided in **Setup New Machines**.
  * `prod.europe_crypto_1.sh`
  1. Update `MASTER_HOST` to the **second** `cryptodb` IP address provided in **Setup New Machines**.

### Topology Configuration Change

Create a new folder for the new region under environment config folder:
[`apphost/config-data/src/main/resources/junbo/conf`](https://github.com/junbo/main/blob/master/apphost/config-data/src/main/resources/junbo/conf)

For PROD, since there is green/blue groups, we need to create two folders:
  * `prod.europe`
  * `prod.europe-blue`

For staging, create one folder:
  * `staging.europe`

Copy from an existing region as template. And modify the following properties:
  * `datacenter`: Fill in the new region name. In this example, the value is `europe`.
  * `subnet`: Fill in the new region's subnet. In this example, the value is `10.110.0.0/16`
  * `common.topo.datacenters`: Append with the new regions JVM URL in the same group. For staging, this property is not in per-region config file and need to be fixed later.
    * `prod.europe`: Append the JVM URL for PROD Europe green.
    * `prod.europe-blue`: Append the JVM URL for PROD Europe blue.
  * `common.topo.appservers`: Append the IP addresses of the app servers in the same region and group. For staging, this property is not in per-region config file and need to be fixed later.
  * `common.topo.otherservers`: Append the IP addresses of the cryptoapp servers and utility server. For staging, this property is not in per-region config file and need to be fixed later.
  * `clientproxy.*.url`: Fill in the value for the new regions JVM URL in the same group.
  * `clientproxy.crypto.url`: Fill in the value for new region's crypto service URL in the same group.
  * `common.memcached.servers`: Fill in the IP addresses of `memcached` role in the new region, split by space or ','. There is a pitfall. If you use \<LF> to make the memcached server configuration in multiple lines, do use ',' instead of space. i.e, you should write
  ```
  common.memcached.servers=\
  10.110.10.50:11211,\
  10.110.10.51:11211
  ```
  Instead of
  ```
  common.memcached.servers=\
    10.110.10.50:11211\
    10.110.10.51:11211
  ```

###### Update configuration for existing regions
For PROD we need to update the existing regions:
  * `prod.useast`
  * `prod.useast-blue`
  * `prod.uswest`
  * `prod.uswest-blue`
Open the `conf.properties` in these regions and update the following properties to include the new region with same group.
  * `common.topo.datacenters`
  * `common.topo.appservers`
  * `common.topo.otherservers`

For Staging, open `staging/topo.properties` and update the following properties to include the new region:
  * `common.topo.datacenters`
  * `common.topo.appservers`
  * `common.topo.otherservers`

### Update Ansible Server Lists

Modify Ansible hosts file in [junbo/commops repo](https://github.com/junbo/commops). This repo is mirrored to [shanghai-commops](https://github.com/OculusVR/shanghai-commops).

Modify [`prod.hosts`](https://github.com/junbo/commops/blob/master/ansible/prod.hosts) and add the new servers. We give a short name "eu" to the new region. To fill in the information you need to refer to the full server list in step **New DC build out Guide**. Note the hostname should not contain the `srv-` prefix.

```
# eu servers
[eu:children]
eu-nat
eu-vpn
eu-app
eu-memcached
eu-utl
eu-db
eu-cryptoapp

[eu-nat]
10.110.108.10 hostname=prd-pub-utl-ec1a-nat01
10.110.104.10 hostname=prd-pub-utl-ec1b-nat02

[eu-vpn]
10.110.108.11 hostname=prd-pub-utl-ec1a-vpn02

# app servers
[eu-app:children]
eu-app-green
eu-app-blue

[eu-app-green:children]
eu-app-green-1
eu-app-green-2

[eu-app-green-1]
10.110.8.50 hostname=prd-pri-app-ec1a-app001
10.110.8.52 hostname=prd-pri-app-ec1a-app005
10.110.8.54 hostname=prd-pri-app-ec1a-app009
10.110.8.56 hostname=prd-pri-app-ec1a-app013

[eu-app-green-2]
10.110.12.50 hostname=prd-pri-app-ec1b-app002
10.110.12.52 hostname=prd-pri-app-ec1b-app006
10.110.12.54 hostname=prd-pri-app-ec1b-app010

[eu-app-blue:children]
eu-app-blue-1
eu-app-blue-2

[eu-app-blue-1]
10.110.8.51 hostname=prd-pri-app-ec1a-app003
10.110.8.53 hostname=prd-pri-app-ec1a-app007
10.110.8.55 hostname=prd-pri-app-ec1a-app011

[eu-app-blue-2]
10.110.12.51 hostname=prd-pri-app-ec1b-app004
10.110.12.53 hostname=prd-pri-app-ec1b-app008
10.110.12.55 hostname=prd-pri-app-ec1b-app012
10.110.12.56 hostname=prd-pri-app-ec1b-app014

[eu-memcached]
10.110.10.50 hostname=prd-pri-app-ec1a-mem001
10.110.14.50 hostname=prd-pri-app-ec1b-mem002

[eu-utl]
10.110.11.50 hostname=prd-pri-app-ec1a-utl001

# database servers
[eu-db:children]
eu-dbpri
eu-dbsec
eu-dbrep
eu-dbbcp
eu-cryptodb

[eu-dbpri]
10.110.16.50 hostname=prd-pri-db-ec1a-dbp001

[eu-dbsec]
10.110.20.50 hostname=prd-pri-db-ec1b-dbs001

[eu-dbrep]
10.110.22.50 hostname=prd-pri-db-ec1b-dbr001

[eu-dbbcp]
10.110.22.52 hostname=prd-pri-db-ue1c-dbr003

[eu-cryptodb]
10.110.34.10 hostname=prd-pri-cry-ec1a-dbp001
10.110.38.10 hostname=prd-pri-cry-ec1b-dbp002

# crytpoapp servers
[eu-cryptoapp:children]
eu-cryptoapp-green
eu-cryptoapp-blue

[eu-cryptoapp-green:children]
eu-cryptoapp-green-1
eu-cryptoapp-green-2

[eu-cryptoapp-blue:children]
eu-cryptoapp-blue-1
eu-cryptoapp-blue-2

[eu-cryptoapp-green-1]
10.110.32.10 hostname=prd-pri-cry-ec1a-app01
10.110.32.12 hostname=prd-pri-cry-ec1a-app05
10.110.32.14 hostname=prd-pri-cry-ec1a-app09

[eu-cryptoapp-green-2]
10.110.36.10 hostname=prd-pri-cry-ec1b-app02
10.110.36.12 hostname=prd-pri-cry-ec1b-app06

[eu-cryptoapp-blue-1]
10.110.32.11 hostname=prd-pri-cry-ec1a-app03
10.110.32.13 hostname=prd-pri-cry-ec1a-app07

[eu-cryptoapp-blue-2]
10.110.36.11 hostname=prd-pri-cry-ec1b-app04
10.110.36.13 hostname=prd-pri-cry-ec1b-app08
10.110.36.14 hostname=prd-pri-cry-ec1b-app10

```

And also modify the existing nodes to include roles from new region:

```
# nat servers
[nat:children]
ue-nat
uw-nat
eu-nat

# vpn servers
[vpn:children]
ue-vpn
uw-vpn
eu-vpn

# ...
# do the same for the following nodes:
# [app:children]
# [app-green-1:children]
# [app-green-2:children]
# [app-blue-1:children]
# [app-blue-2:children]
# [memcached:children]
# [utl:children]
# [db:children]
# [db-master:children]
# [db-slave:children]
# [db-repli:children]
# [db-bcp:children]
# [cryptodb:children]
# [cryptoapp:children]
# [cryptoapp-green-1:children]
# [cryptoapp-green-2:children]
# [cryptoapp-blue-1:children]
# [cryptoapp-blue-2:children]
```

And modify the following groups to add new servers:

```
# servers to run liquibase command
[liquibase-server]
10.11.34.10 hostname=prd-pri-cry-ue1b-dbp001
10.20.34.10 hostname=prd-pri-cry-uw1a-dbp001
10.110.34.10 hostname=prd-pri-cry-ec1a-dbp001

# servers to run dataloader
[loader-server]
10.11.32.10 hostname=prd-pri-cry-ue1b-app01
10.20.32.10 hostname=prd-pri-cry-uw1a-app01
10.110.32.10 hostname=prd-pri-cry-ec1a-app01

```

## Setup Cloudant DBs in New Region
Cloudant replication is still managed by cloudant team. To build a new region, we need all tables but `encrypt_user_personal_info` replicated before the new region could run.

**Note**: In our pevious email contact with Cloudant, the `oculus002` site already had replications setup, except for the `encrypt_user_personal_info` DB. We need to send emails to confirm the replication status before using the `oculus002` region. For other regions, follow the steps below.

1. Create DBs using script
  For PROD:
  ```
  cd couchdb
  python ./couchdbcmd.py createdbs prod --prefix= --yes --key=$CRYPTO_KEY
  ```
  For Staging:
  ```
  cd couchdb
  python ./couchdbcmd.py createdbs staging --prefix=stg_ --yes --key=$CRYPTO_KEY
  ```

  **Notes**:
    1. This step will create missing database, update views and assign API Key permissions for all regions. Please **DO** make sure the local git you are using is **at the same commit** of what is live in **staging** or **prod**.
    1. All staging DBs have prefix `stg_`. Prod DBs don't have the prefix. For example, `user` is `stg_user` in staging and `user` in prod. The prefix value is defined in [`prod/cloudant.properties`](https://github.com/junbo/main/blob/master/apphost/config-data/src/main/resources/junbo/conf/prod/cloudant.properties) and [`staging/cloudant.properties`](https://github.com/junbo/main/blob/master/apphost/config-data/src/main/resources/junbo/conf/staging/cloudant.properties).

1. Send an email to [Cloudant Support](mailto:support@cloudant.com) to build multi-home replication for all DBs except `encrypt_user_personal_info`. Whether `encrypt_user_personal_info` needs to be replicated is based on the business decision. If it is necessary, also include that DB in email.

**Note**: For staging, the DB name is 'stg_encrypt_user_personal_info'.

1. Verify the DB setup using curl. Look at the DB doc count for the top DBs.

```
curl "https://$api_key:$api_secret@oculus001.cloudant.com/user/_all_docs?limit=0" -H "X-Cloudant-User: oculus-cni"
curl "https://$api_key:$api_secret@oculus002.cloudant.com/user/_all_docs?limit=0" -H "X-Cloudant-User: oculus-cni"
```
Make sure the `total_rows` from both `oculus001` and `oculus002` are same or similar and both increasing.

Check several big DBs to make sure the majority of the replication is working. If `encrypt_user_personal_info` doesn't need to be replicated, also check to make sure the `total_rows` is zero in the new region.

## Deployment

SSH to the prod deployment server using `devops` account.

### Prepare the Bits
Make sure you got the main branch at the same commit as the running production servers + the configuration changes you made.

Build the main branch using:

```
./build.sh -x build
```

Copy the zip file from `apphost/apphost-cli/build/distributions/` to your deployment folder. For example, `/home/devops`.

Go to the ansible folder of the deployment server and pull the hosts change.

Download the latest newrelic jar and configure the license file. Put the files in `/home/devops/newrelic`.

Prepare the JKS file used to encrypt crypto key. They are found in `/etc/silkcloud/encryptKeyStore.jks` of existing crypto-app servers. Copy the file to `/home/devops/encryptKeyStore.jks`

### Test Connection to New Servers

```
ansible -i prod.hosts eu -m ping --sudo
```

Make sure devops can connect to all new servers successfully.

### Deploy The Servers

For more information about the playbook, refer to [Silkcloud Playbook](https://github.com/junbo/commops/tree/master/ansible).

1. Init servers
```
ansible-playbook -i prod.hosts initserver.yml -e "jkskeypath=/home/devops/encryptKeyStore.jks" --limit eu
```

1. Install fbservices
```
ansible-playbook -i prod.hosts fbservices.yml --limit eu
```

1. Install logstash servers
```
ansible-playbook -i prod.hosts logstash.yml --limit eu
```

1. Reset Config Files
  Prepare the CRYPTO_KEY and OAUTH_CRYPTO_KEY for the environment. They are found in `/etc/silkcloud/configuration.properties` in existing servers.

  ```
  ansible-playbook -i prod.hosts reset-config.yml -e "CRYPTO_KEY=... OAUTH_CRYPTO_KEY=..." --limit eu
  ```

  If there is any effective property overrides in production, you need additional commands to write these overrides to new servers. The best practice is commit these overrides to the source repository immediately.

1. Setup DBs in new region

  ```
  ansible-playbook -i prod.hosts setup-db.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT CRYPTO_KEY=..." --limit eu
  ```

1. Copy masterkey from existing regions

  ```
  export CRYPTO_SERVER_1=10.11.34.10
  export CRYPTO_SERVER_2=10.110.34.10
  export CRYPTO_SERVER_3=10.110.38.10

  ROWCOUNT1=`ssh $CRYPTO_SERVER_1 'psql -d crypto -c "select count(*) from master_key;"' | egrep '^[[:blank:]]*[[:digit:]]+[[:blank:]]*$' | sed 's/[ \t]*//'`
  ROWCOUNT2=`ssh $CRYPTO_SERVER_2 'psql -d crypto -c "select count(*) from master_key;"' | egrep '^[[:blank:]]*[[:digit:]]+[[:blank:]]*$' | sed 's/[ \t]*//'`
  ROWCOUNT3=`ssh $CRYPTO_SERVER_3 'psql -d crypto -c "select count(*) from master_key;"' | egrep '^[[:blank:]]*[[:digit:]]+[[:blank:]]*$' | sed 's/[ \t]*//'`

  echo Found $ROWCOUNT1 master keys in $CRYPTO_SERVER_1
  echo Found $ROWCOUNT2 master keys in $CRYPTO_SERVER_2
  echo Found $ROWCOUNT3 master keys in $CRYPTO_SERVER_3

  if [ "$ROWCOUNT2" -gt "$ROWCOUNT1" ]; then
  echo $CRYPTO_SERVER_2 has more rows, aborting...
  exit 1
  fi

  if [ "$ROWCOUNT3" -gt "$ROWCOUNT1" ]; then
  echo $CRYPTO_SERVER_3 has more rows, aborting...
  exit 1
  fi

  mkdir -p ~/crypto-backup
  ssh $CRYPTO_SERVER_1 pg_dump crypto | gzip > ~/crypto-backup/backup1.sql.gz
  ssh $CRYPTO_SERVER_2 pg_dump crypto | gzip > ~/crypto-backup/backup2.sql.gz
  ssh $CRYPTO_SERVER_3 pg_dump crypto | gzip > ~/crypto-backup/backup3.sql.gz

  ssh $CRYPTO_SERVER_2 psql -d postgres << EOF
  UPDATE pg_database SET datallowconn = 'false' WHERE datname = 'crypto';
  SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'crypto';
  DROP DATABASE crypto;
  EOF
  ssh $CRYPTO_SERVER_2 createdb crypto
  ssh $CRYPTO_SERVER_1 pg_dump crypto | ssh $CRYPTO_SERVER_2 psql -d crypto

  ssh $CRYPTO_SERVER_3 psql -d postgres << EOF
  UPDATE pg_database SET datallowconn = 'false' WHERE datname = 'crypto';
  SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'crypto';
  DROP DATABASE crypto;
  EOF
  ssh $CRYPTO_SERVER_3 createdb crypto
  ssh $CRYPTO_SERVER_1 pg_dump crypto | ssh $CRYPTO_SERVER_3 psql -d crypto

  diff <(ssh $CRYPTO_SERVER_1 pg_dump crypto ) <(ssh $CRYPTO_SERVER_2 pg_dump crypto )
  diff <(ssh $CRYPTO_SERVER_1 pg_dump crypto ) <(ssh $CRYPTO_SERVER_3 pg_dump crypto )

  ```

  If all the commands ran successfully, delete the backup files.

  ```
  rm -rf ~/crypto-backup
  ```

1. Setup Utility in new region
```
ansible-playbook -i prod.hosts upgrade-app.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT newrelicdir=/home/devops/newrelic" --limit eu-utl
```

1. Setup Apps in new region

  ```
  ansible-playbook -i prod.hosts upgrade-app.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT newrelicdir=/home/devops/newrelic" --limit eu-app:eu-cryptoapp
  ```

1. Test new Apps in new region
Run some tests to verify new region is working properly.

1. Update existing app servers to apply the configuration changes. This is to make them aware that they can route the requests with new region to the new region ELB. Follow the regular upgrade guide for this.

## Cleanup

Clear the command history and `/home/devops/*.jks`, then quit the ssh session.
