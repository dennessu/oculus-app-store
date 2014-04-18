package com.junbo.identity.data.repository.impl

import com.junbo.common.id.Id
import com.junbo.common.util.Identifiable
import com.junbo.identity.data.repository.IdentityBaseRepository
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-18.
 */
@CompileStatic
abstract class IdentityBaseRepositoryImpl<ID extends Id, M extends Identifiable<ID>,
        Repo extends IdentityBaseRepository<M, ID>> implements IdentityBaseRepository<M, ID> {

    private Repo cloudantRepo
    private Repo sqlRepo
    private PersistentMode mode
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Required
    void setMode(PersistentMode mode) {
        this.mode = mode
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Required
    void setCloudantRepo(Repo cloudantRepo) {
        this.cloudantRepo = cloudantRepo
    }

    @Required
    void setSqlRepo(Repo sqlRepo) {
        this.sqlRepo = sqlRepo
    }

    Promise<M> get(ID id) {
        // ALWAYS READ FROM CLOUDANT
        return this.cloudantRepo.get(id)
    }

    Promise<M> create(M model) {
        return null
    }

    Promise<M> update(M model) {
        return null
    }

    Promise<Void> delete(ID id) {
        if (mode == PersistentMode.CLOUDANT_READ_WRITE) {
            this.cloudantRepo.delete(id)
        }
        else if (mode == PersistentMode.CLOUDANT_READ_DUAL_WRITE_SQL_PRIMARY
                || mode == PersistentMode.CLOUDANT_READ_DUAL_WRITE_CLOUDANT_PRIMARY) {
            this.sqlRepo.delete(id)
            this.cloudantRepo.delete(id)
        }

        return Promise.pure(null)
    }
}
