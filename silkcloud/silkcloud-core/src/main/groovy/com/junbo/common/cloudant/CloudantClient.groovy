package com.junbo.common.cloudant

import com.junbo.common.cloudant.client.CloudantDbUri
import com.junbo.common.cloudant.client.CloudantGlobalUri
import com.junbo.common.cloudant.client.CloudantUri
import com.junbo.configuration.topo.DataCenters
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.track.TrackContextManager
import groovy.transform.CompileStatic

/**
 * CloudantClient used for CloudantOnly resources.
 */
@CompileStatic
abstract class CloudantClient<T extends CloudantEntity> extends CloudantClientBase<T> {

    @Override
    Promise<T> cloudantPost(T entity) {
        def trackContext = TrackContextManager.get()
        entity.createdBy = trackContext.currentUserId ?: 0L
        entity.createdByClient = trackContext.currentClientId ?: ""
        entity.createdTime = new Date()
        entity.updatedBy = entity.createdBy
        entity.updatedByClient = entity.createdByClient
        entity.updatedTime = entity.createdTime
        return super.cloudantPost(entity)
    }

    @Override
    Promise<T> cloudantPut(T entity, T oldEntity) {
        def trackContext = TrackContextManager.get()
        entity.createdBy = oldEntity.createdBy
        entity.createdByClient = oldEntity.createdByClient
        entity.createdTime = oldEntity.createdTime
        entity.updatedBy = trackContext.currentUserId ?: 0L
        entity.updatedByClient = trackContext.currentClientId ?: ""
        entity.updatedTime = new Date()
        return super.cloudantPut(entity, oldEntity)
    }

    protected CloudantDbUri getDbUriByDc(int dc) {
        return getDbUriByDc(this.cloudantGlobalUri, dc);
    }

    protected CloudantDbUri getDbUriByDc(CloudantGlobalUri cloudantGlobalUri, int dc) {
        CloudantUri uri
        if (!DataCenters.instance().hasDataCenter(dc)) {
            return null
        }

        try {
            uri = cloudantGlobalUri.getUri(dc)
        } catch (RuntimeException e) {
            return null
        }

        if (uri == null) {
            return null
        }
        return new CloudantDbUri(cloudantUri: uri, dbName: dbName, fullDbName: cloudantDbUri.fullDbName)
    }
}
