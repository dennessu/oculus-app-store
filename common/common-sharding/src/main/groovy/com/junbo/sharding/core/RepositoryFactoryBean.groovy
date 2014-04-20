package com.junbo.sharding.core

import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 4/20/14.
 */
class RepositoryFactoryBean<T> implements FactoryBean<T> {
    private Class<T> repositoryInterface
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator
    private PersistentMode persistentMode
    private Object sqlRepositoryTarget
    private Object cloudantRepositoryTarget

    @Required
    void setRepositoryInterface(Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Required
    void setPersistentMode(PersistentMode persistentMode) {
        this.persistentMode = persistentMode
    }

    void setSqlRepositoryTarget(Object sqlRepositoryTarget) {
        this.sqlRepositoryTarget = sqlRepositoryTarget
    }

    void setCloudantRepositoryTarget(Object cloudantRepositoryTarget) {
        this.cloudantRepositoryTarget = cloudantRepositoryTarget
    }

    @Override
    T getObject() throws Exception {
        return RepositoryProxy.newProxyInstance(this.repositoryInterface, this.sqlRepositoryTarget,
                this.cloudantRepositoryTarget, this.persistentMode, this.shardAlgorithm, this.idGenerator)
    }

    @Override
    Class<?> getObjectType() {
        return this.repositoryInterface
    }

    @Override
    boolean isSingleton() {
        return true
    }
}
