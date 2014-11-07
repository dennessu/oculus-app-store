#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"
set -e

: ${ENV:?'ENV must be defined.'}
: ${CLOUDANT_MH_URI:?'CLOUDANT_MH_URI must be defined.'}
: ${CLOUDANT_USWEST_URI:?'CLOUDANT_USWEST_URI must be defined.'}
: ${CLOUDANT_USEAST_URI:?'CLOUDANT_USEAST_URI must be defined.'}
: ${CLOUDANT_MH_URI_WITH_API_KEY:?'CLOUDANT_MH_URI_WITH_API_KEY must be defined.'}
: ${CLOUDANT_USWEST_URI_WITH_API_KEY:?'CLOUDANT_USWEST_URI_WITH_API_KEY must be defined.'}
: ${CLOUDANT_USEAST_URI_WITH_API_KEY:?'CLOUDANT_USEAST_URI_WITH_API_KEY must be defined.'}
: ${DB_MASTER_USEAST:?'DB_MASTER_USEAST must be defined.'}
: ${DB_MASTER_USWEST:?'DB_MASTER_USWEST must be defined.'}
: ${DB_SLAVE_USEAST:?'DB_SLAVE_USEAST must be defined.'}
: ${DB_SLAVE_USWEST:?'DB_SLAVE_USWEST must be defined.'}
: ${DB_REPLICA_USEAST:?'DB_REPLICA_USEAST must be defined.'}
: ${DB_REPLICA_USWEST:?'DB_REPLICA_USWEST must be defined.'}
: ${DB_BCP_USEAST:?'DB_BCP_USEAST must be defined.'}
: ${DB_BCP_USWEST:?'DB_BCP_USWEST must be defined.'}
: ${CRYPTO_DB_MASTER_USEAST:?'CRYPTO_DB_MASTER_USEAST must be defined.'}
: ${CRYPTO_DB_MASTER_USWEST:?'CRYPTO_DB_MASTER_USWEST must be defined.'}
: ${CRYPTO_DB_SLAVE_USEAST:?'CRYPTO_DB_SLAVE_USEAST must be defined.'}
: ${CRYPTO_DB_SLAVE_USWEST:?'CRYPTO_DB_SLAVE_USWEST must be defined.'}

: ${NEWRELIC_KEY:?'NEWRELIC_KEY must be defined.'}
: ${KOUNT_KEY:?'KOUNT_KEY must be defined.'}
: ${TELESIGN_KEY:?'TELESIGN_KEY must be defined.'}
: ${RECAPTCHA_PRIVATE_KEY:?'RECAPTCHA_PRIVATE_KEY must be defined.'}
: ${MANDRILL_KEY:?'MANDRILL_KEY must be defined.'}
: ${ENTITLEMENT_AWS_SECRET_ACCESS_KEY:?'ENTITLEMENT_AWS_SECRET_ACCESS_KEY must be defined.'}

: ${BRAINTREE_PRIVATEKEY:?'BRAINTREE_PRIVATEKEY must be defined.'}
: ${BRAINTREE_CSE:?'BRAINTREE_CSE must be defined.'}
: ${PAYPAL_PASSWORD:?'PAYPAL_PASSWORD must be defined.'}
: ${PAYPAL_SIGNATURE:?'PAYPAL_SIGNATURE must be defined.'}
: ${ADYEN_SKINSECRET:?'ADYEN_SKINSECRET must be defined.'}
: ${ADYEN_PASSWORD:?'ADYEN_PASSWORD must be defined.'}
: ${ADYEN_NOTIFYPASSWORD:?'ADYEN_NOTIFYPASSWORD must be defined.'}

: ${AVALARA_AUTH:?'AVALARA_AUTH must be defined.'}
: ${SABRIX_URL:?'SABRIX_URL must be defined.'}
: ${SABRIX_PASSWORD:?'SABRIX_PASSWORD must be defined.'}

function genkey {
    python $DIR/AESCipher.py genkey
}

function genpwd {
    $DIR/keygen.sh
}

function genclientid {
    $DIR/skeygen.sh
}

function encrypt {
    python $DIR/AESCipher.py encrypt $CRYPTO_KEY $1
}

function encryptclient {
    python $DIR/AESCipher.py encrypt $OAUTH_CRYPTO_KEY $1
}

export ONEBOX_CRYPTO_KEY=D58BA755FF96B35A6DABA7298F7A8CE2
export CRYPTO_KEY=`genkey`
export OAUTH_CRYPTO_KEY=`genkey`
export DATABASE_PASSWORD=`genpwd`
export DATABASE_PASSWORD_HASH=`md5 -s "${DATABASE_PASSWORD}silkcloud"|sed 's/\(.*\)= \(.*\)/\2/g'`
export JKS_PASSWORD=`genpwd`
export JKS_CERT_ALIAS=silkcloud
export JKS_CERT_PASSWORD=`genpwd`
export CLOUDANT_URIS="$CLOUDANT_MH_URI;us-west,$CLOUDANT_MH_URI;us-east"
export CLOUDANT_PII_URIS="$CLOUDANT_USWEST_URI;us-west,$CLOUDANT_USEAST_URI;us-east"
export CLOUDANT_URIS_WITH_API_KEY="$CLOUDANT_MH_URI_WITH_API_KEY;us-west,$CLOUDANT_MH_URI_WITH_API_KEY;us-east"
export CLOUDANT_PII_URIS_WITH_API_KEY="$CLOUDANT_USWEST_URI_WITH_API_KEY;us-west,$CLOUDANT_USEAST_URI_WITH_API_KEY;us-east"

export ACTIVEMQ_PASSWORD=`genkey`

# client secrets
export SERVICE_CLIENT_ID=`genclientid`
export SERVICE_CLIENT_SECRET=`genkey`
export SMOKETEST_CLIENT_ID=`genclientid`
export SMOKETEST_CLIENT_SECRET=`genkey`

export ADMIN_TOOL_CLIENT_SECRET=`genkey`
export CMS_ADMIN_PORTAL_CLIENT_SECRET=`genkey`
export CSR_CLIENT_SECRET=`genkey`
export CSR_TOOL_CLIENT_SECRET=`genkey`
export CURATION_TOOL_CLIENT_SECRET=`genkey`
export DEVELOPER_CENTER_CLIENT_SECRET=`genkey`
export DISCOURSE_FORUM_CLIENT_SECRET=`genkey`
export LEGACY_DEVELOPER_CENTER_CLIENT_CLIENT_SECRET=`genkey`
export LEGACY_DEVELOPER_CENTER_SERVER_CLIENT_SECRET=`genkey`
export MIGRATION_CLIENT_SECRET=`genkey`
export SEWER_CLIENT_SECRET=`genkey`
export SHOP_CLIENT_SECRET=`genkey`
export STORE_CLIENT_SECRET=`genkey`
export ID_CLIENT_SECRET=`genkey`
export SHARE_CLIENT_SECRET=`genkey`
export TECHNODROME_CLIENT_SECRET=`genkey`
export IDENTITY_INDEX_HASH_SALT=`./skeygen.sh 20`

mkdir -p build/$ENV
cd build/$ENV
# gen jks file
keytool -keystore encryptKeyStore.jks -genkey -keyalg RSA -alias $JKS_CERT_ALIAS -noprompt \
 -dname "CN=silkcloud, OU=silkcloud, O=silkcloud, L=Shanghai, S=Shanghai, C=CN" \
 -storepass $JKS_PASSWORD \
 -keypass $JKS_CERT_PASSWORD

cat <<EOF > ./${ENV}_info.txt
export ONEBOX_CRYPTO_KEY=$ONEBOX_CRYPTO_KEY
export CRYPTO_KEY=$CRYPTO_KEY
export OAUTH_CRYPTO_KEY=$OAUTH_CRYPTO_KEY
export DATABASE_PASSWORD='$DATABASE_PASSWORD'
export DATABASE_PASSWORD_HASH=$DATABASE_PASSWORD_HASH
export JKS_PASSWORD='$JKS_PASSWORD'
export JKS_CERT_ALIAS=$JKS_CERT_ALIAS
export JKS_CERT_PASSWORD='$JKS_CERT_PASSWORD'
export CLOUDANT_URIS='$CLOUDANT_URIS'
export CLOUDANT_PII_URIS='$CLOUDANT_PII_URIS'
export CLOUDANT_URIS_WITH_API_KEY='$CLOUDANT_URIS_WITH_API_KEY'
export CLOUDANT_PII_URIS_WITH_API_KEY='$CLOUDANT_PII_URIS_WITH_API_KEY'

export CLOUDANT_MH_URI='$CLOUDANT_MH_URI'
export CLOUDANT_USWEST_URI='$CLOUDANT_USWEST_URI'
export CLOUDANT_USEAST_URI='$CLOUDANT_USEAST_URI'
export CLOUDANT_MH_URI_WITH_API_KEY='$CLOUDANT_MH_URI_WITH_API_KEY'
export CLOUDANT_USWEST_URI_WITH_API_KEY='$CLOUDANT_USWEST_URI_WITH_API_KEY'
export CLOUDANT_USEAST_URI_WITH_API_KEY='$CLOUDANT_USEAST_URI_WITH_API_KEY'

export ACTIVEMQ_PASSWORD='$ACTIVEMQ_PASSWORD'

# client secrets
export SERVICE_CLIENT_ID=$SERVICE_CLIENT_ID
export SERVICE_CLIENT_SECRET='$SERVICE_CLIENT_SECRET'
export SMOKETEST_CLIENT_ID=$SMOKETEST_CLIENT_ID
export SMOKETEST_CLIENT_SECRET='$SMOKETEST_CLIENT_SECRET'

export ADMIN_TOOL_CLIENT_SECRET=$ADMIN_TOOL_CLIENT_SECRET
export CMS_ADMIN_PORTAL_CLIENT_SECRET=$CMS_ADMIN_PORTAL_CLIENT_SECRET
export CSR_CLIENT_SECRET=$CSR_CLIENT_SECRET
export CSR_TOOL_CLIENT_SECRET=$CSR_TOOL_CLIENT_SECRET
export CURATION_TOOL_CLIENT_SECRET=$CURATION_TOOL_CLIENT_SECRET
export DEVELOPER_CENTER_CLIENT_SECRET=$DEVELOPER_CENTER_CLIENT_SECRET
export DISCOURSE_FORUM_CLIENT_SECRET=$DISCOURSE_FORUM_CLIENT_SECRET
export LEGACY_DEVELOPER_CENTER_CLIENT_CLIENT_SECRET=$LEGACY_DEVELOPER_CENTER_CLIENT_CLIENT_SECRET
export LEGACY_DEVELOPER_CENTER_SERVER_CLIENT_SECRET=$LEGACY_DEVELOPER_CENTER_SERVER_CLIENT_SECRET
export MIGRATION_CLIENT_SECRET=$MIGRATION_CLIENT_SECRET
export SEWER_CLIENT_SECRET=$SEWER_CLIENT_SECRET
export SHOP_CLIENT_SECRET=$SHOP_CLIENT_SECRET
export STORE_CLIENT_SECRET=$STORE_CLIENT_SECRET
export ID_CLIENT_SECRET=$ID_CLIENT_SECRET
export SHARE_CLIENT_SECRET=$SHARE_CLIENT_SECRET
export TECHNODROME_CLIENT_SECRET=$TECHNODROME_CLIENT_SECRET

# identity
export IDENTITY_INDEX_HASH_SALT='$IDENTITY_INDEX_HASH_SALT'

# external
export NEWRELIC_KEY='$NEWRELIC_KEY'
export KOUNT_KEY='$KOUNT_KEY'
export TELESIGN_KEY='$TELESIGN_KEY'
export RECAPTCHA_PRIVATE_KEY='$RECAPTCHA_PRIVATE_KEY'
export MANDRILL_KEY='$MANDRILL_KEY'
export ENTITLEMENT_AWS_SECRET_ACCESS_KEY='$ENTITLEMENT_AWS_SECRET_ACCESS_KEY'

# payment
export BRAINTREE_PRIVATEKEY='$BRAINTREE_PRIVATEKEY'
export BRAINTREE_CSE='$BRAINTREE_CSE'
export PAYPAL_PASSWORD='$PAYPAL_PASSWORD'
export PAYPAL_SIGNATURE='$PAYPAL_SIGNATURE'
export ADYEN_SKINSECRET='$ADYEN_SKINSECRET'
export ADYEN_PASSWORD='$ADYEN_PASSWORD'
export ADYEN_NOTIFYPASSWORD='$ADYEN_NOTIFYPASSWORD'

# tax
export AVALARA_AUTH='$AVALARA_AUTH'
export SABRIX_URL='$SABRIX_URL'
export SABRIX_PASSWORD='$SABRIX_PASSWORD'

EOF

cat <<EOF > ./automation-exports.txt
export CRYPTO_KEY='$CRYPTO_KEY'
export OAUTH_CRYPTO_KEY='$OAUTH_CRYPTO_KEY'
export DATABASE_PASSWORD_HASH='$DATABASE_PASSWORD_HASH'
EOF

# config-data
mkdir -p $ENV
mkdir -p ${ENV}.useast
mkdir -p ${ENV}.uswest

cat <<EOF > $ENV/client.properties
# csr token client
csr.login.clientId=csr
csr.login.clientSecret.encrypted=`encrypt $CSR_CLIENT_SECRET`
csr.login.token.scope=offline csr

# store
store.oauth.clientId=2dstore
store.oauth.clientSecret.encrypted=`encrypt $STORE_CLIENT_SECRET`

# notification
notification.activemq.password.encrypted=`encrypt $ACTIVEMQ_PASSWORD`

EOF

cat <<EOF > $ENV/clientproxy.properties
# client proxy urls are defined in per datacenter configurations

# internal call clients
authorization.lib.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
billing.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
cart.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
catalog.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
crypto.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
csr.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
drm.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
email.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
entitlement.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
fulfilment.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
identity.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
oauth.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
order.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
payment.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
rating.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
subscription.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
token.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`
store.clientproxy.service.clientId.encrypted=`encrypt $SERVICE_CLIENT_ID`


# internal call client secrets
authorization.lib.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
billing.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
cart.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
catalog.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
crypto.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
csr.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
drm.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
email.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
entitlement.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
fulfilment.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
identity.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
oauth.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
order.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
payment.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
rating.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
subscription.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
token.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`
store.clientproxy.service.clientSecret.encrypted=`encrypt $SERVICE_CLIENT_SECRET`

EOF

cat <<EOF > $ENV/cloudant.properties
# cloudant URIs
common.cloudant.dbNamePrefix=
common.cloudant.url.encrypted=`encrypt $CLOUDANT_URIS_WITH_API_KEY`
common.cloudantWithSearch.url.encrypted=`encrypt $CLOUDANT_URIS_WITH_API_KEY`

crypto.userkey.cloudant.url.encrypted=`encrypt $CLOUDANT_URIS_WITH_API_KEY`
crypto.itemCryptoKey.cloudant.url.encrypted=`encrypt $CLOUDANT_URIS_WITH_API_KEY`

# configuration
common.cloudant.cache.enabled=true
common.cloudant.cache.includeDocs=false
common.cloudant.cache.storeviewresults=false
common.cloudant.cache.maxentitysize=10000
common.cloudant.cache.expiration=3600
EOF

cat <<EOF > $ENV/common.properties
apphost.fastboot=false
apphost.gracePeriod=40

# authorization
authorization.lib.allowTestAccessToken=false
authorization.lib.service.disabled=false

# common
common.conf.debugMode=false
common.routing.crossDcRoutingEnabled=true
common.routing.inDcRoutingEnabled=true
common.routing.forceRoute=false
common.routing.showRoutingPath=false

common.accesscontrol.allowOrigin=*
common.accesscontrol.allowHeader=Authorization, Origin, X-Requested-With, Content-Type, X-Email-Notification
common.accesscontrol.exposeHeaders=Location, Content-Disposition
common.accesscontrol.allowMethods=POST, PUT, GET, DELETE, HEAD, OPTIONS

# memcached
common.memcached.enabled=true
common.memcached.timeout=100
# per data center, configured in sub environments
# common.memcached.servers=127.0.0.1:11211
common.memcached.compressionThreshold=

# keep username empty to disable auth
common.memcached.auth=CRAM-MD5
common.memcached.username=
common.memcached.password=
EOF

cat <<EOF > $ENV/crypto.properties
# crypto cert
crypto.core.keyStore=file://encryptKeyStore.jks
crypto.core.keyStorePassword.encrypted=`encrypt $JKS_PASSWORD`
crypto.core.keyAlias=$JKS_CERT_ALIAS
crypto.core.keyPassword.encrypted=`encrypt $JKS_CERT_PASSWORD`
crypto.core.userKeyLength=16

# identity
identity.conf.indexHashSalt.encrypted=`encrypt $IDENTITY_INDEX_HASH_SALT`
identity.conf.indexHashAlgorithm=SHA-256

EOF

cat <<EOF > $ENV/db.properties
# DB connections
billing.db.jdbcUrls=\
  jdbc:postgresql://$DB_MASTER_USEAST:6543|$DB_SLAVE_USEAST:6543/billing;public;0..0;us-east,\
  jdbc:postgresql://$DB_MASTER_USWEST:6543|$DB_SLAVE_USWEST:6543/billing;public;0..0;us-west
billing.db.username=silkcloud
billing.db.password.encrypted=`encrypt $DATABASE_PASSWORD`

crypto.db.jdbcUrls=\
  jdbc:postgresql://$CRYPTO_DB_MASTER_USEAST:6543|$CRYPTO_DB_SLAVE_USEAST:6543/crypto;public;0..255;us-east,\
  jdbc:postgresql://$CRYPTO_DB_MASTER_USWEST:6543|$CRYPTO_DB_SLAVE_USEAST:6543/crypto;public;0..255;us-west
crypto.db.username=silkcloud
crypto.db.password.encrypted=`encrypt $DATABASE_PASSWORD`

dualwrite.db.jdbcUrls=\
  jdbc:postgresql://$DB_MASTER_USEAST:6543|$DB_SLAVE_USEAST:6543/dualwrite;public;0..255;us-east,\
  jdbc:postgresql://$DB_MASTER_USWEST:6543|$DB_SLAVE_USWEST:6543/dualwrite;public;0..255;us-west
dualwrite.db.username=silkcloud
dualwrite.db.password.encrypted=`encrypt $DATABASE_PASSWORD`

ewallet.db.jdbcUrls=\
  jdbc:postgresql://$DB_MASTER_USEAST:6543|$DB_SLAVE_USEAST:6543/ewallet;public;0..255;us-east,\
  jdbc:postgresql://$DB_MASTER_USWEST:6543|$DB_SLAVE_USWEST:6543/ewallet;public;0..255;us-west
ewallet.db.username=silkcloud
ewallet.db.password.encrypted=`encrypt $DATABASE_PASSWORD`

fulfilment.db.jdbcUrls=\
  jdbc:postgresql://$DB_MASTER_USEAST:6543|$DB_SLAVE_USEAST:6543/fulfilment;public;0..255;us-east,\
  jdbc:postgresql://$DB_MASTER_USWEST:6543|$DB_SLAVE_USWEST:6543/fulfilment;public;0..255;us-west
fulfilment.db.username=silkcloud
fulfilment.db.password.encrypted=`encrypt $DATABASE_PASSWORD`

index.db.jdbcUrls=\
  jdbc:postgresql://$DB_MASTER_USEAST:6543|$DB_SLAVE_USEAST:6543/index;public;0..255;us-east,\
  jdbc:postgresql://$DB_MASTER_USWEST:6543|$DB_SLAVE_USWEST:6543/index;public;0..255;us-west
index.db.username=silkcloud
index.db.password.encrypted=`encrypt $DATABASE_PASSWORD`

order.db.jdbcUrls=\
  jdbc:postgresql://$DB_MASTER_USEAST:6543|$DB_SLAVE_USEAST:6543/order;public;0..0;us-east,\
  jdbc:postgresql://$DB_MASTER_USWEST:6543|$DB_SLAVE_USWEST:6543/order;public;0..0;us-west
order.db.username=silkcloud
order.db.password.encrypted=`encrypt $DATABASE_PASSWORD`

payment.db.jdbcUrls=\
  jdbc:postgresql://$DB_MASTER_USEAST:6543|$DB_SLAVE_USEAST:6543/payment;public;0..0;us-east,\
  jdbc:postgresql://$DB_MASTER_USWEST:6543|$DB_SLAVE_USWEST:6543/payment;public;0..0;us-west
payment.db.username=silkcloud
payment.db.password.encrypted=`encrypt $DATABASE_PASSWORD`

sharding.db.jdbcUrls=\
  jdbc:postgresql://$DB_MASTER_USEAST:6543|$DB_SLAVE_USEAST:6543/sharding;public;0..255;us-east,\
  jdbc:postgresql://$DB_MASTER_USWEST:6543|$DB_SLAVE_USWEST:6543/sharding;public;0..255;us-west
sharding.db.username=silkcloud
sharding.db.password.encrypted=`encrypt $DATABASE_PASSWORD`

subscription.db.jdbcUrls=\
  jdbc:postgresql://$DB_MASTER_USEAST:6543|$DB_SLAVE_USEAST:6543/subscription;public;0..0;us-east,\
  jdbc:postgresql://$DB_MASTER_USWEST:6543|$DB_SLAVE_USWEST:6543/subscription;public;0..0;us-west
subscription.db.username=silkcloud
subscription.db.password.encrypted=`encrypt $DATABASE_PASSWORD`

EOF

cat <<EOF > $ENV/external.properties
# external configurations excluding payment and cloudant

# newrelic
common.newrelic.insights.account=661721
common.newrelic.insights.key.encrypted=`encrypt $NEWRELIC_KEY`
common.newrelic.insights.log.enable=false

# kount
order.risk.enable=false
order.risk.kount.merchantId=600900
order.risk.kount.url=https://risk.test.kount.net
order.risk.kount.keyFileName=ris_kount_test.p12
order.risk.kount.keyFilePass.encrypted=`encrypt $KOUNT_KEY`

# tele sign configuration
identity.conf.teleSign.customerId=A12FF775-1B07-4FB1-A569-3EF96B7FEFE8
identity.conf.teleSign.secretKey.encrypted=`encrypt $TELESIGN_KEY`

# recaptcha, not used
oauth.core.recaptcha.enabled=false
oauth.core.recaptcha.public.key=6Lep9PESAAAAAKYiXwtWgXjuSzuHv3dGKQZZOnK2
oauth.core.recaptcha.private.key.encrypted=`encrypt $RECAPTCHA_PRIVATE_KEY`
oauth.core.recaptcha.verify.endpoint=http://www.google.com/recaptcha/api/verify

# email
email.client.mandrill.enabled = true
email.client.mandrill.url=https://mandrillapp.com/api/1.0/messages/send-template
email.client.mandrill.key.encrypted=`encrypt $MANDRILL_KEY`

# entitlement
entitlement.aws.accesskeyId=AKIAI7OBWYUIU6SPIBDA
entitlement.aws.secretAccessKey.encrypted=`encrypt $ENTITLEMENT_AWS_SECRET_ACCESS_KEY`
entitlement.aws.bucketNames=static.oculusvr.com,ovr_ink_uploader
entitlement.aws.cloudFrontDomains=d1aifagf6hhneo.cloudfront.net,d39nlaid7cu5vo.cloudfront.net

EOF

cat <<EOF > $ENV/payment.properties
# payment
payment.provider.braintree.environment=SANDBOX
payment.provider.braintree.merchantid=ym3jpwzw4kqc3797
payment.provider.braintree.publickey=cf3m6fvrdmsz2y9w
payment.provider.braintree.privatekey.encrypted=`encrypt $BRAINTREE_PRIVATEKEY`
payment.provider.braintree.CSE.encrypted=`encrypt $BRAINTREE_CSE`
payment.provider.braintree.companyname=Oculus VR


payment.provider.paypal.apiversion=104.0
payment.provider.paypal.redirecturl=https://www.sandbox.paypal.com/cgi-bin/webscr
payment.provider.paypal.mode=sandbox
payment.provider.paypal.username=jb-us-seller_api1.paypal.com
payment.provider.paypal.password.encrypted=`encrypt $PAYPAL_PASSWORD`
payment.provider.paypal.signature.encrypted=`encrypt $PAYPAL_SIGNATURE`


payment.provider.adyen.redirecturl=https://test.adyen.com/hpp/select.shtml
payment.provider.adyen.merchantaccount=OculusCOM
payment.provider.adyen.skincode=0ceFRQOp
payment.provider.adyen.oldmobileskincode=0b6bjPEc
payment.provider.adyen.mobileskincode=RbpqLL88
payment.provider.adyen.skinsecret.encrypted=`encrypt $ADYEN_SKINSECRET`
payment.provider.adyen.paymenturl=https://pal-test.adyen.com/pal/servlet/soap/Payment
payment.provider.adyen.recurringurl=https://pal-test.adyen.com/pal/servlet/soap/Recurring
payment.provider.adyen.user=ws@Company.Oculus
payment.provider.adyen.password.encrypted=`encrypt $ADYEN_PASSWORD`
payment.provider.adyen.notifyUser=notifyUser
payment.provider.adyen.notifyPassword.encrypted=`encrypt $ADYEN_NOTIFYPASSWORD`

# not used yet
payment.jobs.batch.batchDirectory=/dummy/batch

EOF

cat <<EOF > $ENV/tax.properties
# tax provider
tax.provider.name=SABRIX

# avalara, not used just to unblock decrypt
avalara.authorization.encrypted=`encrypt $AVALARA_AUTH`

# sabrix
sabrix.host.system=DEV
sabrix.calling.system.number=Oculus eCommerce
sabrix.company.role=S
sabrix.external.company.id=139
sabrix.vat.registration.countries=AU,AT,BE,CZ,DK,FI,FR,DE,IE,IT,NL,PL,ES,SE,GB,US,CA
sabrix.username=sabrix
sabrix.password.encrypted=`encrypt $SABRIX_PASSWORD`
sabrix.version=G
sabrix.baseUrl=$SABRIX_URL
sabrix.tax.exclusive.countries=US,CA

EOF

# dataloader encrypted info
cat <<EOF > dataloader-clientsecrets.txt
service-clientid: $SERVICE_CLIENT_ID
service: `encryptclient $SERVICE_CLIENT_SECRET`
smoketest-clientid: $SMOKETEST_CLIENT_ID
smoketest: `encryptclient $SMOKETEST_CLIENT_SECRET`

2dstore: `encryptclient $STORE_CLIENT_SECRET`
adminTool: `encryptclient $ADMIN_TOOL_CLIENT_SECRET`
cms-admin-portal: `encryptclient $CMS_ADMIN_PORTAL_CLIENT_SECRET`
csr: `encryptclient $CSR_CLIENT_SECRET`
curationTool: `encryptclient $CURATION_TOOL_CLIENT_SECRET`
developerCenter: `encryptclient $DEVELOPER_CENTER_CLIENT_SECRET`
discourseForum: `encryptclient $DISCOURSE_FORUM_CLIENT_SECRET`
legacyDeveloperCenter-client: `encryptclient $LEGACY_DEVELOPER_CENTER_CLIENT_CLIENT_SECRET`
legacyDeveloperCenter-server: `encryptclient $LEGACY_DEVELOPER_CENTER_SERVER_CLIENT_SECRET`
migration: `encryptclient $MIGRATION_CLIENT_SECRET`
sewer: `encryptclient $SEWER_CLIENT_SECRET`
shop: `encryptclient $SHOP_CLIENT_SECRET`
id: `encryptclient $ID_CLIENT_SECRET`
share: `encryptclient $SHARE_CLIENT_SECRET`
technodrome: `encryptclient $TECHNODROME_CLIENT_SECRET`
EOF

# couchdb conf
mkdir -p couchdb/conf
cat <<EOF > couchdb/conf/${ENV}.json
{
    "dbs":{
        "couchdb.encrypted": "`encrypt $CLOUDANT_MH_URI`",
        "pii.encrypted": [
            "`encrypt $CLOUDANT_USEAST_URI`",
            "`encrypt $CLOUDANT_USWEST_URI`"
        ],
        "cloudant.encrypted": "`encrypt $CLOUDANT_MH_URI`"
    }
}
EOF

# liquibase conf
mkdir -p liquibase/conf/${ENV}.useast
mkdir -p liquibase/conf/${ENV}.uswest
mkdir -p liquibase/conf/${ENV}.replica

# useast
cat <<EOF > liquibase/conf/${ENV}.useast/billing.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USEAST:6543/billing;public
EOF

cat <<EOF > liquibase/conf/${ENV}.useast/crypto.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..1
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$CRYPTO_DB_MASTER_USEAST:6543/crypto;public
jdbc_url_1 = jdbc:postgresql://$CRYPTO_DB_SLAVE_USEAST:6543/crypto;public
EOF

cat <<EOF > liquibase/conf/${ENV}.useast/dualwrite.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USEAST:6543/dualwrite;public
EOF

cat <<EOF > liquibase/conf/${ENV}.useast/ewallet.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USEAST:6543/ewallet;public
EOF

cat <<EOF > liquibase/conf/${ENV}.useast/fulfilment.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USEAST:6543/fulfilment;public
EOF

cat <<EOF > liquibase/conf/${ENV}.useast/index.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USEAST:6543/index;public
EOF

cat <<EOF > liquibase/conf/${ENV}.useast/order.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USEAST:6543/order;public
EOF

cat <<EOF > liquibase/conf/${ENV}.useast/payment.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USEAST:6543/payment;public
EOF

cat <<EOF > liquibase/conf/${ENV}.useast/sharding.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USEAST:6543/sharding;public
EOF

cat <<EOF > liquibase/conf/${ENV}.useast/subscription.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USEAST:6543/subscription;public
EOF

# uswest

cat <<EOF > liquibase/conf/${ENV}.uswest/billing.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USWEST:6543/billing;public
EOF

cat <<EOF > liquibase/conf/${ENV}.uswest/crypto.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..1
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$CRYPTO_DB_MASTER_USWEST:6543/crypto;public
jdbc_url_1 = jdbc:postgresql://$CRYPTO_DB_SLAVE_USWEST:6543/crypto;public
EOF

cat <<EOF > liquibase/conf/${ENV}.uswest/dualwrite.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USWEST:6543/dualwrite;public
EOF

cat <<EOF > liquibase/conf/${ENV}.uswest/ewallet.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USWEST:6543/ewallet;public
EOF

cat <<EOF > liquibase/conf/${ENV}.uswest/fulfilment.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USWEST:6543/fulfilment;public
EOF

cat <<EOF > liquibase/conf/${ENV}.uswest/index.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USWEST:6543/index;public
EOF

cat <<EOF > liquibase/conf/${ENV}.uswest/order.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USWEST:6543/order;public
EOF

cat <<EOF > liquibase/conf/${ENV}.uswest/payment.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USWEST:6543/payment;public
EOF

cat <<EOF > liquibase/conf/${ENV}.uswest/sharding.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USWEST:6543/sharding;public
EOF

cat <<EOF > liquibase/conf/${ENV}.uswest/subscription.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..0
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_MASTER_USWEST:6543/subscription;public
EOF


# replica

cat <<EOF > liquibase/conf/${ENV}.replica/billing.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..1
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_REPLICA_USEAST:6543/billing;public
jdbc_url_1 = jdbc:postgresql://$DB_REPLICA_USWEST:6543/billing;public
EOF

cat <<EOF > liquibase/conf/${ENV}.replica/crypto.conf
[liquibase]
# not replicated, keep empty
intended_empty = true
EOF

cat <<EOF > liquibase/conf/${ENV}.replica/dualwrite.conf
[liquibase]
# not replicated, keep empty
intended_empty = true
EOF

cat <<EOF > liquibase/conf/${ENV}.replica/ewallet.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..1
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_REPLICA_USEAST:6543/ewallet;public
jdbc_url_1 = jdbc:postgresql://$DB_REPLICA_USWEST:6543/ewallet;public
EOF

cat <<EOF > liquibase/conf/${ENV}.replica/fulfilment.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..1
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_REPLICA_USEAST:6543/fulfilment;public
jdbc_url_1 = jdbc:postgresql://$DB_REPLICA_USWEST:6543/fulfilment;public
EOF

cat <<EOF > liquibase/conf/${ENV}.replica/index.conf
[liquibase]
# not replicated, keep empty
intended_empty = true
EOF

cat <<EOF > liquibase/conf/${ENV}.replica/order.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..1
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_REPLICA_USEAST:6543/order;public
jdbc_url_1 = jdbc:postgresql://$DB_REPLICA_USWEST:6543/order;public
EOF

cat <<EOF > liquibase/conf/${ENV}.replica/payment.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..1
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_REPLICA_USEAST:6543/payment;public
jdbc_url_1 = jdbc:postgresql://$DB_REPLICA_USWEST:6543/payment;public
EOF

cat <<EOF > liquibase/conf/${ENV}.replica/sharding.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..1
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_REPLICA_USEAST:6543/sharding;public
jdbc_url_1 = jdbc:postgresql://$DB_REPLICA_USWEST:6543/sharding;public
EOF

cat <<EOF > liquibase/conf/${ENV}.replica/subscription.conf
[liquibase]

jdbc_driver = org.postgresql.Driver

shard_range = 0..1
login_username = silkcloud
login_password.encrypted = `encrypt $DATABASE_PASSWORD`

jdbc_url_0 = jdbc:postgresql://$DB_REPLICA_USEAST:6543/subscription;public
jdbc_url_1 = jdbc:postgresql://$DB_REPLICA_USWEST:6543/subscription;public
EOF


# pgha

mkdir -p pgha/env/
cat <<EOF > pgha/env/${ENV}.useast_0.sh
#!/bin/bash

export MASTER_HOST=$DB_MASTER_USEAST
export SLAVE_HOST=$DB_SLAVE_USEAST
export REPLICA_HOST=$DB_REPLICA_USEAST
export BCP_HOST=$DB_BCP_USEAST

export REPLICA_DATABASES=('postgres' 'billing' 'ewallet' 'fulfilment' 'order' 'payment' 'sharding' 'subscription')
EOF

cat <<EOF > pgha/env/${ENV}.uswest_0.sh
#!/bin/bash

export MASTER_HOST=$DB_MASTER_USWEST
export SLAVE_HOST=$DB_SLAVE_USWEST
export REPLICA_HOST=$DB_REPLICA_USWEST
export BCP_HOST=$DB_BCP_USWEST

export REPLICA_DATABASES=('postgres' 'billing' 'ewallet' 'fulfilment' 'order' 'payment' 'sharding' 'subscription')
EOF

cat <<EOF > pgha/env/${ENV}.useast_crypto_0.sh
#!/bin/bash

export MASTER_HOST=$CRYPTO_DB_MASTER_USEAST
export SLAVE_HOST=
export REPLICA_HOST=
export BCP_HOST=

export REPLICA_DATABASES=()
EOF

cat <<EOF > pgha/env/${ENV}.useast_crypto_1.sh
#!/bin/bash

export MASTER_HOST=$CRYPTO_DB_SLAVE_USEAST
export SLAVE_HOST=
export REPLICA_HOST=
export BCP_HOST=

export REPLICA_DATABASES=()
EOF

cat <<EOF > pgha/env/${ENV}.uswest_crypto_0.sh
#!/bin/bash

export MASTER_HOST=$CRYPTO_DB_MASTER_USWEST
export SLAVE_HOST=
export REPLICA_HOST=
export BCP_HOST=

export REPLICA_DATABASES=()
EOF

cat <<EOF > pgha/env/${ENV}.uswest_crypto_1.sh
#!/bin/bash

export MASTER_HOST=$CRYPTO_DB_SLAVE_USWEST
export SLAVE_HOST=
export REPLICA_HOST=
export BCP_HOST=

export REPLICA_DATABASES=()
EOF

