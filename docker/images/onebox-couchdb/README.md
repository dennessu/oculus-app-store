https://registry.hub.docker.com/u/silkcloud/onebox-couchdb/

Docker image to run a local dev couchdb server

## Features
* Based on phusion/baseimage
* Listen at 0.0.0.0

## Folders
* /var/log/couchdb (log folder, exposed as Volume)
* /var/lib/couchdb (data folder, exposed as Volume)
* /etc/couchdb (config folder)

## Usage
To run the image and bind to port 5984:

```
sudo docker run -d -p 5984:5984 --name=couchdb silkcloud/onebox-couchdb
```

To build the image `silkcloud/onebox-couchdb` , run:

```
sudo docker build --rm -t silkcloud/onebox-couchdb .
```

To publish to docker hub, run:

```
sudo docker login
sudo docker push silkcloud/onebox-couchdb
```
