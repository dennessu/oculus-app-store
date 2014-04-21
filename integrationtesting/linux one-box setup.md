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

7. aws-artifactory setup
   1). echo 54.254.249.206 aws-artifactory >> /etc/hosts
   2). download source code by git clone
   3). set up key:
        cd ~/owp-main/bootstrap/setup
        keytool -import -alias aws-artifactory -keystore $JAVA_HOME/jre/lib/security/cacerts -file ./aws-artifactory.cer -trustcacerts

8. Install python
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

9. full build and start bundle(TBF)