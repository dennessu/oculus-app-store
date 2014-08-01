#Use docker to run development environment

Today we need to run couchdb and postgresql in our local dev machine, we might have to run more open source services such as ElasticSearch, memcached, etc in the future. It would be nice if we can have a simple way to setup all these external dependencies, and if everyone can run on the same environments, we can avoid introducing platform specific bugs.

Docker is the answer.

We can now run postgresql and couchdb in docker containers, and here is the instructions.

**To set it up, you need to stop/uninstall your local postgres couchdb first.**

## Mac OSX Users

Docker can only run in linux environment. We can use Vagrant to start a uniform linux VM.

### Install Vagrant and VirtualBox

Our vagrant box is based on virtual box. So go ahead to install the latest virtualbox(64 bit):

```
https://www.virtualbox.org/wiki/Downloads
```

And then install vagrant(64 bit):

```
http://www.vagrantup.com/downloads.html
```

Make sure you can run vagrant in your terminal. Then go to the root of our source code repo, run:
```
$ vagrant up

# By default, it would launch a VM with 2G memory, you can adjust it:
$ BOX_MEM=3072 vagrant up
```

This would download the box image, so it would takes a couple minutes in the first time.

You need to input your **local account password** during `vagrant up`, because we are using NFS as shared folder protocol, which is much faster than virtual box shared folder.

Now the virtual box is up and running, connect to it:
```
vagrant ssh
```

FYI, some vagrant commands you need to know:
```
# shutdown machine
vagrant halt
# reboot machine
vagrant reload
# pause machine
vagrant suspend
# check global status
vagrant global-status
# delete VM
vagrant destroy VMID
```

### Run docker containers inside VM

You are now inside the VM, run this to start postgres and couchdb:
```
# devenv.sh is in the root of source repo, so you can find it in ~/src
$ ./denenv.sh start
```

The script would pull down our docker images and run them as containers, it would take several minutes depends on the network speed. The images would be cached locally, so next time it would be lightning fast.

It is done! You have a VM running postgres and couchdb and the port forwarding is all set.

Our VM has JDK7 and gradle installed, you can build the code inside the VM, but since gradle is a memory monster, make sure you have allocate enough memory for the VM, otherwise build would fail or hang.

Of course, you can still build in your local MAC environment, and run intellij outside the VM.


## Windows Users

Windows user can also use Vagrant to run a VM, however virtualbox's performance is not so good on windows. Strongly recommend windows users to install VMWare Player(free) and run an **ubuntu 14.04** linux as your development. Also recommend **Kubuntu 14.04**.

If you are using VMWare + Ubuntu, please read the sections for Linux Users below.

If you want to use Vagrant, let's go on.

### Install Vagrant and VirtualBox

Our vagrant box is based on virtual box. So go ahead to install the latest virtualbox(64 bit):

```
https://www.virtualbox.org/wiki/Downloads
```

And then install vagrant(64 bit):

```
http://www.vagrantup.com/downloads.html
```

Make sure you can run vagrant in your terminal. Then go to the root of our source code repo, run:
```
# IMPORTANT! make sure you cmd or shell is running in admin mode!
$ vagrant up

# By default, it would launch a VM with 2G memory, you can adjust it:
$ SET BOX_MEM=3072 && vagrant up
```

This would download the box image, so it would takes a couple minutes in the first time.

Now the virtual box is up and running, connect to it:
```
vagrant ssh
```

FYI, some vagrant commands you need to know:
```
# shutdown machine
vagrant halt
# reboot machine
vagrant reload
# pause machine
vagrant suspend
# check global status
vagrant global-status
# delete VM
vagrant destroy VMID
```

### Run docker containers inside VM

You are now inside the VM, run this to start postgres and couchdb:
```
# devenv.sh is in the root of source repo, so you can find it in ~/src
$ ./denenv.sh start
```

The script would pull down our docker images and run them as containers, it would take several minutes depends on the network speed. The images would be cached locally, so next time it would be lightning fast.

It is done! You have a VM running postgres and couchdb and the port forwarding is all set.

Our VM has JDK7 and gradle installed, you can build the code inside the VM, but since gradle is a memory monster, make sure you have allocate enough memory for the VM, otherwise build would fail or hang.

Of course, you can still build in your local windows environment, and run intellij outside the VM.


## Linux Users

Welcome, if you are from windows VMWare, you have made a great choice!

Firstly, let install docker and docker tools
```
curl -s https://gist.githubusercontent.com/jubot/4dd5ea0d8789ddc0eaa5/raw/f4b979c342f69ebc93c5835a7680241e4bebcd86/installdocker.sh | sudo sh
```

Please notice that this script only support ubuntu, if you are using other disto, go to `https://docs.docker.com/` to get the instruction.

Then you need to clone our source code repo, and there is only 1 step further:
```
$ ./devenv.sh start
```

Done, postgresql and couchdb is up and running!

To develop in linux, you would also need to install JDK7 and gradle, you can refer to our Vagrant setup:
```
docker/vagrant/sc-localdev-base/VagrantFile
```

Of course, you can install intellij for linux.

VMWare player has a great feature called Unity, check it out. The whole experience would be very similar to your previous windows. By running everything in real linux, you can help Shu Zhang to save more time, otherwise he need to write scripts for all platforms.

## Docker operations

Docker has quite a few concept, you can check their fantastic doc site: https://docs.docker.com/userguide/

Our `devenv.sh` can simplify your work with docker, just run it without any argument to see what it supports.

## About our docker images

We have a docker registry account: https://registry.hub.docker.com/repos/silkcloud/

You can find the images on the web.

The source code of the docker images:
```
docker/images/onebox-psql
docker/images/onebox-couchdb
```
