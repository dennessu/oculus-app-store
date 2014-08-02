Phusion base + JDK7

## Features
* Based on phusion/baseimage:latest
* Include latest JDK7

## Volumes

## Usage

To build the image `silkcloud/jdk7base` , run:

```
sudo docker build --rm -t silkcloud/jdk7base .
```

To publish to docker hub, run:

```
sudo docker login
sudo docker push silkcloud/jdk7base
```
