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

### Copy Bits
  After full build, copy the following files to Bastion server:
  ```
  scp apphost/apphost-identity/build/distributions/apphost-identity-0.0.1-SNAPSHOT.zip $YOUR_USER@bsn-ue1.online.silkcloud.com:/home/$YOUR_USER
  scp apphost/apphost-crypto/build/distributions/apphost-crypto-0.0.1-SNAPSHOT.zip $YOUR_USER@bsn-ue1.online.silkcloud.com:/home/$YOUR_USER
  scp apphost/apphost-dataloader/build/distributions/apphost-dataloader-0.0.1-SNAPSHOT.zip $YOUR_USER@bsn-ue1.online.silkcloud.com:/home/$YOUR_USER
  ```
  The first package apphost-identity contains the binary to run on app server and the files used to setup databases.
  The second package apphost-crypto contains the binary to run on crypto app server.


## Database Setup

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
  1. Setup SSH Key
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

  Switch back to devops account:
  ```
  exit
  ```

  1. Create silkcloud folders
  Prepare the crypto.core.key used to encrypt passwords in the configuration files. The following script assumes $CRYPTO_KEY is the key.
  Run the following command using devops account:
  ```
  grep "$HOSTNAME" /etc/hosts || sudo bash -c 'echo 127.0.0.1 $HOSTNAME >> /etc/hosts'
  sudo mkdir /var/silkcloud
  sudo chown -R silkcloud:silkcloud /var/silkcloud
  sudo mkdir /etc/silkcloud
  sudo bash -c "echo environment=ppe > /etc/silkcloud/configuration.properties"
  sudo bash -c "echo crypto.core.key=$CRYPTO_KEY >> /etc/silkcloud/configuration.properties"
  sudo chmod 600 /etc/silkcloud/configuration.properties
  sudo chown -R silkcloud:silkcloud /etc/silkcloud
  ```

### Create DB and Setup High Availability

  1. Extract the DB setup binary on the db primary server from bastion
  ```
  scp $YOUR_USER@bsn-ue1.online.silkcloud.com:/home/$YOUR_USER/apphost-identity-0.0.1-SNAPSHOT.zip /home/silkcloud
  ```

  1. Upload all PGHA scripts to master/slave/replica servers.
  Run the following commands using silkcloud on bastion server to copy the bits to the first crypto db server
  ```
  scp apphost-identity-0.0.1-SNAPSHOT.zip silkcloud@10.24.34.10:/home/silkcloud
  ```

  Then ssh to the first crypto db server and run the following command:
  ```
  unzip apphost-identity-0.0.1-SNAPSHOT.zip
  cd apphost-identity-0.0.1-SNAPSHOT/dbsetup/pgha
  ./upload_script.sh ppe
  ```

  1. Go to the master and slave servers using silkcloud account. For PPE, the servers are:
    * `10.24.16.50`
    * `10.24.34.10`
    * `10.24.38.10`
    * `10.24.20.50`

  Run the following commands to setup pgbouncer access password. The password should match `*.db.password` configured in configuration-data.jar

  For example, if the password plain text is `abc123` in ppe:
  ```
  echo '"silkcloud" "abc123"' > ~/.pgbouncer_auth
  chmod 600 ~/.pgbouncer_auth
  ```

  1. Go to the master and crypto servers using silkcloud account. For PPE, the servers are:
    * `10.24.16.50`
    * `10.24.34.10`
    * `10.24.38.10`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./setup/setup_master.sh
  ./test/test_liquibase_master.sh
  ./pgbouncer/pgbouncer_master.sh
  nc -zv localhost 113 5432 6543
  ```

  The nc command is used to verify the following ports are open:
    * `5432`: postgresql database port
    * `6543`: pgbouncer proxy port
    * `113`: oident server port

  1. Run liquibase on the first crypto db server
  Prepare the crypto.core.key used to encrypt passwords in the configuration files. The following script assumes $CRYPTO_KEY is the key.
  For PPE, the server is `10.24.34.10`

  First, install default jdk in order to run liquibase
  ```
  sudo apt-get install default-jdk
  ```

  Then run the following command:
  ```
  cd apphost-identity-0.0.1-SNAPSHOT/dbsetup/liquibase
  ./createdb.sh -env:ppe -key:$CRYPTO_KEY
  ./updatedb.sh -env:ppe -key:$CRYPTO_KEY
  ```
  When prompted, input the password cipher key

  1. Setup streaming replication on primary servers. For PPE, the servers are:
    * `10.24.16.50`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./util/base_backup.sh
  ```

  1. Go to the slave servers using silkcloud account. For PPE, the slave servers are:
    * `10.24.20.50`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./setup/setup_slave.sh
  ./pgbouncer/pgbouncer_master.sh
  nc -zv localhost 113 5432 6543
  ```

  1. Setup replication using skytool londiste on primary using silkcloud account. For PPE, the master servers are:
    * `10.24.16.50`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./londiste/londiste_config_root.sh
  ./londiste/londiste_root.sh
  ./londiste/londiste_pgqd.sh
  ./util/base_backup.sh
  ```

  1. Setup replication using skytool londiste on secondary using silkcloud account. For PPE, the slave servers are:
    * `10.24.22.50`

  Run the following commands:
  ```
  cd /var/silkcloud/pgha
  ./setup/setup_replica.sh
  ./londiste/londiste_config_leaf.sh
  ./londiste/londiste_leaf.sh
  ./londiste/londiste_pgqd.sh
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
```
cd apphost-identity-0.0.1-SNAPSHOT/dbsetup/cloudant
python ./couchdbcmd.py createdbs ppe --prefix=ppe_ --yes
```

## Setup Application

### Prepare Machine
Prepare the crypto.core.key used to encrypt passwords in the configuration files. The following script assumes $CRYPTO_KEY is the key.
Run the following commands using devops account:
```
grep "$HOSTNAME" /etc/hosts || sudo bash -c 'echo 127.0.0.1 $HOSTNAME >> /etc/hosts'
sudo mkdir /var/silkcloud
sudo chown -R silkcloud:silkcloud /var/silkcloud
sudo mkdir /etc/silkcloud
sudo bash -c "echo environment=ppe > /etc/silkcloud/configuration.properties"
sudo bash -c "echo crypto.core.key=$CRYPTO_KEY >> /etc/silkcloud/configuration.properties"
sudo chmod 600 /etc/silkcloud/configuration.properties
sudo chown -R silkcloud:silkcloud /etc/silkcloud
```

Run the following commands using devops account to install necessary packages:
```
sudo apt-get install unzip
```

### Setup Crypto Servers
  * `10.24.32.10`
  * `10.24.36.10`

1. Run the following commands on bastion servers using silkcloud:
```
scp /home/$YOUR_USER/apphost-crypto-0.0.1-SNAPSHOT.zip 10.24.32.10:/var/silkcloud
scp /home/$YOUR_USER/apphost-crypto-0.0.1-SNAPSHOT.zip 10.24.36.10:/var/silkcloud
```

1. Put the jks file to /etc/silkcloud and chmod 600
```
scp $PATH_TO_JKS 10.24.32.10:/var/silkcloud
scp $PATH_TO_JKS 10.24.36.10:/var/silkcloud
ssh 10.24.32.10 chmod 600 '/etc/silkcloud/*.jks'
ssh 10.24.36.10 chmod 600 '/etc/silkcloud/*.jks'
```

1. Run the following command on crypto servers:
```
cd /var/silkcloud
unzip -o apphost-crypto-0.0.1-SNAPSHOT.zip
cd apphost-crypto-0.0.1-SNAPSHOT
./startup.sh
```

### Setup Rest Servers
  * `10.24.8.50`
  * `10.24.12.50`

Run the following command on bastion servers using silkcloud:
```
scp /home/$YOUR_USER/apphost-identity-0.0.1-SNAPSHOT.zip 10.24.8.50:/var/silkcloud
scp /home/$YOUR_USER/apphost-identity-0.0.1-SNAPSHOT.zip 10.24.12.50:/var/silkcloud
```
Run the following command on crypto servers:
```
cd /var/silkcloud
unzip -o apphost-identity-0.0.1-SNAPSHOT.zip
cd apphost-identity-0.0.1-SNAPSHOT
./startup.sh
```

### Load Initial Data
  * `10.24.32.10`

Run the following command on bastion servers using silkcloud:
```
scp /home/$YOUR_USER/apphost-dataloader-0.0.1-SNAPSHOT.zip 10.24.32.10:/var/silkcloud
```
Run the following command on crypto servers:
```
cd /var/silkcloud
unzip -o apphost-dataloader-0.0.1-SNAPSHOT.zip
cd apphost-dataloader-0.0.1-SNAPSHOT
./dataloader.sh
```

### Sync Crypto Key in SQL
  * 10.24.34.10
  * 10.24.38.10

TODO:

# Appendix
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
