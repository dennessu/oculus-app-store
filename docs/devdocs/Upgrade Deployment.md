# Upgrade Deployment Guide

This document covers how the upgrade deployment is done. In a upgrade deployment we don't assume new servers added in the environment.

In order to deploy the environment, you should login to proper bastion or utility servers in the environment.

SSH to the deployment server using `devops` account.

### Prepare the Bits
Make sure you got the main branch at the expected commit which you want to upgrade.

Build the main branch using:

```
./build.sh -x build
```

Copy the zip file from `apphost/apphost-cli/build/distributions/` to your deployment folder. For example, `/home/devops`.

Go to the ansible folder of the deployment server and pull the hosts change.

Make sure newrelic jar and proper license file exists in `/home/devops/newrelic`.

### Deploy The Servers

For more information about the playbook, refer to [Silkcloud Playbook](https://github.com/junbo/commops/tree/master/ansible).


#### Deployment Order
For a upgrade or hotfix to get deployed it should go from staging all the way to the preprod. The deployment order at this level is:
  * staging.useast
  * staging.uswest
  * preprod useast (prod.useast or prod.useast-blue, depending on which the prod sewer is pointing to)
  * preprod uswest (prod.useast or prod.useast-blue, depending on which the prod sewer is pointing to)
Then the sewer verifies the preprod is good and switch the URL to new environment.

If the deployment is an urgent hotfix, we also need to deploy the prod useast and prod uswest.

Within each environment, the servers are deployed in a way that the live traffic is not impacted. The deployment happens in the following order:
  * Upgrade cloudant DBs. The DB changes should be backward compatible. This step create new cloudant views or new cloudant DBs. We need manual step to create DBs and setup replication 1 day before the deployment. See the detailed steps below.
  * Upgrade Postgre SQL DBs. The DB changes should be backward compatible.
  * Upgrade Half App Servers. This step will upgrade half of the app servers. Another half of the servers will still be online to handle the live traffic. In Production, although there is green/blue, some hotfix goes to the one taking live traffic directly, so it's still necessary to follow the half-half rule. For staging, verify the first half several times using smoketests.
  * Upgrade Utility Server.
  * Upgrade Another Half App Servers.

The deployment steps below is using `preprod.useast-blue` as an example.

#### Deployment Steps

###### Setup new Cloudant DBs
This step is necessary when there is new DB in the cloudant. If there is no new DB in this upgrade, skip this step. Cloudant replication is still managed by cloudant team. When there is a new cloudant DB included in the upgrade, we need to do this 1 day before the binary upgrade.

1. Create DBs using script
For PROD:
```
cd couchdb
python ./couchdbcmd.py createdbs prod --prefix= --yes --key=$CRYPTO_KEY
```
For Staging:
```
cd couchdb
python ./couchdbcmd.py createdbs staging --prefix=stg_ --yes --key=$CRYPTO_KEY
```

**Notes**:
1. This step will create missing database, update views and assign API Key permissions for all regions. Please **DO** make sure the local git you are using is **at the expected commit** of what you plan to use for upgrade.
1. All staging DBs have prefix `stg_`. Prod DBs don't have the prefix. For example, `user` is `stg_user` in staging and `user` in prod. The prefix value is defined in [`prod/cloudant.properties`](https://github.com/junbo/main/blob/master/apphost/config-data/src/main/resources/junbo/conf/prod/cloudant.properties) and [`staging/cloudant.properties`](https://github.com/junbo/main/blob/master/apphost/config-data/src/main/resources/junbo/conf/staging/cloudant.properties).

1. Send an email to [Cloudant Support](mailto:support@cloudant.com) to build multi-home replication for all new DBs.
1. Verify the DB setup using curl. Look at the DB doc count for the top DBs.

  ```
  curl -X POST -d '{"_id": "1", "test": "test"}' "https://$api_key:$api_secret@oculus001.cloudant.com/{new_db}" -H "X-Cloudant-User: oculus-cni"
  curl "https://$api_key:$api_secret@oculus003.cloudant.com/{new_db}/_all_docs?limit=1" -H "X-Cloudant-User: oculus-cni"
  ```

  Make sure the `total_rows` from both `oculus003` is 1 and the test doc is replicated. Then delete the test doc:

  ```
  curl -X DELETE "https://$api_key:$api_secret@oculus003.cloudant.com/{new_db}/1?rev={rev}" -H "X-Cloudant-User: oculus-cni"
  curl "https://$api_key:$api_secret@oculus001.cloudant.com/{new_db}/_all_docs?limit=1" -H "X-Cloudant-User: oculus-cni"
  ```

  The `rev` is the `_rev` returned in the first account. Make sure the DELETE is replicated to `oculus001` and the second curl command returned zero rows.

###### Upgrade DBs
This step should run per region. It includes Cloudant DB view upgrade.

Prepare the CRYPTO_KEY and OAUTH_CRYPTO_KEY for the environment. They are found in `/etc/silkcloud/configuration.properties` in existing servers.

```
ansible-playbook -i prod.hosts upgrade-db.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT CRYPTO_KEY=..."
```

If there is any error in liquibase, fix the liquibase file to make sure the change is an upgrade.

###### Reset Config and Upgrade First Half App Servers
The reset-config step is to make sure the configuration is clean and committed to the repository. Check the hotfix guideline below for details on why this is necessary and when it may break.

```
ansible-playbook -i prod.hosts reset-config.yml -e "CRYPTO_KEY=... OAUTH_CRYPTO_KEY=..." --limit ue-app-blue-1:ue-cryptoapp-blue-1
ansible-playbook -i prod.hosts upgrade-app.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT newrelicdir=/home/devops/newrelic" --limit ue-app-blue-1:ue-cryptoapp-blue-1
```

Run smoke test several times to make sure the half is good.


###### Reset Config and Upgrade Utility Server

```
ansible-playbook -i prod.hosts reset-config.yml -e "CRYPTO_KEY=... OAUTH_CRYPTO_KEY=..." --limit ue-utl
ansible-playbook -i prod.hosts upgrade-app.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT newrelicdir=/home/devops/newrelic" --limit ue-utl
```

Make sure sniffer is working after this step by monitoring the app server's health check.

###### Reset Config and Upgrade Second Half App Servers

```
ansible-playbook -i prod.hosts reset-config.yml -e "CRYPTO_KEY=... OAUTH_CRYPTO_KEY=..." --limit ue-app-blue-1:ue-cryptoapp-blue-2
ansible-playbook -i prod.hosts upgrade-app.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT newrelicdir=/home/devops/newrelic" --limit ue-app-blue-1:ue-cryptoapp-blue-2
```

Run smoke test several times to make sure the half is good.

###### Check Versions
```
ansible-playbook -i prod.hosts check-version.yml --limit ue-app-blue:ue-cryptoapp-blue:ue-utl
```

Make sure all app servers are using binaries with the same version.
