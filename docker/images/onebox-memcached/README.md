Docker image to run a local dev memcached server

## Features
* Based on phusion/baseimage
* Listen at 0.0.0.0
* Max Memory(-m, default 64MB) can be specified via env var 'MAX_MEM'
* Max Connection(-c, default 1024) can be specified via env var 'MAX_CONN'

## Usage
To run the image and bind to port 11211:

```
sudo docker run -d -p 11211:11211 --name=memcached silkcloud/onebox-memcached
```

To change Max Memory or Max Connection:

```
sudo docker run -d -p 11211:11211 -e MAX_MEM=512 -e MAX_CONN=2048 --name=memcached silkcloud/onebox-memcached
```

To build the image `silkcloud/onebox-memcached` , run:

```
sudo docker build --rm -t silkcloud/onebox-memcached .
```

To publish to docker hub, run:

```
sudo docker login
sudo docker push silkcloud/onebox-memcached
```
