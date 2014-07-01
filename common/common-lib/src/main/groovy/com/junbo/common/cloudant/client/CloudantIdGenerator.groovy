package com.junbo.common.cloudant.client

import com.junbo.common.cloudant.CloudantMarshaller
import com.junbo.common.cloudant.DefaultCloudantMarshaller
import com.junbo.common.cloudant.exception.CloudantConnectException
import com.junbo.common.cloudant.exception.CloudantException
import com.junbo.common.cloudant.model.CloudantError
import com.junbo.common.cloudant.model.CloudantUuids
import com.junbo.common.util.Utils
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.Realm
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils

import javax.ws.rs.core.UriBuilder
import java.util.concurrent.ConcurrentLinkedQueue

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture

/**
 * Cloudant ID Generator.
 */
@CompileStatic
class CloudantIdGenerator {
    private static final Logger logger = LoggerFactory.getLogger(CloudantIdGenerator)

    private static final String GENERATE_ID = '/_uuids'
    private static final ConcurrentLinkedQueue<String> cloudantIds = new ConcurrentLinkedQueue<>()
    private static final CloudantMarshaller cloudantMarshaller = DefaultCloudantMarshaller.instance()

    private static JunboAsyncHttpClient asyncHttpClient
    private static String cloudantUser
    private static String cloudantPassword
    private static String cloudantDBUri
    private static int batchSize = 100

    void setAsyncHttpClient(JunboAsyncHttpClient asyncHttpClient) {
        CloudantIdGenerator.asyncHttpClient = asyncHttpClient
    }

    void setCloudantDBUri(String cloudantDBUri) {
        CloudantIdGenerator.cloudantDBUri = Utils.filterPerDataCenterConfig(cloudantDBUri, "cloudantDBUri")
    }

    void setCloudantUser(String cloudantUser) {
        CloudantIdGenerator.cloudantUser = cloudantUser
    }

    void setCloudantPassword(String cloudantPassword) {
        CloudantIdGenerator.cloudantPassword = cloudantPassword
    }

    void setBatchSize(int batchSize) {
        CloudantIdGenerator.batchSize = batchSize
    }

    static Promise<String> nextId() {
        String result = cloudantIds.poll();
        if (result != null) {
            return Promise.pure(result);
        }

        logger.debug("Not enough Ids, get more Ids from cloudant.")

        UriBuilder uriBuilder = UriBuilder.fromUri(cloudantDBUri)
        uriBuilder.path(GENERATE_ID)

        def requestBuilder = asyncHttpClient.prepareGet(uriBuilder.toTemplate())
        if (!StringUtils.isEmpty(cloudantUser)) {
            Realm realm = new Realm.RealmBuilder().setPrincipal(cloudantUser).setPassword(cloudantPassword)
                    .setUsePreemptiveAuth(true).setScheme(Realm.AuthScheme.BASIC).build();
            requestBuilder.setRealm(realm);
        }
        requestBuilder.addQueryParameter("count", batchSize.toString())

        try {
            return Promise.wrap(asGuavaFuture(requestBuilder.execute())).then { Response response ->
                if (response.statusCode != HttpStatus.OK.value()) {
                    CloudantError cloudantError = cloudantMarshaller.unmarshall(response.responseBody, CloudantError)
                    throw new CloudantException("Failed to generate IDs, error: $cloudantError.error," +
                            " reason: $cloudantError.reason")
                }

                CloudantUuids uuids = cloudantMarshaller.unmarshall(response.responseBody, CloudantUuids)
                result = uuids.uuids.first()

                // add others to the pool
                cloudantIds.addAll(uuids.uuids.tail())
                logger.debug("Refilled Cloudant Ids, current count: {}", cloudantIds.size())

                return Promise.pure(result)
            }
        } catch (IOException e) {
            throw new CloudantConnectException('Exception happened while executing request to cloudant DB', e)
        }
    }
}
