FROM phusion/baseimage:latest
MAINTAINER Tianxiang Chen "txchen@silkcloud.com"

# Set correct environment variables.
ENV HOME /root

RUN locale-gen en_US.UTF-8 && echo 'LANG="en_US.UTF-8"' > /etc/default/locale

# Install psql 9.3
RUN apt-get update &&\
    DEBIAN_FRONTEND=noninteractive apt-get install -y --force-yes postgresql-9.3 &&\
    /etc/init.d/postgresql stop &&\
    apt-get clean &&\
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Change data directory location
RUN sed -i -e"s/^data_directory =.*$/data_directory = '\/data'/" /etc/postgresql/9.3/main/postgresql.conf
# Allow connections from anywhere.
RUN sed -i -e"s/^#listen_addresses =.*$/listen_addresses = '*'/" /etc/postgresql/9.3/main/postgresql.conf
RUN echo "host    all    all    0.0.0.0/0    md5" >> /etc/postgresql/9.3/main/pg_hba.conf
# Tune connection settings
RUN sed -i -e"s/^#max_prepared_transactions =.*$/max_prepared_transactions = 100/" /etc/postgresql/9.3/main/postgresql.conf
RUN sed -i -e"s/^max_connections =.*$/max_connections = 500/" /etc/postgresql/9.3/main/postgresql.conf

# Add daemon to be run by runit
RUN mkdir /etc/service/postgresql
ADD runpsql.sh /etc/service/postgresql/run
RUN chmod +x /etc/service/postgresql/run

EXPOSE 5432

# Expose log and data directories as Volume, to improve performance
VOLUME ["/var/log/postgresql", "/data"]

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]
