https://registry.hub.docker.com/u/silkcloud/onebox-psql/

Docker image to run a local dev postgresql server

## Features
* Based on phusion/baseimage
* Listen at 0.0.0.0, allow network connections from anywhere
* Use default user "postgres"
* Support changing password, by specify ENV 'PSQL_PASS'
* /data as data folder, during boot, the owner of folder would be changed to postgres, permission to 700

## Folders
* /etc/postgresql (config folder)
* /var/log/postgresql (log folder, exposed as Volume)
* /data (data folder, exposed as Volume)

## Usage
To run the image and bind to port 5432:

```
sudo docker run -d -p 5432:5432 --name=psql -e PSQL_PASS='PASSWORD_FOR_POSTGRES' silkcloud/onebox-psql
```

To persist the data, mount a folder from host to the data volume:

```
sudo docker run -d -p 5432:5432 --name=psql -v /path/to/data/dir:/data silkcloud/onebox-psql
```

If the folder is empty, container would initialize the db. Otherwise, the folder must be a valid postgresql data foler.

To verify, run the following command and type password:

```
psql -lt -U postgres -W -h localhost
```

To build the image `silkcloud/onebox-psql` , run:

```
sudo docker build --rm -t silkcloud/onebox-psql .
```

To publish to docker hub, run:

```
sudo docker login
sudo docker push silkcloud/onebox-psql
```
