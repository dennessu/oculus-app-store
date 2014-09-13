package com.junbo.common.cloudant

import com.junbo.langur.core.track.TrackContextManager
import com.junbo.langur.core.promise.Promise
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
}
