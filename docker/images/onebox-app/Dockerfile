FROM silkcloud/jdk7base:latest

# memcached public variable
ENV MAX_MEM 64
ENV MAX_CONN 1024

RUN mkdir /etc/service/memcached
ADD runmemcached.sh /etc/service/memcached/run
RUN chmod +x /etc/service/memcached/run

# use rinetd to setup port forwarding
ADD rinetd.conf /etc/service/rinetd/rinetd.conf
ADD rinetd.sh /etc/service/rinetd/run
RUN chmod +x /etc/service/rinetd/run

# check environment script, which will be executed during boot
RUN mkdir -p /etc/my_init.d
ADD checkdockerenv.sh /etc/my_init.d/checkdockerenv.sh
RUN chmod +x /etc/my_init.d/checkdockerenv.sh

# init and purge db scripts
ADD init_databases.sh /var/silkcloud/scripts/init_databases.sh
RUN chmod +x /var/silkcloud/scripts/init_databases.sh
ADD purge_databases.sh /var/silkcloud/scripts/purge_databases.sh
RUN chmod +x /var/silkcloud/scripts/purge_databases.sh

# create folder for config and log
RUN mkdir -p /etc/silkcloud
RUN mkdir -p /var/silkcloud/logs

# Expose config, log and data directories.
VOLUME ["/etc/silkcloud", "/var/silkcloud/logs"]

EXPOSE 8079 8080 8081

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]

# apphost service
ADD runapphost.sh /etc/service/apphost/run
RUN chmod +x /etc/service/apphost/run

# Add apphost binaries folder
ADD bin/apphost/ /var/silkcloud/apphost/
