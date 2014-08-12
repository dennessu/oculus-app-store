# Run docker based silkcloud onebox

Currently our system has the following depedencies:

* postgresql
* couchdb/cloudant
* memcached
* JDK7 runtime

These are all wrapped in docker images, so that we can spin up an onebox service purely from docker.

## Our docker images list

* onebox-psql (public): https://registry.hub.docker.com/u/silkcloud/onebox-psql/
* onebox-couchdb (public): https://registry.hub.docker.com/u/silkcloud/onebox-couchdb/
* onebox-memcached (public): https://registry.hub.docker.com/u/silkcloud/onebox-memcached/
* onebox-app (private): https://registry.hub.docker.com/u/silkcloud/onebox-app/

There is further information about images on the dockerhub pages. Click the links to check it out.

All of our docker images are based on phusion/baseimage, you can use phusion's `docker-ssh` or `docker-bash` to get into the containers.

Make sure you have access to `onebox-app` before you continue.

## Launch db/cache instances

It is recommended to create a folder to store database files, so that when we upgrade docker images, data never got lost.

You can create these folders anywhere, here is just any example:

```bash
mkdir -p ~/scdockerdata
mkdir -p ~/scdockerdata/psql
mkdir -p ~/scdockerdata/couchdb
mkdir -p ~/scdockerdata/applogs
mkdir -p ~/scdockerdata/appconfig
```

Now, launch psql instance. Our onebox assume the db password is #Bugsfor$, so we need to specify it.

And You don't have to use -p 5432:5432, since the onebox-app would use link to connect to psql.

```bash
sudo docker run \
  -d \
  -p 5432:5432 \
  --name=psql \
  -e PSQL_PASS='#Bugsfor$' \
  -v ~/scdockerdata/psql:/data \
  silkcloud/onebox-psql
```

Then launch couchdb instance. Similarly you don't have to open port.

```bash
sudo docker run \
  -d \
  -p 5984:5984 \
  --name=couchdb \
  -v /~/scdockerdata/couchdb:/var/lib/couchdb \
  silkcloud/onebox-couchdb
```

Then, memcached. Port is not required as well.

```bash
sudo docker run \
  -d \
  -p 11211:11211 \
  --name=memcached \
  silkcloud/onebox-memcached
```

Run `sudo docker ps` to check if all of them are running.

## Launch apphost

Make sure you have the access to pull onebox-app image.

```bash
sudo docker pull silkcloud/onebox-app:master
```

Every time we push the onebox-app image to dockerhub, we create two tags. 1. The branch name 2. branchname-commitid

silkcloud/onebox-app:master is the lastest of master branch, you can also get image of particular commit.

Before we continue, we need to create the configuration first, otherwise apphost cannot start.

Let's assume you create the config file at `~/scdockerdata/appconfig/configuration.properties`

The content should be like:

```
environment=onebox.int
profile=apphost-cli
common.cloudant.dbNamePrefix=youruniqueprefix_
```

You can override any configurations in this file. But these 3 are mandatory.

The `environment` can be `onebox` or `onebox.int`, our AWS oneboxes are all using onebox.int, so recommend to use this one.

`profile` must be `apphost-cli`, if you want to run the app with full features.

`common.cloudant.dbNamePrefix` is importatnt, since we are now using a shared cloudant account to store catalog data, we must pick a unique prefix to avoid conflict. Recommend to use a string that contains your alias.

Now you have your configuration.properties ready, but before we run the apphost, firstly, we need to populate the databases and load domain data:

```bash
sudo docker run --rm \
  --link psql:psql \
  --link couchdb:couchdb \
  --link memcached:memcached \
  -v ~/scdockerdata/appconfig:/etc/silkcloud \
  silkcloud/onebox-app:master \
  /var/silkcloud/scripts/init_databases.sh
```

Please notice that links must be specified, and the aliases(2nd part) must be psql/couchdb/memcached, otherwise it will fail.

If there is no error, you will see `#### finished preparing databases.`, then you can continue to run the apphost service:

```bash
sudo docker run -d \
  -p 8080:8080 \
  -p 8081:8081 \
  --link psql:psql \
  --link couchdb:couchdb \
  --link memcached:memcached \
  --name=onebox-app \
  -v ~/scdockerdata/appconfig:/etc/silkcloud \
  -v ~/scdockerdata/applogs:/var/silkcloud/logs \
  silkcloud/onebox-app:master
```

It would take about 1 minute to start up, after that, it would listen at 8080 and 8081 port. Open `http://localhost:8080/v1/offers` to check.

If the container stops after a while, means it failed to boot, use `sudo docker logs onebox-app` to check what happened.
