Docker image to run a local dev couchdb server

## Features
* Based on phusion/baseimage
* Listen at 0.0.0.0

## Volumes
* /var/log/couchdb
* /var/lib/couchdb
* /etc/couchdb

## Usage
To build the image `silkcloud/onebox-couchdb` , run:
```
docker build --rm -t silkcloud/onebox-couchdb .
```

To run the image and bind to port 5984:
```
docker run -d -p 5984:5984 silkcloud/onebox-couchdb
```
