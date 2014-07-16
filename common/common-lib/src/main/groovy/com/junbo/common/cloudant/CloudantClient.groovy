package com.junbo.common.cloudant

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * CloudantClient used for CloudantOnly resources.
 */
@CompileStatic
abstract class CloudantClient<T extends CloudantEntity> extends CloudantClientBase<T> {

    @Override
    Promise<T> cloudantPost(T entity) {
        entity = tracker.trackCreate(entity)
        return super.cloudantPost(entity)
    }

    @Override
    Promise<T> cloudantPut(T entity, T oldEntity) {
        entity = tracker.trackUpdate(entity, oldEntity)
        return super.cloudantPut(entity, oldEntity)
    }
}
