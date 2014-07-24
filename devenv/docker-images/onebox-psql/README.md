Docker image to run a local dev postgresql server

## Features
* Based on phusion/baseimage
* Listen at 0.0.0.0, allow network connections from anywhere
* Use default user "postgres"
* Support changing password, by specify ENV 'PSQL_PASS'
* /data as data folder

## Volumes
* /etc/postgresql
* /var/log/postgresql
* /data

## Usage
To run the image and bind to port 5432:

```
docker run -d -p 5432:5432 --name=psql -e PSQL_PASS='PASSWORD_FOR_POSTGRES' silkcloud/onebox-psql
```

To verify, run the following command and type password:

```
psql -lt -U postgres -W -h localhost
```

To build the image `silkcloud/onebox-psql` , run:

```
docker build --rm -t silkcloud/onebox-psql .
```

To publish to docker hub, run:

```
docker login
docker push silkcloud/onebox-psql
```
