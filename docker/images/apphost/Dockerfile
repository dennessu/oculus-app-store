FROM silkcloud/jdk7base:latest

# create folder for config and log
RUN mkdir -p /etc/silkcloud
RUN mkdir -p /var/silkcloud/logs

# Add apphost folder
ADD bin/apphost/ /var/silkcloud/apphost/

# add daemon

# Expose config, log and data directories.
VOLUME ["/etc/silkcloud", "/var/silkcloud/logs"]

# Enable insecure key so that docker-ssh can work, it is ok in dev env
RUN /usr/sbin/enable_insecure_key

EXPOSE 8080 8081

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]
