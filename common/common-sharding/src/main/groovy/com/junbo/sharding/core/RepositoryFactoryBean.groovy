package com.junbo.sharding.core

import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 4/20/14.
 */
@CompileStatic
class RepositoryFactoryBean<T> implements FactoryBean<T> {
    private Class<T> repositoryInterface
    private PersistentMode persistentMode
    private Object sqlRepositoryTarget
    private Object cloudantRepositoryTarget

    @Required
    void setRepositoryInterface(Class<T> repositoryInterface) {
        this.repositoryInterface = repositoryInterface
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
                this.cloudantRepositoryTarget, this.persistentMode)
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
