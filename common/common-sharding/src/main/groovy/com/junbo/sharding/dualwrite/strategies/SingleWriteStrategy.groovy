/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite.strategies;

import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.DataAccessStrategy;
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic;
import org.springframework.beans.factory.annotation.Required;

import java.lang.reflect.Method;

/**
 * SingleWriteStrategy.
 *
 * In this mode, all calls are forwarded to the repository directly.
 */
@CompileStatic
public class SingleWriteStrategy implements DataAccessStrategy {
    private BaseRepository repositoryImpl;

    public SingleWriteStrategy(BaseRepository repositoryImpl) {
        this.repositoryImpl = repositoryImpl;
    }

    @Required
    public void setRepositoryImpl(BaseRepository repositoryImpl) {
        this.repositoryImpl = repositoryImpl;
    }

    @Override
    public Promise<CloudantEntity> invokeReadMethod(Method method, Object[] args) {
        return (Promise<CloudantEntity>)method.invoke(repositoryImpl, args);
    }

    @Override
    public Promise<CloudantEntity> invokeWriteMethod(Method method, Object[] args) {
        return (Promise<CloudantEntity>)method.invoke(repositoryImpl, args);
    }

    @Override
    public Promise<Void> invokeDeleteMethod(Method method, Object[] args) {
        return (Promise<Void>)method.invoke(repositoryImpl, args);
    }
}
