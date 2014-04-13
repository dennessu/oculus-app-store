/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core;

import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.FactoryBean;

/**
 * Java doc.
 * @param <T>
 */
public class ShardAwareDaoFactoryBean<T> implements FactoryBean<T> {
    private Class<T> daoInterface;
    private IdGeneratorFacade idGeneratorFacade;
    private Object daoTarget;

    public Class<T> getDaoInterface() {
        return daoInterface;
    }

    public IdGeneratorFacade getIdGeneratorFacade() {
        return idGeneratorFacade;
    }

    public void setDaoInterface(Class<T> daoInterface) {
        this.daoInterface = daoInterface;
    }

    public void setIdGeneratorFacade(IdGeneratorFacade idGeneratorFacade) {
        this.idGeneratorFacade = idGeneratorFacade;
    }

    public Object getDaoTarget() {
        return daoTarget;
    }

    public void setDaoTarget(Object daoTarget) {
        this.daoTarget = daoTarget;
    }

    @Override
    public T getObject() throws Exception {
        return ShardAwareDaoProxy.newProxyInstance(this.daoInterface, this.daoTarget, this.idGeneratorFacade);
    }

    @Override
    public Class<?> getObjectType() {
        return this.daoInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
