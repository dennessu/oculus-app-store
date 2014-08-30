https://registry.hub.docker.com/u/silkcloud/onebox-elk/

Docker image to run elk (elasticsearch, logstash, kibana)

## ELK Versions
- elasticsearch 1.3.2
- logstash 1.4.2
- kibana 3.1.0

## Features
- To browse kibana site, only one port (9000) is needed
- Support specify logstash.conf
- Based on phusion-base
- Logstash config is located at /config/logstash.conf
- By default, logstash reads log from /inputlogs

## Usage

Suppose /path/to/logs is the folder that contains your logs, and you want to feed them to elk, just run:

```
sudo docker run -d -p 9000:9000 \
  -v /path/to/logs:/inputlogs silkcloud/onebox-elk
```

If you want to change the kibana port in your docker host, you also need to specify ENV KIBANA_PORT:

```
sudo docker run -d -p 7000:9000 -e KIBANA_PORT=7000 \
  -v /path/to/logs:/inputlogs silkcloud/onebox-elk
```

If you want to use your own logstash config, put it to /config/logstash.conf, and make sure your config has defined `sincedb_path`, otherwise, logstash will crash:

```
sudo docker run -d -p 9000:9000 \
  -v /localconfig/path:/config \
  -v /path/to/logs:/inputlogs silkcloud/onebox-elk
```

To build the image `silkcloud/onebox-elk` , run:

```
sudo docker build --rm -t silkcloud/onebox-elk .
```

To publish to docker hub, run:

```
sudo docker login
sudo docker push silkcloud/onebox-elk
```
