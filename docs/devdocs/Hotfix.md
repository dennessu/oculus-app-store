# Hotfix

This document covers how to make hotfixes to PROD.

In order to deploy the environment, you should login to proper bastion or utility servers in the environment.

SSH to the deployment server using `devops` account.

### Prepare the Change

#### Prepare the Commit
Prepare the fix in `release` branch. Make sure you run **supercycle.sh** before commit.

Check the bits used in current PROD. Check current production commit by running:

```
ansible-playbook -i prod.hosts check-version.yml --limit ue-app-blue:ue-cryptoapp-blue:ue-utl
```

Checkout the current version of production and make it hotfix branch.
```
git checkout -b hotfix <prod-commit>
git cherry-pick <your-fix>
git push origin --force HEAD:hotifx
```

**Note**: Release branch is very close to production version, so it's usually okay to cherry-pick. If it's not you may want to fix first then cherry-pick back to release. We are considering putting a long term hotfix branch for this purpose.

If all your changes are in `apphost/config-data/src/main/resources/junbo/conf` folder, the fix is configuration only. You can jump to **Configuration Only Hotfixes** section for a quick hotfix.

#### Prepare the Binary

Build the hotfix branch using:
```
git checkout hotfix
./build.sh -x build
```

Copy the zip file from `apphost/apphost-cli/build/distributions/` to your deployment folder. For example, `/home/devops`.

Go to the ansible folder of the deployment server and pull the hosts change.

Make sure newrelic jar and proper license file exists in `/home/devops/newrelic`.

### Deploy The Servers

For more information about the playbook, refer to [Silkcloud Playbook](https://github.com/junbo/commops/tree/master/ansible).


#### Deployment Order
Although it might be a super super urgent hotfix, we still strongly recommend deploying to staging first.
For a upgrade or hotfix to get deployed it should go from staging all the way to the preprod. The deployment order at this level is:
  * `staging.useast`
  * `staging.uswest`
  * `preprod useast` (prod.useast or prod.useast-blue, depending on which the prod sewer is pointing to)
  * `preprod uswest` (prod.useast or prod.useast-blue, depending on which the prod sewer is pointing to)
Then the sewer verifies the preprod is good and switch the URL to new environment.

If the deployment is an urgent hotfix, we also need to deploy the prod useast and prod uswest.
The deployment steps below is using `preprod.useast-blue` as an example.

#### Deployment Steps

###### Upgrade DBs
If there is change to PostgreSQL or Cloudant Views, run this step. **Otherwise SKIP this step.**

Prepare the CRYPTO_KEY and OAUTH_CRYPTO_KEY for the environment. They are found in `/etc/silkcloud/configuration.properties` in existing servers.

```
ansible-playbook -i prod.hosts upgrade-db.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT CRYPTO_KEY=..."
```

If there is any error in liquibase, fix the liquibase file to make sure the change is an upgrade.

###### Upgrade First Half App Servers
```
ansible-playbook -i prod.hosts upgrade-app.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT newrelicdir=/home/devops/newrelic" --limit ue-app-blue-1:ue-cryptoapp-blue-1
```
**Note**: We don't run reset-config in hotfix to avoid unnecessary breaks.

Run smoke test several times to make sure the half is good.

###### Upgrade Utility Server

```
ansible-playbook -i prod.hosts upgrade-app.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT newrelicdir=/home/devops/newrelic" --limit ue-utl
```

Make sure sniffer is working after this step by monitoring the app server's health check.

###### Upgrade Second Half App Servers

```
ansible-playbook -i prod.hosts upgrade-app.yml -e "sourcedir=/home/devops appname=apphost-cli-0.0.1-SNAPSHOT newrelicdir=/home/devops/newrelic" --limit ue-app-blue-1:ue-cryptoapp-blue-2
```

Run smoke test several times to make sure the half is good.

### Configuration Only Hotfixes
If the change is only on configurations, you can use the following steps. This avoids a full build and is easier to deploy.

Make sure you have your new configuration committed to **release** branch.

Then do the following to add the override in the configuration and restart the apphost. The example below overrides the `clientproxy.casey.url`.
```
ansible -i prod.hosts app-green-1 -m shell -a 'echo "clientproxy.casey.url=https://api-prod-green.oculus.com/v1" >> /etc/silkcloud/configuration.properties' --sudo
ansible -i prod.hosts app-green-1 -m shell -a 'restart silkcloud-apphost' --sudo
```

Then run smoke test to make sure the server is still working after hotfix. Then run the second half:
```
ansible -i prod.hosts app-green-2 -m shell -a 'echo "clientproxy.casey.url=https://api-prod-green.oculus.com/v1" >> /etc/silkcloud/configuration.properties' --sudo
ansible -i prod.hosts app-green-2 -m shell -a 'restart silkcloud-apphost' --sudo
```

**IMPORTANT**: Make sure your change is in **release** branch. This configuration override will be **RESET** by next full deployment.
