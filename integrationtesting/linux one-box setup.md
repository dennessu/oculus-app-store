1. Install JDK
   1) download jdk package(repalce 1.8.0_05 below accordingly if jdk version downloaded is different)
   2) tar zxvf jdk1.8.0_05-linux-x64.gz
   3) mkdir /opt/java
   4) mv  jdk1.8.0_05 /opt/java
   5) alternatives --install "/usr/bin/java" "jave" "/opt/java/jdk1.8.0_05/bin/java" 1
   6) export JAVA_HOME=/opt/java/jdk1.8.0_05/
   7) use "jave" and "ls $JAVA_HOME" to check JDK and java path set correctly.

2. Install gradle
	1) download gradle files(gradle-1.11)
	2) set path and GRADLE_HOME
       a) GRADLE_HOME=/home/zj/Desktop/gradle-1.8
       b) export PATH=$PATH:$GRADLE_HOME/bin

3. Install git
    1).Yum install git

4. Install postgresql
    1). yum install http://yum.postgresql.org/9.3/redhat/rhel-6-x86_64/pgdg-redhat93-9.3-1.noarch.rpm
    2). yum install postgresql93-server postgresql93-contrib
    3). service postgresql-9.3 initdb
    4). chkconfig postgresql-9.3 on
    5). /etc/init.d/postgresql-9.3   start
    6). netstat  -an  |  grep   543
    7). change config: /var/lib/pgsql/9.3/data/postgresql.conf
        a).Find max_prepared_transactions, uncomment the line and change the value to 100.
            max_prepared_transactions = 100
        b). Find max_connections, uncomment if commented and change the value to 100.
            max_connections = 100
    8). change config: /var/lib/pgsql/9.3/data/pg_hba.conf
        # "local" is for Unix domain socket connections only
        local   all             all                                     trust
        # IPv4 local connections:
        host    all             all             127.0.0.1/32            trust
        # IPv6 local connections:
        host    all             all             ::1/128                 trust
    9). change postgres's password:
        a). su - postgres
        b). psql
        c). ALTER USER Postgres WITH PASSWORD '#Bugsfor$';
    10). Restart Posgresql to reload the new config

5. install couchdb:
   1). install couchdb following http://www.tecmint.com/install-apache-couchdb-on-rhel-centos-6-5/
   2). service iptables stop
   3). chkconfig iptables off

6. Generating SSH Keys got github access:
    https://help.github.com/articles/generating-ssh-keys

7. Install python
  1). Yum install gcc
  2). wget http://python.org/ftp/python/2.7.3/Python-2.7.3.tar.bz2
  3). tar -jxvf Python-2.7.3.tar.bz2
  4). cd Python-2.7.3
  5). ./configure
  6). make all
  7). make install
  8)  make clean
  9). make distclean
  10). ln -s /usr/local/bin/python2.7 /usr/bin/python

8. db setup:
   1). git clone source code to /home/sourcecode/main
   2). run git pull to pull latest code
   3). run ./setupdb.sh to drop old database and create new database in localhost.
   note: all the old data will be removed by this script.

9. install ActiveMQ
   1). Download activeMQ from http://activemq.apache.org/activemq-591-release.html.   
   2). tar -zxvf apache-activemq-5.9.1-bin.tar.gz
   3). start activemq: ./apache-activemq-5.9.1-bin/activemq start
   4). check status: http://<ip>:8161/ (usr&pwd: admin/admin)

10. install encrypt cert

    Check $JAVA_HOME and $JAVA_HOME/lib/security/encryptKeyStore.jks exists, if not, please do the following steps:

    1): run command: update-alternatives --display java

        It will display it as: Current "BEST" version is /usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin/java

        Your java home should be in "/usr/lib/jvm/jre-1.7.0-openjdk.x86_64"

    2): Set up JAVA_HOME = /usr/lib/jvm/jre-1.7.0-openjdk.x86_64
        Open up the profile in your terminal using vi:
            sudo vi /etc/profile
                Set the JAVA_HOME environment variable using the following syntax:
            export JAVA_HOME=/usr/lib/jvm/jdk1.6.0_32
                Don’t forget to set the PATH variable too:
            export PATH=$PATH:/usr/lib/jvm/jdk1.6.0_32/bin
                You can now logout and login back to the session for the settings you’ve made to take effect immediately, or just type the following for an immediate effect:
            source /etc/profile

            or:
            . /etc/profile

    3): import cert:

        openssl genrsa -out ca.key 2048

        openssl req -new -key ca.key -out ca.csr

        // generate self signed key with 10 years valid time
        openssl x509 -req -days 3650 -in ca.csr -signkey ca.key -out ca.crt

        openssl pkcs12 -export -name test -in ca.crt -inkey ca.key -out keystore.p12

        keytool -importkeystore -destkeystore $JAVA_HOME/lib/security/encryptKeyStore.jks -srckeystore keystore.p12 -srcstoretype pkcs12 -alias test

        Please remember the keyStore password in 1-box should be: changeit

        The test cert's password should be: 123456

11. copy bundles and start service
   1). in source branch /main/apphost
   2). gradle installApp
   3). /apphost/apphost-cli/build/install/apphost-cli to onebox
   4). killd old one and run ./startup.sh to start identity/catalog/commerce on 8080

12. startup docs bundle
   1). go to main/bootstrap/docs-bundle
   2). gradle installApp
   3). copy bootstrap/docs-bundle/build/install/docs-bundle to onebox
   4). kill old one and run ./startup.sh to start docs on 8079
   5). use http://oneboxip:8079/ to check docs
   
13. populate catalog data(TBD)
