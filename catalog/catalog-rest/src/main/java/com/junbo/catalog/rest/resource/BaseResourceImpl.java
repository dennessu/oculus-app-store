/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.BaseService;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.catalog.spec.model.common.VersionedModel;
import com.junbo.common.id.Id;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Base resource implementation.
 * @param <T> the model type.
 */
public abstract class BaseResourceImpl<T extends VersionedModel> {
    protected abstract <E extends BaseService<T>> E getEntityService();

    public Promise<Results<T>> getEntities(EntitiesGetOptions options) {
        List<T> entities = getEntityService().getEntities(options);
        Results<T> resultList = new Results<>();
        resultList.setItems(entities);
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
        if (entity.getId() == null) {
            throw AppErrors.INSTANCE.missingField("id").exception();
        }
        if (!entityId.getValue().equals(entity.getId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("id", entity.getId(), entityId).exception();
        }

        // TODO: change this
        if (Status.PENDING_REVIEW.equalsIgnoreCase(entity.getStatus())) {
            return review(entityId.getValue());
        } else if (Status.REJECTED.equalsIgnoreCase(entity.getStatus())) {
            return reject(entityId.getValue());
        } else if (Status.RELEASED.equalsIgnoreCase(entity.getStatus())) {
            return release(entityId.getValue());
        } else if (Status.REMOVED.equalsIgnoreCase(entity.getStatus())) {
            return remove(entityId.getValue());
        }

        if (entity.getStatus()==null) {
            entity.setStatus(Status.DESIGN);
        }

        return Promise.pure(getEntityService().update(entityId.getValue(), entity));
    }

    public Promise<T> remove(Long entityId) {
        return Promise.pure(getEntityService().remove(entityId));
    }

    public Promise<Response> delete(Long entityId) {
        getEntityService().delete(entityId);
        return Promise.pure(Response.status(204).build());
    }
}
