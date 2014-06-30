package com.junbo.common.cloudant
import com.junbo.common.cloudant.model.CloudantUniqueItem
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * CloudantUniqueClient used for storing unique values to client.
 */
@CompileStatic
class CloudantUniqueClient extends CloudantClientBase<CloudantUniqueItem> {

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
