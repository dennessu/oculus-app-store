package com.junbo.common.cloudant.client
import com.junbo.common.cloudant.CloudantMarshaller
import com.junbo.common.cloudant.DefaultCloudantMarshaller
import com.junbo.common.cloudant.exception.CloudantException
import com.junbo.common.cloudant.model.CloudantError
import com.junbo.common.cloudant.model.CloudantUuids
import com.junbo.configuration.ConfigService
import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

import java.util.concurrent.ConcurrentLinkedQueue
/**
 * Cloudant ID Generator.
 */
@CompileStatic
class CloudantIdGenerator {
    private static final Logger logger = LoggerFactory.getLogger(CloudantIdGenerator)

    private static final String GENERATE_ID = '/_uuids'
    private static final ConcurrentLinkedQueue<String> cloudantIds = new ConcurrentLinkedQueue<>()
    private static final CloudantMarshaller marshaller = DefaultCloudantMarshaller.instance()
    private static final CloudantClientImpl impl = CloudantClientImpl.instance()

    private static CloudantGlobalUri cloudantGlobalUri
    private static CloudantDbUri cloudantDbUri
    private static int batchSize

    static {
        ConfigService configService = ConfigServiceManager.instance()
        cloudantGlobalUri = new CloudantGlobalUri(configService.getConfigValue("common.cloudant.url"))
        batchSize = Integer.parseInt(configService.getConfigValue("common.cloudant.bulk.idBatchSize"))
        cloudantDbUri = new CloudantDbUri(cloudantUri: cloudantGlobalUri.currentDcUri)
    }

    public static Promise<String> nextId() {
        String result = cloudantIds.poll();
        if (result != null) {
            return Promise.pure(result);
        }

        logger.debug("Not enough Ids, get more Ids from cloudant.")

        return impl.executeRequest(cloudantDbUri, HttpMethod.GET, GENERATE_ID, ["count": batchSize.toString()], null).then { Response response ->
            if (response.statusCode != HttpStatus.OK.value()) {
                CloudantError cloudantError = marshaller.unmarshall(response.responseBody, CloudantError)
                throw new CloudantException("Failed to generate IDs, error: $cloudantError.error," +
                        " reason: $cloudantError.reason")
            }

            CloudantUuids uuids = marshaller.unmarshall(response.responseBody, CloudantUuids)
            result = uuids.uuids.first()

            // add others to the pool
            cloudantIds.addAll(uuids.uuids.tail())
            logger.debug("Refilled Cloudant Ids, current count: {}", cloudantIds.size())

            return Promise.pure(result)
        }
    }
}
