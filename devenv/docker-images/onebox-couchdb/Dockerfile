FROM phusion/baseimage
MAINTAINER Tianxiang Chen "txchen@silkcloud.com"

# Set correct environment variables.
ENV HOME /root

RUN locale-gen en_US.UTF-8
RUN apt-get update
RUN apt-get install -y couchdb
RUN sed -e 's/^bind_address = .*$/bind_address = 0.0.0.0/' -i /etc/couchdb/default.ini

RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN mkdir /etc/service/couchdb
ADD runcouchdb.sh /etc/service/couchdb/run
ADD finish.sh /etc/service/couchdb/finish

# Define mountable directories.
VOLUME ["/var/log/couchdb", "/var/lib/couchdb", "/etc/couchdb"]

EXPOSE 5984

# Use phusion/baseimage-docker's init system.
CMD ["/sbin/my_init"]
