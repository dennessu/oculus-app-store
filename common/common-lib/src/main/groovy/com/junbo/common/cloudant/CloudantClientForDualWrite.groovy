package com.junbo.common.cloudant

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * CloudantClient for dual write.
 */
@CompileStatic
abstract class CloudantClientForDualWrite<T extends CloudantEntity> extends CloudantClientBase<T> {

    @Override
    Promise<T> cloudantPost(T entity) {
        return cloudantSave(entity);
    }

    @Override
    Promise<T> cloudantPut(T entity) {
        return cloudantSave(entity);
    }

    private Promise<T> cloudantSave(T entity) {
        // for dual write, always treat as save.
        entity.cloudantId = entity.id.toString()

        return cloudantGet(entity.cloudantId).then { T cloudantDoc ->
            if (cloudantDoc == null) {
                // create
                return super.cloudantPost(entity);
            }

            // resourceAge is increased by the data source (SQL)
            if (entity.resourceAge <= cloudantDoc.resourceAge) {
                // The target resource age is higher or equal than the resource age of the entity we are about to put.
                // This indicates a conflicting change or change already committed.
                return Promise.pure(cloudantDoc)
            }

            // update
            entity.cloudantRev = cloudantDoc.cloudantRev
            return super.cloudantPut(entity, cloudantDoc);
        }
    }
}
