# -*- mode: ruby -*-
# vi: set ft=ruby :

BOX_NAME = ENV['BOX_NAME'] || "sc-localdev-base"
BOX_URI = ENV['BOX_URI'] || "http://arti.silkcloud.info/sc-localdev-base-20140711.box"

VAGRANTFILE_API_VERSION = "2"
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  # Setup virtual machine box. This VM configuration code is always executed.
  config.vm.box = BOX_NAME
  config.vm.box_url = BOX_URI

  config.vm.hostname = "sc-localdev"

  # Forward keys from SSH agent rather than copypasta
  config.ssh.forward_agent = true

  config.vm.network :private_network, ip: "192.168.200.101"

  # sync source folder
  config.vm.synced_folder ".", "/home/vagrant/src"

  # port forwarding
  config.vm.network "forwarded_port", guest: 8079, host: 8079
  config.vm.network "forwarded_port", guest: 8080, host: 8080
  config.vm.network "forwarded_port", guest: 8081, host: 8081
  config.vm.network "forwarded_port", guest: 5984, host: 5984
  config.vm.network "forwarded_port", guest: 5432, host: 5432

  config.vm.provider "virtualbox" do |v|
    v.name = "sc-localdev"
    v.memory = 2048
    v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  end

  # system initial setup
  config.vm.provision "shell", inline: <<-EOF
    set -e

    # tune gradle
    echo 'export GRADLE_OPTS="$GRADLE_OPTS -Xmx2048m -Xms1024m -XX:PermSize=512m -XX:MaxPermSize=1024m"' >> /home/vagrant/.bashrc

    # Auto cd to source code dir
    echo "cd /home/vagrant/src" >> /home/vagrant/.bashrc

    # configure git
    su - vagrant -c 'git config --global push.default simple'
    su - vagrant -c 'git config --global branch.autosetuprebase always'
    su - vagrant -c 'git config --global pull.rebase true'
    su - vagrant -c 'git config --global rerere.enabled true'
    su - vagrant -c 'git config --global color.ui true'
    su - vagrant -c 'git config --global core.pager \"LESS=FRXK less\"'
    su - vagrant -c 'git config --global core.editor \"vim -c startinsert\"'
    su - vagrant -c 'git config --global alias.tree \"log --graph --decorate --pretty=format:\\\"%C(auto)%h %C(blue)%ad%C(reset)%C(auto) - %s%d %C(yellow)[%an]\\\" --abbrev-commit --date=short\"'

    echo "* PROVISIONING COMPLETED:"
    echo "** type 'vagrant ssh' to connect to sc-localdev"
    echo "** and you can do some customization on the box now"
  EOF

end
