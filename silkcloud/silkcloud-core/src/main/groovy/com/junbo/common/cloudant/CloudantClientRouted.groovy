package com.junbo.common.cloudant
import com.junbo.common.cloudant.client.CloudantDbUri
import com.junbo.common.cloudant.client.CloudantUri
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.shuffle.Oculus48Id
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * CloudantClientRouted.
 */
@CompileStatic
abstract class CloudantClientRouted<T extends CloudantEntity> extends CloudantClientBase<T> {
    private static Logger logger = LoggerFactory.getLogger(CloudantClientRouted.class)

    public CloudantDbUri getDbUri(String id) {
        CloudantUri uri = cloudantGlobalUri.currentDcUri
        if (id != null) {
            try {
                Long value = Long.parseLong(id)
                if (value != null) {
                    int dc = (int) ((value.longValue() >> 2) & 0xF)
                    uri = cloudantGlobalUri.getUri(dc)
                    if (uri == null) {
                        logger.error("Cloudant URI not found for datacenter: $dc id: $id")
                        throw AppCommonErrors.INSTANCE.invalidId("id", Oculus48Id.format(value)).exception()
                    }
                }
            } catch (NumberFormatException ex) {
                logger.error("Error parsing id to long. id: $id")
                throw AppCommonErrors.INSTANCE.invalidId("id", id).exception()
            }
        }
        return new CloudantDbUri(cloudantUri: uri, dbName: dbName, fullDbName: cloudantDbUri.fullDbName)
    }
}
