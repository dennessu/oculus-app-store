package com.junbo.sharding.repo
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * The base repository for cloudant for dual write. Won't generate ids or do something special.
 * @param <K> the entity key.
 * @param <T> the entity type.
 */
@CompileStatic
abstract class BaseCloudantRepositoryForDualWrite<T extends CloudantEntity, K> extends CloudantClient<T> {
    public BaseCloudantRepositoryForDualWrite() {
        setNoOverrideWrites(true)
    }

    @Override
    Promise<T> create(T entity) {
        return cloudantPost(entity)
    }

    @Override
    Promise<T> update(T entity, T oldEntity) {
        return cloudantPut(entity, oldEntity)
    }

    @Override
    Promise<T> get(K id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(K id) {
        return cloudantDelete(id.toString())
    }
}
