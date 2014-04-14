# Environment Setup Guide

## Install Base Software
### Setup Environment for Windows
The following guide is for setting up windows as the development and testing environment.

#### Install JDK
1. Download and install [JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).
1. Add the path of java (for example, `C:\Program Files\Java\jdk1.7.0_51\bin`) to the system `PATH` and put it before `%SystemRoot%\system32`
1. To verify, type the following command in windows command line.

    ```
    where java
    ```

    It should show the `java.exe` from the JDK folder rather than `%SystemRoot%\system32`.

#### Install PostgreSQL
1. Download and install [PostgreSQL 9.3](http://www.enterprisedb.com/products-services-training/pgdownload). 
1. During the installation, please keep the superuser name as `postgres` if asked.
1. After installation, add the path of `psql.exe` (for example, `C:\Program Files\PostgreSQL\9.3\bin`) to the system `PATH`.
1. Open pgAdmin 3, try to connect localhost and check "Store Password".
1. Verify `psql.exe` won't ask for password by running the following command in your command line.

    ```
    psql.exe -lt postgres postgres
    ```

    It should show the list of databases without asking for password.

1. Change the `postgresql.conf` to increase the max connections and transactions for unit tests.
    - Open `data\postgresql.conf` from the PostgreSQL installation path (for example, `C:\Program Files\PostgreSQL\9.3\data\postgresql.conf`).
    - Find `max_prepared_transactions`, uncomment the line and change the value to 100. 

        ```
        max_prepared_transactions = 100
        ```

    - Find `max_connections`, uncomment if commented and change the value to 100.

        ```
        max_connections = 100
        ```

1. Restart PostgreSQL using the following commands (assume the installation path is `C:\Program Files\PostgreSQL\9.3`):
    1. Open an elevated command line window.

        ```
        powershell Start-Process cmd.exe -Verb RunAs
        ```

    1. Type the following command.

        ```
        net stop postgresql-9.3
        net start postgresql-9.3
        ```
        
#### Install CouchDB
1. Download and install [CouchDB 1.5.1 with Erlang R14B04](http://www.apache.org/dyn/closer.cgi?path=/couchdb/binary/win/1.5.1/setup-couchdb-1.5.1_R14B04.exe).
1. Verify CouchDB is running by accessing [http://localhost:5984/\_utils](http://localhost:5984/_utils). It should show the "Apache CouchDB - Futon" page.

#### Install Cygwin
1. Download and install [Cygwin](http://cygwin.com/)
1. When setup cygwin, select the following packages:
    - `curl`
    - `wget`
    - `git`
    - `openssh`

    If you forgot to install any package above, you can run `setup-x86.exe` or `setup-x86_64.exe` again to add them.
1. Set the eol mode for cygwin by running the following command in cygwin terminal

```
git config --global core.eol native
```

#### Install Python
1. Download and install [Python 2.7](https://www.python.org/download/).
1. Add the path of python (for example, `C:\Python27`) to system `PATH`.

#### Install Gradle
1. Download [Gradle 1.11](https://services.gradle.org/distributions/gradle-1.11-all.zip) and extract to `C:\gradle`.
1. Add `C:\gradle\bin` to system `PATH`.
1. Verify gradle is available in command by running the following command in your command line.

    ```
    gradle --version
    ```

    It should show the gradle version.

#### Install IntelliJ IDEA (Optional)
This step is optional. IntelliJ IDEA is the recommended IDE for developing Java and Groovy code. Download and install [IntelliJ IDEA 13 Community Edition](http://www.jetbrains.com/idea/download/).

### Setup Environment for OS X
The following guide is for setting up OS X as the development and testing environment.

## Build and Run

### Full build
In windows, do the following steps in cygwin terminal. For OS X and Linux, open the terminal and do the following steps.
1. Setup the ssh key according to the [github guide](https://help.github.com/articles/generating-ssh-keys).
1. After setup the ssh key, enter the following comands to clone the branch.

    ```
    cd ~
    mkdir owp-main
    git clone git@github.com:junbo/main.git owp-main
    ```

1. Run the build using the following commands.

    ```
    cd ~/owp-main
    ./fullcycle.sh
    ```

    The command should finish without error. The fullcycle.sh includes the unit tests.

### Partial build
If you want to build only one component (for example, identity), run the following commands:

```
cd ~/owp-main/identity
gradle clean build install
```

If you want to run the build without unittests, run the following commands:

```
cd ~/owp-main/identity
gradle clean build install -x test
```

### Run the servers
After the build, run the servers using the following command:

```
cd ~/owp-main/bootstrap
gradle installApp distTar
pushd catalog-bundle/build/install/catalog-bundle
./startup.sh
popd
pushd commerce-bundle/build/install/commerce-bundle
./startup.sh
popd
pushd identity-bundle/build/install/identity-bundle
./startup.sh
popd
pushd docs-bundle/build/install/docs-bundle
./startup.sh
popd

```

After the steps, the serices are available at different ports on the development machine.
- Docs Url: [http://localhost:8079](http://localhost:8079)
- Commerce Url: [http://localhost:8080](http://localhost:8080)
- Identity Url: [http://localhost:8081](http://localhost:8081)
- Catalog Url: [http://localhost:8082](http://localhost:8082)

Note: In integration and production environment, a hardware load balancer will be put in front of these endpoints to provide a single endpoint. 
(TODO: In onebox, use a Nginx server will be used to proxy the requests.)

### Run the integration test cases
After the servers are running, you can run test cases using the following command:

```
cd ~/owp-main/integrationtesting
gradle build
```

