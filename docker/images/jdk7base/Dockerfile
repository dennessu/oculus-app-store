FROM phusion/baseimage:latest
MAINTAINER Tianxiang Chen "txchen@silkcloud.com"

# Set correct environment variables.
ENV HOME /root

# Install JDK7
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections &&\
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections &&\
    add-apt-repository -y ppa:webupd8team/java &&\
    apt-get update -qq &&\
    DEBIAN_FRONTEND=noninteractive apt-get install -qqy --force-yes oracle-java7-installer rinetd postgresql-client-9.3 python memcached &&\
    rm -f -r /var/cache/oracle-jdk7-installer &&\
    apt-get clean &&\
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
