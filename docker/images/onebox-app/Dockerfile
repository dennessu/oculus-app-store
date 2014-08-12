FROM silkcloud/jdk7base:latest

ADD rinetd.conf /etc/service/rinetd/rinetd.conf
ADD rinetd.sh /etc/service/rinetd/run
RUN chmod +x /etc/service/rinetd/run

RUN mkdir -p /etc/my_init.d
ADD checkdockerenv.sh /etc/my_init.d/checkdockerenv.sh
RUN chmod +x /etc/my_init.d/checkdockerenv.sh

ADD init_databases.sh /var/silkcloud/scripts/init_databases.sh
RUN chmod +x /var/silkcloud/scripts/init_databases.sh

# create folder for config and log
RUN mkdir -p /etc/silkcloud
RUN mkdir -p /var/silkcloud/logs

# Expose config, log and data directories.
VOLUME ["/etc/silkcloud", "/var/silkcloud/logs"]

# Enable insecure key so that docker-ssh can work, it is ok in dev env
RUN /usr/sbin/enable_insecure_key

EXPOSE 8080 8081

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]

# Add apphost folder
ADD bin/apphost/ /var/silkcloud/apphost/
