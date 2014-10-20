FROM phusion/baseimage:latest
MAINTAINER Tianxiang Chen "txchen@silkcloud.com"

# Set correct environment variables.
ENV HOME /root

RUN locale-gen en_US.UTF-8 && echo 'LANG="en_US.UTF-8"' > /etc/default/locale

# install memcached
RUN apt-get update &&\
    DEBIAN_FRONTEND=noninteractive apt-get install -y memcached &&\
    apt-get clean &&\
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN mkdir /etc/service/memcached
ADD memcached.sh /etc/service/memcached/run
RUN chmod +x /etc/service/memcached/run

# memcached public variable
ENV MAX_MEM 64
ENV MAX_CONN 1024

EXPOSE 11211

# Use phusion/baseimage-docker's init system.
CMD ["/sbin/my_init"]
