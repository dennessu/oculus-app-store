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
    protected abstract <S extends BaseService<T>> S getEntityService();

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

    public Promise<T> review(Id entityId) {
        T entity = getEntityService().review(entityId.getValue());
        return Promise.pure(entity);
    }

    public Promise<T> release(Id entityId) {
        T entity = getEntityService().release(entityId.getValue());
        return Promise.pure(entity);
    }

    public Promise<T> reject(Id entityId) {
        return Promise.pure(getEntityService().reject(entityId.getValue()));
    }

    public Promise<T> update(T entity) {
        return Promise.pure(getEntityService().update(entity));
    }

    public Promise<Void> remove(Id entityId) {
        getEntityService().remove(entityId.getValue());
        return Promise.pure(null);
    }

    public Promise<Void> delete(Id entityId) {
        getEntityService().delete(entityId.getValue());
        return Promise.pure(null);
    }
}
