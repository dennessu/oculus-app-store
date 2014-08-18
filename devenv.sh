#!/bin/bash
set -e

if [[ `uname` != 'Linux' ]]; then
  echo "OS is not linux, cannot run"
  exit 2
fi

hash docker >/dev/null 2>&1 || { echo "!! docker not installed, cannot continue"; exit 2; }

CONTAINER_NAMES="psql couchdb memcached"

DATADIR=${DOCKERDATADIR:-$HOME/dockerdata}

killz(){
  echo "Killing all dev-env docker containers:"
  sudo docker ps
  echo $CONTAINER_NAMES | xargs -r -n 1 sudo docker kill
  echo $CONTAINER_NAMES | xargs -r -n 1 sudo docker rm
  echo "All the dev-env containers have been killed and removed"
}

stop(){
  echo "Stopping all dev-env docker containers:"
  sudo docker ps
  echo $CONTAINER_NAMES | xargs -r -n 1 sudo docker stop
  echo "All the dev-env containers have been stopped"
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

resume_container() {
  CONTAINER=$1
  RUNNING=$(sudo docker inspect --format="{{ .State.Running }}" $CONTAINER 2> /dev/null)
  if [ $? -eq 1 ]; then
    echo "Container - $CONTAINER does not exist."
    return 1
  fi

  if [ "$RUNNING" == "false" ]; then
    echo "Container - $CONTAINER is not running. Will start it"
    sudo docker start $CONTAINER
    return 0
  fi

  if [ "$RUNNING" == "true" ]; then
    echo "Container - $CONTAINER is already running"
    return 0
  fi
}

start(){
  # CouchDB
  mkdir -p $DATADIR/couchdb/data
  mkdir -p $DATADIR/couchdb/logs

  resume_container couchdb || {
    echo "Pulling latest image - silkcloud/onebox-couchdb ..."
    sudo docker pull silkcloud/onebox-couchdb > /dev/null
    COUCHDB=$(sudo docker run \
      -d \
      -p 5984:5984 \
      -v $DATADIR/couchdb/logs:/var/log/couchdb \
      -v $DATADIR/couchdb/data:/var/lib/couchdb \
      --name=couchdb \
      silkcloud/onebox-couchdb)
    echo "Started COUCHDB in new container $COUCHDB"
  }

  # Postgresql
  mkdir -p $DATADIR/psql/data
  mkdir -p $DATADIR/psql/logs

  resume_container psql || {
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
    echo "Started PSQL in new container $PSQL"
  }

  # Memcached
  resume_container memcached || {
    echo "Pulling latest image - silkcloud/onebox-memcached ..."
    sudo docker pull silkcloud/onebox-memcached > /dev/null
    MEMCACHED=$(sudo docker run \
      -d \
      -p 11211:11211 \
      --name=memcached \
      silkcloud/onebox-memcached)
    echo "Started MEMCACHED in new container $MEMCACHED"
  }

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
