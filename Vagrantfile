# -*- mode: ruby -*-
# vi: set ft=ruby :

BOX_NAME = ENV['BOX_NAME'] || "sc-localdev-base"
BOX_URI = ENV['BOX_URI'] || "http://arti.silkcloud.info/sc-localdev-base-20140713.box"

VAGRANTFILE_API_VERSION = "2"
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  # Setup virtual machine box. This VM configuration code is always executed.
  config.vm.box = BOX_NAME
  config.vm.box_url = BOX_URI

  config.vm.hostname = "sc-localdev"

  config.vm.network :private_network, ip: "192.168.200.101"

  host = RbConfig::CONFIG['host_os']

  # sync source folder
  if host =~ /mswin|mingw/
    config.vm.synced_folder ".", "/home/vagrant/src", type: "smb"
  else
    config.vm.synced_folder ".", "/home/vagrant/src", type: "nfs"
  end

  # port forwarding
  config.vm.network "forwarded_port", guest: 8079, host: 8079
  config.vm.network "forwarded_port", guest: 8080, host: 8080
  config.vm.network "forwarded_port", guest: 8081, host: 8081
  config.vm.network "forwarded_port", guest: 5984, host: 5984
  config.vm.network "forwarded_port", guest: 5432, host: 5432

  config.vm.provider "virtualbox" do |v|
    v.name = "sc-localdev"
    v.memory = 3072
    v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
    if host =~ /mswin|mingw/
    else
      v.customize ["modifyvm", :id, "--cpus",
        `awk "/^processor/ {++n} END {print n}" /proc/cpuinfo 2> /dev/null || sh -c 'sysctl hw.logicalcpu 2> /dev/null || echo ": 2"' | awk \'{print \$2}\' `.chomp ]
    end
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
    su - vagrant -c 'git config --global credential.helper cache'
    su - vagrant -c 'git config --global credential.helper \"cache --timeout=604800\"'

    echo "* PROVISIONING COMPLETED:"
    echo "** type 'vagrant ssh' to connect to sc-localdev"
    echo "** and you can do some customization on the box now"
  EOF

  if host =~ /mswin|mingw/
    config.vm.provision "shell",
        inline: "su - vagrant -c 'git config --global core.autocrlf true'"
  end

end
