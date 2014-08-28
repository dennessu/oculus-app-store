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

RUN cd /tmp && wget https://download.elasticsearch.org/kibana/kibana/kibana-3.1.0.tar.gz -nv && \
    tar xzf kibana-3.1.0.tar.gz -C /var/elk/kibana --strip-components=1 && \
    rm /tmp/kibana-3.1.0.tar.gz

RUN DEBIAN_FRONTEND=noninteractive apt-get update -qq &&\
    DEBIAN_FRONTEND=noninteractive apt-get install -qqy --force-yes nginx &&\
    apt-get clean &&\
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

ADD runlogstash.sh /etc/service/logstash/run
RUN chmod +x /etc/service/logstash/run
ADD runelasticsearch.sh /etc/service/elasticsearch/run
RUN chmod +x /etc/service/elasticsearch/run

ADD purge_databases.sh /var/silkcloud/scripts/purge_databases.sh
RUN chmod +x /var/silkcloud/scripts/purge_databases.sh
