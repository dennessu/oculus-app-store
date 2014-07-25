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
  sudo docker ps
  ids=`sudo docker ps | tail -n +2 |cut -d ' ' -f 1`
  echo $ids | xargs -r sudo docker kill
  echo $ids | xargs -r sudo docker rm
}

stop(){
  echo "Stopping all docker containers:"
  sudo docker ps
  ids=`sudo docker ps | tail -n +2 |cut -d ' ' -f 1`
  echo $ids | xargs -r sudo docker stop
  echo $ids | xargs -r sudo docker rm
}

dockerssh(){
  if [ -z "$1" ]; then
    echo "! Please specify container id or container name"
    exit 1
  fi
  sudo docker-ssh $1
}

dockerenter(){
  if [ -z "$1" ]; then
    echo "Please specify container id or container name"
    exit 1
  fi
  sudo docker-bash $1
}

start(){
  mkdir -p $DATADIR/couchdb/data
  mkdir -p $DATADIR/couchdb/logs
  sudo docker rm couchdb > /dev/null 2>&1 || true
  echo "Pulling latest image - silkcloud/onebox-couchdb ..."
  sudo docker pull silkcloud/onebox-couchdb > /dev/null
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
  sudo docker rm psql > /dev/null 2>&1 || true
  echo "Pulling latest image - silkcloud/onebox-psql ..."
  sudo docker pull silkcloud/onebox-psql > /dev/null
  PSQL=$(sudo docker run \
    -d \
    -p 5432:5432 \
    -v $DATADIR/psql/data:/data \
    -v $DATADIR/psql/logs:/var/log/postgresql \
    --name=psql \
    -e PSQL_PASS='#Bugsfor$' \
    silkcloud/onebox-psql)
  echo "Started PSQL in container $PSQL"

  sudo docker ps
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
  status)
    sudo docker ps
    ;;
  ssh)
    dockerssh $2
    ;;
  enter)
    dockerenter $2
    ;;
  *)
    echo $"Usage: $0 {start|stop|kill|restart|status|ssh <id/name>|enter <id/name>}"
    RETVAL=1
esac
