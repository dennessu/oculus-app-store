/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.BaseService;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.common.id.Id;
import com.junbo.langur.core.promise.Promise;

import java.util.List;

/**
 * Base resource implementation.
 * @param <T> the model type.
 */
public abstract class BaseResourceImpl<T extends BaseModel> {
    protected abstract <E extends BaseService<T>> E getEntityService();

    public Promise<ResultList<T>> getEntities(EntitiesGetOptions options) {
        List<T> entities = getEntityService().getEntities(options);
        ResultList<T> resultList = new ResultList<>();
        resultList.setResults(entities);
        resultList.setHref("href TODO");
        resultList.setNext("next TODO");
        return Promise.pure(resultList);
    }

    public Promise<T> get(Id entityId, EntityGetOptions options) {
        T entity = getEntityService().get(entityId.getValue(), options);
        return Promise.pure(entity);
    }

    public Promise<T> create(T entity) {
        T result = getEntityService().create(entity);
        return Promise.pure(result);
    }

    public Promise<T> review(Long entityId) {
        T entity = getEntityService().review(entityId);
        return Promise.pure(entity);
    }

    public Promise<T> release(Long entityId) {
        T entity = getEntityService().release(entityId);
        return Promise.pure(entity);
    }

    public Promise<T> reject(Long entityId) {
        return Promise.pure(getEntityService().reject(entityId));
    }

    public Promise<T> update(Id entityId, T entity) {
        return Promise.pure(getEntityService().update(entityId.getValue(), entity));
    }

    public Promise<Void> remove(Long entityId) {
        getEntityService().remove(entityId);
        return Promise.pure(null);
    }

    public Promise<Void> delete(Long entityId) {
        getEntityService().delete(entityId);
        return Promise.pure(null);
    }
}
