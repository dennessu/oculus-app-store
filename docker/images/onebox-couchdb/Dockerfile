FROM phusion/baseimage:latest
MAINTAINER Tianxiang Chen "txchen@silkcloud.com"

# Set correct environment variables.
ENV HOME /root

RUN locale-gen en_US.UTF-8 && echo 'LANG="en_US.UTF-8"' > /etc/default/locale

# install couchdb
RUN apt-get update &&\
    DEBIAN_FRONTEND=noninteractive apt-get install -y couchdb &&\
    sed -e 's/^bind_address = .*$/bind_address = 0.0.0.0/' -i /etc/couchdb/default.ini &&\
    apt-get clean &&\
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN mkdir /etc/service/couchdb
ADD runcouchdb.sh /etc/service/couchdb/run
RUN chmod +x /etc/service/couchdb/run
ADD finish.sh /etc/service/couchdb/finish
RUN chmod +x /etc/service/couchdb/finish

# Expose data and log folder as volume, to get better performance.
VOLUME ["/var/log/couchdb", "/var/lib/couchdb"]

EXPOSE 5984

# Use phusion/baseimage-docker's init system.
CMD ["/sbin/my_init"]
