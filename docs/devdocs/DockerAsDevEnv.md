# Use docker to run development environment

Today we need to run couchdb, postgresql and memcached in our local dev machine, we might have to run more open source services such as ElasticSearch, Redis, etc in the future. It would be nice if we can have a simple way to setup all these external dependencies, and if everyone can run on the same environments, we can avoid introducing platform specific bugs.

Docker is the answer.

We can now run postgresql and couchdb in docker containers, and here is the instructions.

**To set it up, you need to stop/uninstall your local postgresql/couchdb/memcached first.**

## Mac OSX/Windows Users

Boot2docker 1.3 improved a lot, to run docker image, we can use that as client tool.

For windows, running an Ubuntu VM inside VMWare Player is also recommended.

### Install boot2docker

Follow official site: http://boot2docker.io/

### Run docker containers

You can now use docker commands, run this to start postgres, couchdb and memcached:
```
# devenv.sh is in the root of source repo
$ ./devenv.sh start
```

The script would pull down our docker images and run them as containers, it would take several minutes depends on the network speed. The images would be cached locally, so next time it would be lightning fast.

It is done! Now you have all the depedencies running in the docker containers.

## Linux Users

Welcome, if you are from windows VMWare, you have made a great choice!

Firstly, let install docker and docker tools
```
curl -s https://get.docker.io/ubuntu/ | sudo sh
```

Please notice that this script only support ubuntu, if you are using other disto, go to `https://docs.docker.com/` to get the instruction.

Then you need to clone our source code repo, and there is only 1 step further:
```
$ ./devenv.sh start
```

Done, postgresql, memcached and couchdb is up and running!

To develop in linux, you would also need to install JDK7 and gradle

Of course, you can install intellij for linux.

VMWare player has a great feature called Unity, check it out. The whole experience would be very similar to your previous windows. By running everything in real linux, you can help Shu Zhang to save more time, otherwise he need to write scripts for all platforms.

## Docker operations

Docker has quite a few concept, you can check their fantastic doc site: https://docs.docker.com/userguide/

Our docker images are based on phusion-base, please checkout their site for more information: http://phusion.github.io/baseimage-docker/

Our `devenv.sh` can simplify your work with docker, just run it without any argument to see what it supports.

To enter the running container, you can:
```
$ sudo docker exec -it <container id or name> bash
```

## About our docker images

We have a docker registry account: https://registry.hub.docker.com/repos/silkcloud/

You can find the images on the web.

The source code of the docker images:
```
docker/images/onebox-psql
docker/images/onebox-couchdb
docker/images/onebox-memcached
docker/images/jdk7base
```
