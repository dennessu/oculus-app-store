This Vagrantfile is used to build base devbox image.

## What's included
* Based on ubuntu 14.04 x64
* Some essential tools like git, tmux
* JDK 7
* Gradle 2.0
* Arcanist

JDK and Gradle are quite big, to save time, build them into the base image,

## What's Not
* Postgresql
* Couchdb

We might use a different way to run pgsql and couchdb, so this base image does not include them.

To build dev vagrant box, use this box as base, then manually install the databases.

## How to build this box
Firstly, shutdown all the other vagrant boxes. If there are boxes based on this Vagrantfile, destroy them all first.

Then just run `vagrant up`, vagrant would download the ubuntu base image if it is not available on your machine, then provision the box.

After the box is up, `vagrant ssh` to connect to it. Run the following commands to nullify the virtual disk, so that the final image size can be smaller.

```bash
sudo dd if=/dev/zero of=/bigemptyfile bs=4096k
sudo rm -rf /bigemptyfile
```

The first dd command would throw not enough space error, that's expected, just continue.

Finally, log out the vagrant box, execute `vagrant package`, you will get a new base box image.

### Version History
* 2014-07-11, first version. Uploaded to `http://arti.silkcloud.info/sc-localdev-base-20140711.box` Image size = 577MB
* 2014-07-13, add cifs-utils for smb mount, remove chef and puppet, change box name. Uploaded to `http://arti.silkcloud.info/sc-localdev-base-20140713.box` Image size = 590MB
* 2014-07-24, add docker and docker-tools, Uploaded to `http://arti.silkcloud.info/sc-localdev-base-20140724.box` Image size = 579MB
* 2014-07-25, add postgresql-client, uploaded to `http://arti.silkcloud.info/sc-localdev-base-20140725.box` Image size = 581MB
