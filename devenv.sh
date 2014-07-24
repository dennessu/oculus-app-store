#!/bin/bash
set -e

if [[ `uname` != 'Linux' ]]; then
  echo "OS is not linux, cannot run"
  exit 2
fi

hash docker >/dev/null 2>&1 || { echo "!! docker not installed, cannot continue"; exit 2; }

DATADIR=${DOCKERDATADIR:-$HOME/dockerdata}

killz(){
  echo "Killing all docker containers:"
  docker ps
  ids=`docker ps | tail -n +2 |cut -d ' ' -f 1`
  echo $ids | xargs -r docker kill
  echo $ids | xargs -r docker rm
}

stop(){
  echo "Stopping all docker containers:"
  docker ps
  ids=`docker ps | tail -n +2 |cut -d ' ' -f 1`
  echo $ids | xargs -r docker stop
  echo $ids | xargs -r docker rm
}

start(){
  mkdir -p $DATADIR/couchdb/data
  mkdir -p $DATADIR/couchdb/logs
  sudo docker rm couchdb > /dev/null 2>&1 || true
  COUCHDB=$(sudo docker run \
    -d \
    -p 5984:5984 \
    -v $DATADIR/couchdb/logs:/var/log/couchdb \
    -v $DATADIR/couchdb/data:/var/lib/couchdb \
    --name=couchdb \
    silkcloud/onebox-couchdb)
  echo "Started COUCHDB in container $COUCHDB"

  mkdir -p $DATADIR/psql/data
  mkdir -p $DATADIR/psql/logs
  docker rm PSQL_PASS > /dev/null 2>&1 || true
  PSQL=$(sudo docker run \
    -d \
    -p 5432:5432 \
    -v $DATADIR/psql/data:/var/lib/postgresql \
    -v $DATADIR/psql/logs:/var/log/postgresql \
    --name=psql \
    -e PSQL_PASS='#Bugsfor$' \
    silkcloud/onebox-psql)
  echo "Started PSQL in container $PSQL"

}

case "$1" in
  restart)
    killz
    start
    ;;
  start)
    start
    ;;
  stop)
    stop
    ;;
  kill)
    killz
    ;;
  *)
    echo $"Usage: $0 {start|stop|kill|restart}"
    RETVAL=1
esac
