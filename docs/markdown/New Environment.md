# New Environment Guide
The guide is to create a new environment for integration purpose.

## Generate Secure Information
Generate secure information and record them securely.
Note: using environment variable may have security issues. The commands are stored in bash_history, do remember to clean up.

1. Generate `crypto.core.key`
```
./scripts/AESCipher.py genkey
```
Name it CRYPTO_KEY

1. Generate database password
```
./scripts/keygen.sh
```
Name it DATABASE_PASSWORD

Get the MD5 of the database password.
```
md5 -s "${DATABASE_PASSWORD}silkcloud"
```
Name it DATABASE_PASSWORD_HASH

Get the encrypted database password
```
./scripts/AESCipher.py encrypt $CRYPTO_KEY $DATABASE_PASSWORD
```
Name it DATABASE_PASSWORD_ENCRYPTED

1. Generate master key
```
./scripts/keygen.sh
```
Name it MASTER_KEY

1. Generate JKS used to encrypt the master key
```
echo generate key for jks
./scripts/keygen.sh
```
Name it JKS_PASSWORD

```
echo generate key for cert key
./scripts/keygen.sh
```
Name it JKS_KEY_PASSWORD

Get the encrypted JKS passwords
```
./scripts/AESCipher.py encrypt $CRYPTO_KEY $JKS_PASSWORD
./scripts/AESCipher.py encrypt $CRYPTO_KEY $JKS_KEY_PASSWORD
```
Then generate the keystore
```
keytool -keystore encryptKeyStore.jks -genkey -keyalg RSA -alias silkcloud
```
Use the keys generated above as the JKS password and the key password.
For example:
  ```
  Enter keystore password: [Enter JKS password here]
  Re-enter new password:
  What is your first and last name?
    [Unknown]:  silkcloud
  What is the name of your organizational unit?
    [Unknown]:  silkcloud
  What is the name of your organization?
    [Unknown]:  silkcloud
  What is the name of your City or Locality?
    [Unknown]:  Shanghai
  What is the name of your State or Province?
    [Unknown]:  Shanghai
  What is the two-letter country code for this unit?
    [Unknown]:  CN
  Is CN=SilkCloud, OU=SilkCloud, O=SilkCloud, L=Shanghai, ST=Shanghai, C=CN correct?
    [no]:  yes

  Enter key password for <silkcloud>
          (RETURN if same as keystore password): [Enter cert key password here]
  Re-enter new password:
  ```
Copy the generated JKS file to a secure place.

1. Obtain the Cloudant URI, username and password
The system need 2 Cloudant URIs
  * The cloudant URI for UserPersonalInfo. These cloudant instances are not global. For example, data in US and EU are not replicated to each other.
  * The cloudant URI for other resources. These cloudant instances will replicate the data globally.
Each data center can be configured to use their own cloudant URIs.

When we get the Cloudant URI, organize them using the following format and encrypt them:
```
https://username:password@cloudant-host1;datacenter1,
https://username:password@cloudant-host2;datacenter2,
...
```
Name it CLOUDANT_URIS and CLOUDANT_PII_URIS

Then encrypt the whole string using:
```
./scripts/AESCipher.py encrypt $CRYPTO_KEY $CLOUDANT_URIS
./scripts/AESCipher.py encrypt $CRYPTO_KEY $CLOUDANT_PII_URIS
```

1. Obtain the Sabrix password and encrypt it
```
./scripts/AESCipher.py encrypt $CRYPTO_KEY $SABRIX_PASSWORD
```
Name it SABRIX_PASSWORD_ENCRYPTED

1. Generate the OAuth client secret for internal calls
```
./scripts/AESCipher.py genkey
```
Name the client secret INTERNAL_CLIENT_SECRET
Encrypt the client secret
```
./scripts/AESCipher.py encrypt $CRYPTO_KEY $INTERNAL_CLIENT_SECRET
```
Name it INTERNAL_CLIENT_SECRET_ENCRYPTED

## Change Configuration

1. Create new environments from existing environments. The configuration is defined in `common/configuration-data/src/main/resources/junbo/`
1. Customize the configurations
  * Change the JDBC URLs
  * Change the topology settings
  * Customize other settings
1. Change the following configurations for security
   1. Remove `crypto.core.key`
   1. Update the following properties

| Property                                         | Comment
|:-------------------------------------------------|:------------
| crypto.core.keyStore                             | Set to `file://encryptKeyStore.jks`
| crypto.core.keyAlias                             | Set to `silkcloud`
| crypto.core.keyStorePassword.encrypted           | Set to `JKS_PASSWORD`
| crypto.core.keyPassword.encrypted                | Set to `JKS_KEY_PASSWORD`
| crypto.userkey.cloudant.url.encrypted            | Set to `CLOUDANT_URIS`
| crypto.itemCryptoKey.cloudant.url.encrypted      | Set to `CLOUDANT_URIS`
| common.cloudant.url.encrypted                    | Set to `CLOUDANT_URIS`
| common.cloudantWithSearch.url.encrypted          | Set to `CLOUDANT_URIS`
| common.cloudant.dbNamePrefix                     | Set to `{env}_` if the cloudant is shared. Otherwise set to empty string.
| common.conf.debugMode                            | Set to `false`
| authorization.lib.service.clientId               | Set to `client`
| authorization.lib.service.clientSecret.encrypted | Set to `INTERNAL_CLIENT_SECRET`
| authorization.lib.allowTestAccessToken           | Set to `false`
| authorization.lib.service.disabled               | Set to `false`
| encrypt.user.personalinfo.cloudant.url.encrypted | Set to `CLOUDANT_PII_URIS`
| billing.db.username                              | Set to `silkcloud`
| billing.db.password.encrypted                    | Set to `DATABASE_PASSWORD_ENCRYPTED`
| crypto.db.username                               | Set to `silkcloud`
| crypto.db.password.encrypted                     | Set to `DATABASE_PASSWORD_ENCRYPTED`
| dualwrite.db.username                            | Set to `silkcloud`
| dualwrite.db.password.encrypted                  | Set to `DATABASE_PASSWORD_ENCRYPTED`
| ewallet.db.username                              | Set to `silkcloud`
| ewallet.db.password.encrypted                    | Set to `DATABASE_PASSWORD_ENCRYPTED`
| fulfilment.db.username                           | Set to `silkcloud`
| fulfilment.db.password.encrypted                 | Set to `DATABASE_PASSWORD_ENCRYPTED`
| index.db.username                                | Set to `silkcloud`
| index.db.password.encrypted                      | Set to `DATABASE_PASSWORD_ENCRYPTED`
| order.db.username                                | Set to `silkcloud`
| order.db.password.encrypted                      | Set to `DATABASE_PASSWORD_ENCRYPTED`
| payment.db.username                              | Set to `silkcloud`
| payment.db.password.encrypted                    | Set to `DATABASE_PASSWORD_ENCRYPTED`
| sharding.db.username                             | Set to `silkcloud`
| sharding.db.password.encrypted                   | Set to `DATABASE_PASSWORD_ENCRYPTED`
| subscription.db.username                         | Set to `silkcloud`
| subscription.db.password.encrypted               | Set to `DATABASE_PASSWORD_ENCRYPTED`
| token.db.username                                | Set to `silkcloud`
| token.db.password.encrypted                      | Set to `DATABASE_PASSWORD_ENCRYPTED`
| sabrix.password.encrypted                        | Set to `SABRIX_PASSWORD_ENCRYPTED`
| avalara.authorization.encrypted                  | Set to `AVALARA_AUTH_ENCRYPTED`

TODOs
```
drm.core.signature.keyStorePassword.encrypted=
drm.core.signature.keyPassword.encrypted=

order.risk.kount.keyFilePass.encrypted=
avalara.authorization.encrypted=
sabrix.password.encrypted=
entitlement.aws.secretAccessKey.encrypted=

payment.provider.braintree.privatekey.encrypted=
payment.provider.paypal.password.encrypted=
payment.provider.adyen.password.encrypted=
payment.provider.adyen.notifyPassword.encrypted=

payment.clientproxy.service.clientId=service
payment.clientproxy.service.clientSecret.encrypted=

notification.activemq.password.encrypted=

identity.conf.indexHashSalt=
identity.conf.teleSign.customerId=
identity.conf.teleSign.secretKey=

oauth.core.recaptcha.public.key=
oauth.core.recaptcha.private.key=

authorization.lib.service.clientSecret=
billing.clientproxy.service.clientSecret=
cart.clientproxy.service.clientSecret=
catalog.clientproxy.service.clientSecret=
crypto.clientproxy.service.clientSecret=
csr.clientproxy.service.clientSecret=
csr.login.clientSecret=
drm.clientproxy.service.clientSecret=
email.clientproxy.service.clientSecret=
entitlement.clientproxy.service.clientSecret=
fulfilment.clientproxy.service.clientSecret=
identity.clientproxy.service.clientSecret=
oauth.clientproxy.service.clientSecret=
order.clientproxy.service.clientSecret=
payment.clientproxy.service.clientSecret=
rating.clientproxy.service.clientSecret=
subscription.clientproxy.service.clientSecret=
token.clientproxy.service.clientSecret=
```

## Change DB Setup Configuration
Add a configuration folder at `liquibase/conf/{env}`
For copy from an existing environment like onebox and modify the configurations.
Set `login_password.encrypted` to `DATABASE_PASSWORD_ENCRYPTED`
```
login_password.encrypted = `DATABASE_PASSWORD_ENCRYPTED`
```

## Change Cloudant Setup Configuration
Add a configuration at `cloudant/conf/{env}.json`
Set the cloudant configurations in the file:
```
{
    "cloudant": "$CLOUDANT_URIS",
    "cloudant_search": "$CLOUDANT_URIS",
}
```
TODO:
  1. It's still clear text, will be fixed.
  1. Separate the cloudant PII URI

## Configure OAuth Clients
1. OAuth client for internal communication
Add a configuration at `apphost/data-loader/src/main/resources/data/{env}/client/service.data`. Copy from other environment, and change the client_secret to `INTERNAL_CLIENT_SECRET`.

TODO: This is clear text now. Will fix later.

1. Other OAuth clients
Onboard other OAuth clients as requested.

## Configure PGHA
Add a configuration at `scripts/pgha/{env}_{shard}.sh`
