package com.junbo.common.cloudant
import com.junbo.common.cloudant.model.CloudantUniqueItem
import com.junbo.configuration.ConfigService
import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * CloudantUniqueClient used for storing unique values to client.
 */
@CompileStatic
class CloudantUniqueClient extends CloudantClientBase<CloudantUniqueItem> {
    private static final String UNIQUE_DB_NAME = "unique"

    private static CloudantUniqueClient instance = new CloudantUniqueClient()
    public static CloudantUniqueClient instance() {
        return instance
    }

    private CloudantUniqueClient() {
        ConfigService configService = ConfigServiceManager.instance()
        super.setCloudantDbUri(configService.getConfigValue("common.cloudant.url"))
        super.setDbName(UNIQUE_DB_NAME)
        super.afterPropertiesSet()
    }

    Promise<CloudantUniqueItem> create(CloudantUniqueItem item) {
        return cloudantPost(item)
    }

    Promise<Void> delete(CloudantUniqueItem item) {
        return cloudantDelete(item)
    }

    Promise<List<CloudantUniqueItem>> getByDoc(String id) {
        return queryView("by_doc", id)
    }
}
