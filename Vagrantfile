# -*- mode: ruby -*-
# vi: set ft=ruby :

# We use ubuntu 14.04 in prod, this vagrant image is also based on ubuntu 14.04
# you can use the vm to simulate prod os environment

BOX_NAME = ENV['BOX_NAME'] || "trusty64"
BOX_URI = ENV['BOX_URI'] || "https://cloud-images.ubuntu.com/vagrant/trusty/current/trusty-server-cloudimg-amd64-vagrant-disk1.box"
BOX_MEM = ENV['BOX_MEM'] || 2048

VAGRANTFILE_API_VERSION = "2"
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  # Setup virtual machine box. This VM configuration code is always executed.
  config.vm.box = BOX_NAME
  config.vm.box_url = BOX_URI
  config.vm.hostname = "sc-localvm"
  config.vm.network :private_network, ip: "192.168.200.201"

  config.vm.synced_folder ".", "/vagrant"

  # Forward keys from SSH agent rather than copy paste
  config.ssh.forward_agent = true

  # port forwarding, expose our service to host
  config.vm.network "forwarded_port", guest: 8079, host: 18079
  config.vm.network "forwarded_port", guest: 8080, host: 18080
  config.vm.network "forwarded_port", guest: 8081, host: 18081
  config.vm.network "forwarded_port", guest: 9000, host: 19000

  config.vm.provider "virtualbox" do |v|
    v.name = "sc-localvm"
    v.memory = BOX_MEM
    v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  end

  # system initial setup
  config.vm.provision "shell", inline: <<-EOF
    set -e

    # System packages
    locale-gen en_US.UTF-8 && echo 'LANG="en_US.UTF-8"' > /etc/default/locale
    echo "Installing Base Packages"
    export DEBIAN_FRONTEND=noninteractive
    apt-get update -qq
    apt-get remove -qqy puppet chef
    apt-get upgrade -y --force-yes
    apt-get install -qqy --force-yes tmux lsof vim htop iftop nload p7zip-full ack-grep httpie postgresql-client-9.3 python-software-properties

    # Install docker
    curl -s https://get.docker.io/ubuntu/ | sudo sh

    # setup swapfile
    # if not then create it
    if grep -q "swapfile" /etc/fstab; then
      echo 'swapfile found. No changes made.'
    else
      echo 'swapfile not found. Adding swapfile.'
      fallocate -l 2048M /swapfile
      chmod 600 /swapfile
      mkswap /swapfile
      swapon /swapfile
      echo '/swapfile none swap defaults 0 0' >> /etc/fstab
    fi

    # make silkcloud dirs
    mkdir -p /var/silkcloud; chown -R vagrant:vagrant /var/silkcloud
    mkdir -p /etc/silkcloud; chown -R vagrant:vagrant /etc/silkcloud

    # cleanup
    apt-get autoclean -y --force-yes
    apt-get autoremove -y
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

    echo "* PROVISIONING COMPLETED:"
    echo "** type 'vagrant ssh' to connect to vm"
    echo "** and you can do some customization on the box now"
  EOF

end
