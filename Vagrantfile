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
  cores = '1'
  if host =~ /mswin|mingw/
    # TODO: set cpu cores
  else
    cores = `awk "/^processor/ {++n} END {print n}" /proc/cpuinfo 2> /dev/null || sh -c 'sysctl hw.logicalcpu 2> /dev/null || echo ": 2"' | awk \'{print \$2}\' `.chomp
  end

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
  config.vm.network "forwarded_port", guest: 9401, host: 9401

  config.vm.provider "virtualbox" do |v|
    v.name = "sc-localdev"
    v.memory = 3072
    v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
    v.customize ["modifyvm", :id, "--cpus", cores ]

    # enable ioapic when more than 1 CPU
    if cores.to_i > 1
      v.customize ["modifyvm", :id, "--ioapic", "on"]
    end
  end

  # system initial setup
  config.vm.provision "shell", inline: <<-EOF
    set -e

    # setup swapfile
    # if not then create it
    if grep -q "swapfile" /etc/fstab; then
      echo 'swapfile found. No changes made.'
    else
      echo 'swapfile not found. Adding swapfile.'
      fallocate -l 3072M /swapfile
      chmod 600 /swapfile
      mkswap /swapfile
      swapon /swapfile
      echo '/swapfile none swap defaults 0 0' >> /etc/fstab
    fi

    # tune gradle
    echo 'export GRADLE_OPTS="$GRADLE_OPTS -Xmx2048m -XX:MaxPermSize=512m"' >> /home/vagrant/.bashrc

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

    # it is recommended to use autocrlf=input on linux, means file would be converted to LF
    # when file is comitted to object database, or when git is compareing working directory with object database
    # so even if on windows host, file is checked out as CRLF, it can also work in vagrant.
    su - vagrant -c 'git config --global core.autocrlf input'

    echo "* PROVISIONING COMPLETED:"
    echo "** type 'vagrant ssh' to connect to sc-localdev"
    echo "** and you can do some customization on the box now"
  EOF

end
