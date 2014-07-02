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
        // Todo:    Need to read from the Universe to cover time and createdBy
        entity.createdBy = 123L
        entity.createdTime = new Date()

        return super.cloudantPost(entity);
    }

    @Override
    Promise<T> cloudantPut(T entity) {
        // Todo:    Need to read from the Universe to cover time and createdBy
        entity.updatedBy = 123L
        entity.updatedTime = new Date()

        return super.cloudantPut(entity);
    }
}
