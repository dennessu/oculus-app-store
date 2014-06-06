package com.junbo.sharding.repo
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.util.Identifiable
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * The base repository for cloudant.
 * @param <K> the entity key.
 * @param <T> the entity type.
 */
@CompileStatic
abstract class BaseCloudantRepository<T extends CloudantEntity, K> extends CloudantClient<T> {
    protected IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    protected abstract K generateId();

    @Override
    Promise<T> create(T entity) {
        Identifiable<K> identifiable = (Identifiable<K>)entity;
        if (identifiable.id == null) {
            identifiable.id = generateId();
        }
        return Promise.pure(cloudantPost(entity))
    }

    @Override
    Promise<T> update(T entity) {
        return Promise.pure(cloudantPut(entity))
    }

    @Override
    Promise<T> get(K id) {
        return Promise.pure(cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(K id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }
}
