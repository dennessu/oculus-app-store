# Deployment Guide

## Preparation

### Prepare linux accounts
  * `devops`: used to make system changes, in sudo list
  * `silkcloud`: used to deploy applicaiton and database, not included in sudo list

### Machine List for PPE

| region    | role       | ip               | comment                |
|:----------|:-----------|:-----------------|:-----------------------|
| us-west-1 | app        | 10.24.8.50       | app server 1           |
| us-west-1 | app        | 10.24.12.50      | app server 2           |
| us-west-1 | db         | 10.24.16.50      | db primary             |
| us-west-1 | db         | 10.24.20.50      | db secondary           |
| us-west-1 | db         | 10.24.22.50      | db replication         |
| us-west-1 | crypto-app | 10.24.32.10      | crypto app server 1    |
| us-west-1 | crypto-app | 10.24.36.10      | crypto app server 2    |
| us-west-1 | crypto-db  | 10.24.34.10      | crypto db server 1     |
| us-west-1 | crypto-db  | 10.24.38.10      | crypto db server 2     |

External DNS names:
  * Crypto: https://internal-crypto-ppe-1961399710.us-west-1.elb.amazonaws.com
  * App: https://internal-api-jvm-ppe-464421051.us-west-1.elb.amazonaws.com

Both endpoints are internal. They can be tested from Bastion.

### Prepare Database Servers
Run the following steps on all db servers including crypto-db. For PPE, the db servers are:
  * `10.24.16.50`
  * `10.24.20.50`
  * `10.24.22.50`
  * `10.24.34.10`
  * `10.24.38.10`

1. Use devops to setup database server
```
sudo su - devops
ssh $SERVER_IP
```
The postgresql 9.3 is already installed on the server when preparing the
server. If it's not setup, refer to Appendix to install.
Check whether postgresql exists by typing psql.

1. Update the apt-get library
```
sudo apt-get update
```
1. Create command links
```
sudo ln -sf /usr/lib/postgresql/9.3/bin/psql /usr/bin/psql
sudo ln -sf /usr/lib/postgresql/9.3/bin/pg_ctl /usr/bin/pg_ctl
sudo ln -sf /usr/lib/postgresql/9.3/bin/createdb /usr/bin/createdb
sudo ln -sf /usr/lib/postgresql/9.3/bin/dropdb /usr/bin/dropdb
```
1. Cleanup existing DB instance
```
sudo rm -rf /etc/init.d/postgresql
sudo kill $(sudo fuser -n tcp 5432 2>/dev/null)
sudo rm -rf /var/run/postgresql
sudo mkdir /var/run/postgresql
sudo chown silkcloud:silkcloud /var/run/postgresql
```
1. Install PGBouncer
```
sudo apt-get install -y pgbouncer
```
1. Install oidentd
```
sudo apt-get install -y oidentd
/etc/init.d/oidentd start
```
1. Install Skytool Loniste
```
sudo apt-get install -y make gcc python-all python-dev python-psycopg2 libpq-dev postgresql-server-dev-9.3
pushd /tmp
wget http://pgfoundry.org/frs/download.php/3622/skytools-3.2.tar.gz
tar zxfv skytools-3.2.tar.gz
cd skytools-3.2
./configure --prefix=/usr/local
make
sudo make install
popd
```

1. Setup SSH Key <br/>
Run the following command to switch to silkcloud account and check ssh key:
```
sudo su - silkcloud
ls ~/.ssh
```
If the files are not found, run the following commands to generate sshkey:
```
# (don't create passphrase!)
ssh-keygen -t rsa
chmod 600 ~/.ssh/id_rsa*
```
Then run the following commands for all database servers:
```
ssh-copy-id silkcloud@$SERVER_IP
ssh silkcloud@$SERVER_IP
```
Make sure the ssh can pass without using password.

1. Create silkcloud folders <br/>
Run the following command using devops account:
```
grep "$HOSTNAME" /etc/hosts || sudo bash -c 'echo 127.0.0.1 $HOSTNAME >> /etc/hosts'
sudo mkdir /var/silkcloud
sudo chown -R silkcloud:silkcloud /var/silkcloud
sudo mkdir /etc/silkcloud
sudo chown -R silkcloud:silkcloud /etc/silkcloud
sudo chmod 700 /etc/silkcloud
```


1. Install other packages
```
sudo apt-get install unzip ntp
sudo apt-get install default-jdk
```
Java is used to run some tools on DB servers.
NTP is used to sync time on servers. Network admin needs to make sure access to the NTP servers on 123 port for UDP is open.

### Prepare App Servers
  * `10.24.8.50`
  * `10.24.12.50`
  * `10.24.32.10`
  * `10.24.36.10`

1. Install Java 1.7 <br/>
Install the latest Oracle JVM 1.7 on all machines.

1. Trust CA cert <br/>
Run the following scripts to install CA cert for internal HTTPS calls.
```
sudo keytool -import -alias crypto-key -keystore `readlink -f /usr/bin/java | sed "s:/bin/java::"`/lib/security/cacerts -trustcacerts -file $CA_CERT_PATH
```
The default password for cacerts is `changeit`

1. Create silklcloud folders <br/>
Run the following command using devops account:
```
grep "$HOSTNAME" /etc/hosts || sudo bash -c 'echo 127.0.0.1 $HOSTNAME >> /etc/hosts'
sudo mkdir /var/silkcloud
sudo chown -R silkcloud:silkcloud /var/silkcloud
sudo mkdir /etc/silkcloud
sudo chown -R silkcloud:silkcloud /etc/silkcloud
sudo chmod 700 /etc/silkcloud
```

1. Configure upstart script <br />
  Run the following commands to create startup script.
  ```
  sudo bash -c 'cat << EOF > /etc/init/silkcloud-apphost.conf
  description "silkcloud app host"

  start on runlevel [2345]
  stop on runlevel [^2345]

  console none
  chdir /var/silkcloud/apphost
  setuid silkcloud
  setgid silkcloud

  respawn
  respawn limit 10 600

  kill timeout 30

  exec ./startup.sh

  EOF
  '
  ```

  Allow silkcloud to start/stop the service by:
  ```
  sudo bash -c 'echo "silkcloud ALL = NOPASSWD: /sbin/start silkcloud-apphost, /sbin/stop silkcloud-apphost, /sbin/initctl status silkcloud-apphost" >> /etc/sudoers'
  ```

  Run the following commands on app servers to create startup script for docs.
  ```
sudo bash -c 'cat << EOF > /etc/init/silkcloud-docs.conf
description "silkcloud docs"

start on runlevel [2345]
stop on runlevel [^2345]

console none
chdir /var/silkcloud/docs-bundle
setuid silkcloud
setgid silkcloud

respawn
respawn limit 10 600

kill timeout 30

exec ./startup.sh

EOF
'
sudo bash -c 'echo "silkcloud ALL = NOPASSWD: /sbin/start silkcloud-docs, /sbin/stop silkcloud-docs, /sbin/initctl status silkcloud-docs" >> /etc/sudoers'
  ```

1. Install other packages
```
sudo apt-get install unzip ntp
```
NTP is used to sync time on servers. Network admin needs to make sure access to the NTP servers on 123 port for UDP is open.

## Deployment

### Preparation
1. Copy Bits
After full build, copy the following files to Bastion server:
```
scp apphost/apphost-cli/build/distributions/apphost-cli-0.0.1-SNAPSHOT.zip $YOUR_USER@bsn-ue1.online.silkcloud.com:/home/$YOUR_USER
```
The package apphost-cli contains the binary to run on app server and the files used to setup databases.


1. Setup the silkcloud configuration folder for all servers
  * `10.24.8.50`
  * `10.24.12.50`
  * `10.24.32.10`
  * `10.24.36.10`
  * `10.24.16.50`
  * `10.24.20.50`
  * `10.24.22.50`
  * `10.24.34.10`
  * `10.24.38.10`

Prepare the crypto.core.key used to encrypt passwords in the configuration files. The following script assumes $CRYPTO_KEY is the key and $OAUTH_CRYPTO_KEY is the key for encrypting OAuth client secret.
```
cat << EOF > /etc/silkcloud/configuration.properties
environment=ppe
crypto.core.key=$CRYPTO_KEY
oauth.crypto.key=$OAUTH_CRYPTO_KEY
common.conf.debugMode=false
EOF
chmod 600 /etc/silkcloud/configuration.properties
```

1. Setup profile to app servers
  * `10.24.8.50`
  * `10.24.12.50`
Run the following commands:
```
echo profile=apphost-rest >> /etc/silkcloud/configuration.properties
```

1. Setup profile to crypto app servers
* `10.24.32.10`
* `10.24.36.10`
Run the following commands:
```
echo profile=apphost-crypto >> /etc/silkcloud/configuration.properties
```

1. Setup jks key store on crypto servers
  * `10.24.32.10`
  * `10.24.36.10`
Prepare the jks file generated in New Environment step
```
scp $PATH_TO_JKS /etc/silkcloud
chmod 600 /etc/silkcloud/*.jks
```

### Create DB and Setup High Availability

  1. Upload all PGHA scripts to master/slave/replica servers
  Copy the DB setup binary to the db primary server from bastion.
  ```
  scp /home/$YOUR_USER/apphost-cli-0.0.1-SNAPSHOT.zip silkcloud@10.24.34.10:/home/silkcloud
  ```
  Then ssh to the db primary server
  ```
  rm -rf apphost-cli-0.0.1-SNAPSHOT
  unzip -o apphost-cli-0.0.1-SNAPSHOT.zip
  cd apphost-cli-0.0.1-SNAPSHOT/dbsetup/pgha
  ./upload_script.sh ppe
  ```

  1. Go to all db servers using silkcloud account. For PPE, the servers are:
    * `10.24.16.50`
    * `10.24.20.50`
    * `10.24.22.50`
    * `10.24.34.10`
    * `10.24.38.10`

  Run the following commands to setup pgbouncer access password. The password should match `*.db.password` configured in config-data.jar

  For example, if the password plain text is `abc123` in ppe:
  ```
  echo '"silkcloud" "md5$DATABASE_PASSWORD_HASH"' > ~/.pgbouncer_auth
  chmod 600 ~/.pgbouncer_auth
  sudo crontab - <<EOF
  @reboot /var/silkcloud/pgha/util/superremedy.sh
  EOF
  ```

  1. Go to the master and crypto servers using silkcloud account. For PPE, the servers are:
    * `10.24.16.50`
    * `10.24.34.10`
    * `10.24.38.10`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./setup/setup_master.sh
  nc -zv localhost 113 5432 6543
  ```

  The nc command is used to verify the following ports are open:
    * `5432`: postgresql database port
    * `6543`: pgbouncer proxy port
    * `113`: oident server port

  1. Run liquibase on the first crypto db server
  Prepare the crypto.core.key used to encrypt passwords in the configuration files. The following script assumes $CRYPTO_KEY is the key.
  For PPE, the server is `10.24.34.10`

  Then run the following command:
  ```
  cd apphost-cli-0.0.1-SNAPSHOT/dbsetup/liquibase
  ./createdb.sh -env:ppe -key:$CRYPTO_KEY
  ./updatedb.sh -env:ppe -key:$CRYPTO_KEY
  ```
  When prompted, input the password cipher key

  1. Initial backup and londiste setup on primary using silkcloud account. For PPE, the master servers are:
    * `10.24.16.50`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./londiste/londiste_root.sh
  ./util/base_backup.sh
  ```

  1. Go to the slave servers using silkcloud account. For PPE, the slave servers are:
    * `10.24.20.50`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./setup/setup_slave.sh
  nc -zv localhost 113 5432 6543
  ```

  1. Setup replication using skytool londiste on primary using silkcloud account. For PPE, the master servers are:
    * `10.24.16.50`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./londiste/londiste_root.sh
  ./util/base_backup.sh
  ```

  1. Setup replication using skytool londiste on secondary using silkcloud account. For PPE, the slave servers are:
    * `10.24.22.50`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./setup/setup_replica.sh
  nc -zv localhost 113 5432
  ```

  1. Test the HA setup. For PPE, go to the primary to test:
    * `10.24.16.50`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./test/test_master2slave.sh
  ```

### Create Cloudant Databases

Ssh to the first crypto db server and run the following command:
  * `10.24.34.10`
```
cd apphost-cli-0.0.1-SNAPSHOT/dbsetup/couchdb
python ./couchdbcmd.py createdbs ppe --prefix=ppe_ --yes
```

## Setup Application

### Setup Crypto Servers
  * `10.24.32.10`
  * `10.24.36.10`

1. Run the following commands on bastion servers using silkcloud:
```
scp /home/$YOUR_USER/apphost-cli-0.0.1-SNAPSHOT.zip 10.24.32.10:/var/silkcloud
scp /home/$YOUR_USER/apphost-cli-0.0.1-SNAPSHOT.zip 10.24.36.10:/var/silkcloud
```

1. Put the jks file to /etc/silkcloud and chmod 600
```
scp $PATH_TO_JKS 10.24.32.10:/etc/silkcloud
scp $PATH_TO_JKS 10.24.36.10:/etc/silkcloud
ssh 10.24.32.10 chmod 600 '/etc/silkcloud/*.jks'
ssh 10.24.36.10 chmod 600 '/etc/silkcloud/*.jks'
```

1. Run the following command on crypto servers:
```
cd /var/silkcloud
(sudo initctl status node-App | grep start) && sudo stop silkcloud-apphost
rm -rf apphost-cli-0.0.1-SNAPSHOT
unzip -o apphost-cli-0.0.1-SNAPSHOT.zip
ln -sf apphost-cli-0.0.1-SNAPSHOT apphost
sudo start silkcloud-apphost
```

### Setup Rest Servers
  * `10.24.8.50`
  * `10.24.12.50`

Run the following command on bastion servers using silkcloud:
```
scp /home/$YOUR_USER/apphost-cli-0.0.1-SNAPSHOT.zip 10.24.8.50:/var/silkcloud
scp /home/$YOUR_USER/apphost-cli-0.0.1-SNAPSHOT.zip 10.24.12.50:/var/silkcloud
```
Run the following command on crypto servers:
```
cd /var/silkcloud
(sudo initctl status node-App | grep start) && sudo stop silkcloud-apphost
rm -rf apphost-cli-0.0.1-SNAPSHOT
unzip -o apphost-cli-0.0.1-SNAPSHOT.zip
ln -sf apphost-cli-0.0.1-SNAPSHOT apphost
sudo start silkcloud-apphost
```

### Load Initial Data
  * `10.24.32.10`

Run the following command on crypto servers:
```
cd /var/silkcloud/apphost
./dataloader.sh
```

### Sync Master Keys in SQL
  * 10.24.34.10
  * 10.24.38.10

Run the following command to check master keys:
```
CRYPTO_SERVER_1=10.24.34.10
CRYPTO_SERVER_2=10.24.38.10

ROWCOUNT1=`ssh $CRYPTO_SERVER_1 'psql -d crypto -c "select count(*) from master_key;"' | egrep '^[[:blank:]]*[[:digit:]]+[[:blank:]]*$' | sed 's/[ \t]*//'`
ROWCOUNT2=`ssh $CRYPTO_SERVER_2 'psql -d crypto -c "select count(*) from master_key;"' | egrep '^[[:blank:]]*[[:digit:]]+[[:blank:]]*$' | sed 's/[ \t]*//'`

echo Found $ROWCOUNT1 master keys in $CRYPTO_SERVER_1
echo Found $ROWCOUNT2 master keys in $CRYPTO_SERVER_2

if [ "$ROWCOUNT2" -gt "$ROWCOUNT1" ]; then
    echo ERROR: $CRYPTO_SERVER_2 has more rows, aborting...
fi
```
If it doesn't print the error, continue with the following statements to sync keys:
```
ssh $CRYPTO_SERVER_1 pg_dump crypto | gzip > backup1.sql.gz
ssh $CRYPTO_SERVER_2 pg_dump crypto | gzip > backup2.sql.gz
ssh $CRYPTO_SERVER_2 psql -d postgres << EOF
UPDATE pg_database SET datallowconn = 'false' WHERE datname = 'crypto';
SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'crypto';
DROP DATABASE crypto;
EOF
ssh $CRYPTO_SERVER_2 createdb crypto
ssh $CRYPTO_SERVER_1 pg_dump crypto | ssh $CRYPTO_SERVER_2 psql -d crypto
```
Now compare two servers:
```
diff <(ssh $CRYPTO_SERVER_1 pg_dump crypto ) <(ssh $CRYPTO_SERVER_2 pg_dump crypto )
```
If the diff didn't show any output, the sync is completed successfully. Backup the `backup1.sql.gz` and put it in a secure place to recover master key when needed. Then remove the file from the server:
```
rm backup1.sql.gz
rm backup2.sql.gz
```

# Appendix
## Empty Bash History
```
history -w
history -c
```

## Install PostgreSQL
Run the following commands
```
sudo apt-get update
sudo apt-get install -y postgresql-9.3
```

## Purge the Postgre SQL server
When the deployment failed and it's okay to purge all data on Postgre SQL server, the following steps
can be used to purge the server.

1. Go to the master servers using silkcloud account. For PPE, the master servers are:
  * `10.24.16.50`
  * `10.24.34.10`
  * `10.24.38.10`

  Run the following commands:
  ```
  /var/silkcloud/pgha/purge/purge_master.sh
  ```

1. Go to the slave servers using silkcloud account. For PPE, the slave servers are:
  * `10.24.20.50`

  Run the following commands:
  ```
  /var/silkcloud/pgha/purge/purge_slave.sh
  ```

1. Go to the replica servers using silkcloud account. For PPE, the replica servers are:
  * `10.24.22.50`

  Run the following commands:
  ```
  /var/silkcloud/pgha/purge/purge_replica.sh
  ```

## Run Unit Tests on PPE
```
cd scripts/utils/ppe
./ut_oauth.py -uri 'https://internal-api-jvm-ppe-464421051.us-west-1.elb.amazonaws.com'
```

## Overriding Properties
Add the properties you want to override in `/etc/silkcloud/configuration.properties` and restart the service.

## Overriding the Trace on the fly
Use the loggers endpoint in 8081 port to tune the trace levels on the fly.

`GET localhost:8081/loggers`
This will show all loggers with trace level set.

`GET localhost:8081/logger?showall=true`
This will show all available loggers.

`GET localhost:8081/loggers/override?name={name}&level={level}`

This can be used to override the log levels. Available levels are:
  * OFF
  * ERROR
  * WARN
  * INFO
  * DEBUG
  * TRACE
  * ALL
To change the default for all loggers, you can override ROOT logger.

`GET localhost:8081/loggers/reset`
Reset the logger levels to default (default to the log back configuration file).

Here is an example, this will turn on the logger for AsyncHttpClient.
`http://localhost:8081/loggers/override?name=com.junbo.langur.core.async.JunboAsyncHttpClient&level=TRACE`

## Overriding the Trace using config file
Put a override logback config file in `/etc/silkcloud/logback-override.xml`, and modify the startup script to add the following:
`-Dlogback.configurationFile=/etc/silkcloud/logback-override.xml`
