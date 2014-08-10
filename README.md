# Environment Setup Guide

**You don't have to install postgresql/couchdb/memcached in your desktop/laptop, check out the docker solution**

[Use docker to run dev environment](docs/devdocs/DockerAsDevEnv.md)

## Install Base Software
### Setup Environment for Windows
The following guide is for setting up windows as the development and testing environment.

#### Install JDK
1. Download and install [JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).
1. Add the path of java (for example, `C:\Program Files\Java\jdk1.7.0_51\bin`) to the system `PATH` and put it before `%SystemRoot%\system32`
1. To verify, type the following command in windows command line.

    ```bash
    where java
    ```

    It should show the `java.exe` from the JDK folder rather than `%SystemRoot%\system32`.

#### Install PostgreSQL
1. Download and install [PostgreSQL 9.3](http://www.enterprisedb.com/products-services-training/pgdownload).
1. During the installation, please keep the superuser name as `postgres` if asked.
1. After installation, add the path of `psql.exe` (for example, `C:\Program Files\PostgreSQL\9.3\bin`) to the system `PATH`.
1. Open pgAdmin3, try to connect localhost and check "Store Password".
1. Verify `psql.exe` won't ask for password by running the following command in your command line.

    ```bat
    psql.exe -lt postgres postgres
    ```

    It should show the list of databases without asking for password.

1. Change the `postgresql.conf` to increase the max connections and transactions for unit tests.
    - Open `data\postgresql.conf` from the PostgreSQL installation path (for example, `C:\Program Files\PostgreSQL\9.3\data\postgresql.conf`).
    - Find `max_connections`, uncomment if commented and change the value to 100.

        ```
        max_connections = 100
        ```

1. Restart PostgreSQL using the following commands (assume the installation path is `C:\Program Files\PostgreSQL\9.3`):
    1. Open an elevated command line window.

        ```bat
        powershell Start-Process cmd.exe -Verb RunAs
        ```

    1. Type the following commands if you installed postgresql x86.

        ```bat
        net stop postgresql-9.3
        net start postgresql-9.3
        ```

        Or the following commands if you installed postgresql x64.

        ```bat
        net stop postgresql-x64-9.3
        net start postgresql-x64-9.3
        ```

#### Install CouchDB
1. Download and install [CouchDB 1.5.1 with Erlang R14B04](http://www.apache.org/dyn/closer.cgi?path=/couchdb/binary/win/1.5.1/setup-couchdb-1.5.1_R14B04.exe).
1. Verify CouchDB is running by accessing [http://localhost:5984/\_utils](http://localhost:5984/_utils). It should show the "Apache CouchDB - Futon" page.

#### Install Memcached
1. Download and install [Memcached from Northscale](http://downloads.northscale.com/memcached-win32-1.4.4-14.zip).
1. Unzip the archive memcached-win32-1.4.4-14.zip
1. [Open command prompt with Administrator privilege](http://technet.microsoft.com/en-us/library/cc947813.aspx) in this location and run the following commands:
    ```
    memcached.exe -d install
    net start memcached
    sc config memcached start= auto
    ```
    Note there can NOT be a space before = and MUST be a space after =.

#### Install Node.js
1. Download and install [Node.js for Windows](http://nodejs.org/download/)
1. Open command prompt and make sure command `node` exists
  ```
  node --version
  ```

#### Install Cygwin
1. Download and install [Cygwin](http://cygwin.com/)
1. When setup cygwin, select the following packages:
    - `curl`
    - `wget`
    - `openssh`

    If you forgot to install any package above, you can run `setup-x86.exe` or `setup-x86_64.exe` again to add them.
    Note: Do *NOT* install the following packages:
    - ~~postgresql~~
    - ~~python~~
    - ~~git~~

1. Add `JAVA_HOME` environment varilable to `~/.bashrc`.

    ```bash
    # This is just an example, please replace the path with your actual JDK path.
    export JAVA_HOME=/cygdrive/c/Program\ Files/Java/jdk1.7.0_51
    ```

1. Verify `JAVA_HOME` using the following commands.

    ```bash
    source ~/.bashrc
    ls $JAVA_HOME
    ```

#### Install Git
Download and install the latest git from [msysGit](https://github.com/msysgit/msysgit/releases/download/Git-1.9.4-preview20140611/Git-1.9.4-preview20140611.exe)

#### Install Python
1. Download and install [Python 2.7](https://www.python.org/download/).
1. Add the path of python and the scripts folder (for example, `C:\Python27` and `C:\Python27\Scripts`) to system `PATH`.
1. Install PIP by running the following commands
    ```
    curl https://bootstrap.pypa.io/get-pip.py | python
    ```
    Note: if your python is installed in other folder, plase change the path in the second command.
1. Install [PyCharm Community Edition](http://www.jetbrains.com/pycharm/features/editions_comparison_matrix.html) (Optional)

#### Install Gradle
1. Download [Gradle 1.11](https://services.gradle.org/distributions/gradle-1.11-all.zip) and extract to `C:\gradle`.
1. Add `C:\gradle\bin` to system `PATH`.
1. Verify gradle is available in command by running the following command in your command line.

    ```bash
    gradle --version
    ```

    It should show the gradle version.

#### Install IntelliJ IDEA (Optional)
This step is optional. IntelliJ IDEA is the recommended IDE for developing Java and Groovy code. Download and install [IntelliJ IDEA 13 Community Edition](http://www.jetbrains.com/idea/download/).

### Setup Environment for OS X
The following guide is for setting up OS X as the development and testing environment.

#### Install [Home Brew](http://brew.sh)
1. Open Terminal and run the following commands:

    ```bash
    xcode-select --install
    ruby -e "$(curl -fsSL https://raw.github.com/Homebrew/homebrew/go/install)"
    ```

1. Prefer gnubin by adding the following lines to `~/.bash_profile`

    ```bash
    # Prefer gnubin
    export PATH="/usr/local/opt/coreutils/libexec/gnubin:$PATH"
    export MANPATH="/usr/local/opt/coreutils/libexec/gnuman:$MANPATH"
    ```

Note: If you don't want to change the coreutils preference, you can create another script and always run before building this branch. We are trying to use Python to reduce the dependency on bash.

#### Install Necessary Packages
1. Open Terminal and run the following command:

    ```bash
    brew install coreutils gradle wget openssl
    ```

1. Verify gradle is available in command by running the following command in your command line.

    ```bash
    gradle --version
    ```

    It should show the gradle version.

#### Install JDK
1. Download and install [JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).
1. Add `JAVA_HOME` to `~/.bash_profile`

    ```bash
    export JAVA_HOME=`/usr/libexec/java_home`
    ```

1. To verify, type the following commands in terminal.

    ```bash
    source ~/.bash_profile
    java -version
    ```

    It should show `java version "1.7.0_xx` where xx is the revision of the JVM you installed.

#### Install PostgreSQL
1. Download and install [PostgreSQL 9.3](http://www.enterprisedb.com/products-services-training/pgdownload).
1. During the installation, please keep the superuser name as `postgres` if asked.
1. Change the `postgresql.conf` to increase the max connections and transactions for unit tests.
    - Open `data\postgresql.conf` from the PostgreSQL installation path.

        ```bash
        sudo -u postgres vim /Library/PostgreSQL/9.3/data/postgresql.conf
        ```

    - Find `max_connections`, uncomment if commented and change the value to 100.

        ```
        max_connections = 100
        ```

1. Restart PostgreSQL using the following commands.

    ```bash
    sudo -u postgres /Library/PostgreSQL/9.3/bin/pg_ctl -D /Library/PostgreSQL/9.3/data -m fast restart
    ```

1. Add the psql environment to `~/.bash_profile`

    ```bash
    source /Library/PostgreSQL/9.3/pg_env.sh
    ```

1. Open pgAdmin3, try to connect localhost and check "Store Password" if asked.
1. Verify `psql` won't ask for password by running the following command in your command line.

    ```bash
    psql -lt postgres postgres
    ```

    It should show the list of databases without asking for password.

#### Install CouchDB

1. Open Terminal and run the following commands:

    ```bash
    brew install couchdb
    ```

    Refer to the [CouchDB Installation Guide](http://docs.couchdb.org/en/latest/install/mac.html) if there is any issue installing couchdb.

1. Configure CouchDB to run as a daemon and start CouchDB

    ```bash
    sudo launchctl load /usr/local/Library/LaunchDaemons/org.apache.couchdb.plist
    sudo launchctl start org.apache.couchdb
    ```

1. Verify CouchDB is running by accessing [http://localhost:5984/\_utils](http://localhost:5984/_utils). It should show the "Apache CouchDB - Futon" page.

#### Install Memcached

Open Terminal and run the following commands:

```bash
brew install memcached
ln -sfv /usr/local/opt/memcached/*.plist ~/Library/LaunchAgents
launchctl load ~/Library/LaunchAgents/homebrew.mxcl.memcached.plist
```

#### Install Node.js

Open Terminal and run the following commands:

```bash
brew install node.js
```

#### Install PIP for Managing Python Packages

Open Terminal and run the following commands:

```bash
curl https://bootstrap.pypa.io/get-pip.py | sudo python
```

#### Install IntelliJ IDEA (Optional)
IntelliJ IDEA is the recommended IDE for developing Java and Groovy code. Download and install [IntelliJ IDEA 13 Community Edition](http://www.jetbrains.com/idea/download/).

#### Install PyCharm (Optional)
Install [PyCharm Community Edition](http://www.jetbrains.com/pycharm/features/editions_comparison_matrix.html) (Optional)

#### Update bash_profile (Optional)
It is possible that when building you will encounter the following error:

```bash
java.lang.OutOfMemoryError: PermGen space
```

To fix this, you should add the following to your ~/.bash_profile:

```bash
export GRADLE_OPTS="$GRADLE_OPTS -Xmx2048m -Xms1024m -XX:PermSize=512m -XX:MaxPermSize=1024m"
```

## Build and Run

### Clone Source Code
In windows, do the following steps in cygwin terminal. For OS X and Linux, open the terminal and do the following steps. Remember to open a new terminal to make sure edits to `~/.bash_profile` took effect.

1. Setup the ssh key according to the [github guide](https://help.github.com/articles/generating-ssh-keys).
    If you meet the error "Could not open a connection to your authentication agent", try running the following command and try again.

    ```bash
    ssh-agent bash --login –i
    ```

1. After setup the ssh key, enter the following comands to clone the branch.

    ```bash
    cd ~
    mkdir owp-main
    git clone git@github.com:junbo/main.git owp-main
    ```

### Full Build

Run the full build using the following commands.

```bash
cd ~/owp-main
./fullcycle.sh
```

The command should finish without error. The fullcycle.sh includes the unit tests.

The above command equals to the following:
```bash
cd ~/owp-main
./setupdb.sh
pushd gradle/bootstrap
gradle
popd
gradle
```

### Partial Build
If you want to build only one component (for example, identity), run the following commands:

```bash
cd ~/owp-main/identity
gradle
```

If you want to run the build without code style checks and unittests, run the following commands:

```bash
cd ~/owp-main/identity
gradle -x build
```

If you want to run the build using locally cached jar packages:

```bash
cdo ~/owp-main/identity
gradle --offline
```

### Run the servers
After the build, run the servers using the following command:

```bash
cd ~/owp-main/apphost
gradle installApp distTar
pushd apphost-cli/build/install/apphost-cli
./startup.sh
popd
pushd docs-bundle/build/install/docs-bundle
./startup.sh
popd
```

After the steps, the serices are available at different ports on the development machine.
- Docs Url: [http://localhost:8079](http://localhost:8079)
- Api Url: [http://localhost:8080/v1](http://localhost:8080)

Note: In integration and production environment, a hardware load balancer will be put in front of these endpoints to provide a single endpoint.
(TODO: In onebox, a Nginx server will be used to proxy the requests.)

### Run the integration test cases
After the servers are running, you can run test cases using the following command:

```bash
cd ~/owp-main/integrationtesting
gradle build
```
