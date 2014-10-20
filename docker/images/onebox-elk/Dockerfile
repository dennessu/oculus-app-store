FROM silkcloud/jdk7base:latest
MAINTAINER Tianxiang Chen "txchen@silkcloud.com"

# Set correct environment variables.
ENV HOME /root

RUN locale-gen en_US.UTF-8 && echo 'LANG="en_US.UTF-8"' > /etc/default/locale

RUN mkdir -p /var/elk && cd /var/elk && mkdir logstash && mkdir elasticsearch && mkdir kibana

RUN cd /tmp && wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.3.2.tar.gz -nv && \
    tar xzf elasticsearch-1.3.2.tar.gz -C /var/elk/elasticsearch --strip-components=1 && \
    rm /tmp/elasticsearch-1.3.2.tar.gz

RUN cd /tmp && wget https://download.elasticsearch.org/logstash/logstash/logstash-1.4.2.tar.gz -nv && \
    tar xzf logstash-1.4.2.tar.gz -C /var/elk/logstash --strip-components=1 && \
    rm /tmp/logstash-1.4.2.tar.gz
RUN mkdir -p /inputlogs

RUN cd /tmp && wget https://download.elasticsearch.org/kibana/kibana/kibana-3.1.0.tar.gz -nv && \
    tar xzf kibana-3.1.0.tar.gz -C /var/elk/kibana --strip-components=1 && \
    rm /tmp/kibana-3.1.0.tar.gz

RUN DEBIAN_FRONTEND=noninteractive apt-get update -qq &&\
    DEBIAN_FRONTEND=noninteractive apt-get install -qqy --force-yes nginx &&\
    apt-get clean &&\
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
# make nginx run foreground
RUN echo "daemon off;" >> /etc/nginx/nginx.conf
RUN rm /etc/nginx/sites-enabled/*

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]

ADD bootcheck.sh /etc/my_init.d/bootcheck.sh
RUN chmod +x /etc/my_init.d/bootcheck.sh

ADD runlogstash.sh /etc/service/logstash/run
RUN chmod +x /etc/service/logstash/run
ADD runelasticsearch.sh /etc/service/elasticsearch/run
RUN chmod +x /etc/service/elasticsearch/run
ADD runnginx.sh /etc/service/nginx/run
RUN chmod +x /etc/service/nginx/run

ADD kibana_site.conf /etc/nginx/sites-enabled/kibana_site.conf
ADD default_logstash.conf /config/logstash.conf
ADD kibana_config.js /var/elk/kibana/config.js

# Expose data and log (to improve performance)
VOLUME ["/var/elk/elasticsearch/logs", "/var/elk/logstash/data", "/var/elk/elasticsearch/data", "/var/log/nginx"]
