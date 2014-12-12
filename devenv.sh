#!/bin/bash
set -e

hash docker >/dev/null 2>&1 || { echo "!! docker not installed, cannot continue"; exit 2; }

if [[ $(uname) == "Linux" ]]; then
  # Linux environment, use docker directly
  DOCKER_CMD="sudo docker"
else
  # Non-Linux environment, use boot2docker
  DOCKER_CMD="docker"
  hash boot2docker >/dev/null 2>&1 || { echo "!! boot2docker not installed, cannot continue"; exit 2; }
fi

CONTAINER_NAMES="psql couchdb memcached"
FORWARDED_PORTS="5432 5984 11211"

DATADIR=${DOCKERDATADIR:-$HOME/ovr-dockerdata}

initboot2docker(){
  if [[ $(uname) != "Linux" ]]; then
    if [[ $(boot2docker status) != "running" ]]; then
      echo "Boot2docker not started, starting it now."
      setupportfwd
      boot2docker start
    fi
    $(boot2docker shellinit 2>/dev/null)
  fi
}

showdownvm(){
  if [[ $(uname) != "Linux" ]]; then
    echo "Shutting down boot2docker vm..."
    boot2docker down
  fi
}

exitifboot2dockernotstarted(){
  if [[ $(uname) != "Linux" ]]; then
    if [[ $(boot2docker status) != "running" ]]; then
      echo "Boot2docker not started, exiting..."
      exit 0
    fi
    $(boot2docker shellinit 2>/dev/null)
  fi
}

# boot2docker would only setup port forwarding at vm level
# from mac/windows to vm, we need another level of forwarding
setupportfwd(){
  if [[ $(uname) != "Linux" ]]; then
    for i in $(echo $FORWARDED_PORTS); do
      VBoxManage modifyvm "boot2docker-vm" --natpf1 delete "tcp-port$i" > /dev/null 2>&1 || true
      VBoxManage modifyvm "boot2docker-vm" --natpf1 "tcp-port$i,tcp,,$i,,$i"
    done
  fi
}

checkportforward(){
  if [[ $(uname) != "Linux" ]]; then
    for i in $(echo $FORWARDED_PORTS); do
      VBoxManage showvminfo "boot2docker-vm" | grep -q tcp-port$i || echo "!!! Tcp port forwarding not set for $i"
    done
  fi
}

killz(){
  exitifboot2dockernotstarted
  echo "Killing all dev-env docker containers:"
  $DOCKER_CMD ps
  echo $CONTAINER_NAMES | xargs -n 1 $DOCKER_CMD rm -f
  echo "All the dev-env containers have been killed and removed"
}

stop(){
  exitifboot2dockernotstarted
  echo "Stopping all dev-env docker containers:"
  $DOCKER_CMD ps
  echo $CONTAINER_NAMES | xargs -n 1 $DOCKER_CMD stop
  echo "All the dev-env containers have been stopped"
}

dockerexec(){
  initboot2docker
  if [ -z "$1" ]; then
    echo "Please specify container id or container name"
    exit 1
  fi
  $DOCKER_CMD exec -it $1 bash
}

resume_container() {
  CONTAINER=$1
  RUNNING=$($DOCKER_CMD inspect --format="{{ .State.Running }}" $CONTAINER 2> /dev/null)
  if [ $? -eq 1 ]; then
    echo "Container - $CONTAINER does not exist."
    return 1
  fi

  if [ "$RUNNING" == "false" ]; then
    echo "Container - $CONTAINER is not running. Will start it"
    $DOCKER_CMD start $CONTAINER
    return 0
  fi

  if [ "$RUNNING" == "true" ]; then
    echo "Container - $CONTAINER is already running"
    return 0
  fi
}

start(){
  initboot2docker
  # CouchDB
  mkdir -p $DATADIR/couchdb/data
  mkdir -p $DATADIR/couchdb/logs

  resume_container couchdb || {
    echo "Pulling latest image - silkcloud/onebox-couchdb ..."
    $DOCKER_CMD pull silkcloud/onebox-couchdb
    COUCHDB=$($DOCKER_CMD run \
      -d \
      --restart=always \
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
    $DOCKER_CMD pull silkcloud/onebox-psql
    PSQL=$($DOCKER_CMD run \
      -d \
      --restart=always \
      -p 5432:5432 \
      -v $DATADIR/psql/logs:/var/log/postgresql \
      --name=psql \
      -e PSQL_PASS='#Bugsfor$' \
      silkcloud/onebox-psql)
      # boot2docker issue 581 workaround: chmod cannot work inside container if data is mounted as volume
      # so don't mount data folder now
      #-v $DATADIR/psql/data:/data \
    echo "Started PSQL in new container $PSQL"
  }

  # Memcached
  resume_container memcached || {
    echo "Pulling latest image - silkcloud/onebox-memcached ..."
    $DOCKER_CMD pull silkcloud/onebox-memcached
    MEMCACHED=$($DOCKER_CMD run \
      -d \
      --restart=always \
      -p 11211:11211 \
      --name=memcached \
      silkcloud/onebox-memcached)
    echo "Started MEMCACHED in new container $MEMCACHED"
  }

  $DOCKER_CMD ps

  checkportforward
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
    exitifboot2dockernotstarted
    $DOCKER_CMD ps
    ;;
  bash)
    dockerexec $2
    ;;
  shutdown)
    showdownvm
    ;;
  *)
    echo $"Usage: $0 {start|stop|kill|restart|status|shutdown|bash <id/name>}"
    RETVAL=1
esac
