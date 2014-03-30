/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.BaseRevisionedService;
import com.junbo.catalog.db.repo.BaseEntityRepository;
import com.junbo.catalog.db.repo.BaseRevisionRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.*;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Base service implementation for revisioned entity.
 * @param <E> entity.
 * @param <T> entity revision.
 */
public abstract class BaseRevisionedServiceImpl<E extends BaseEntityModel, T extends BaseRevisionModel>
        implements BaseRevisionedService<E, T> {

    @Override
    public E getEntity(Long entityId) {
        E entity = getEntityRepo().get(entityId);
        checkNotNull(entityId, entity, getEntityType());
        return entity;
    }

    @Override
    public List getEntities(EntitiesGetOptions options) {
        return null;
    }

    @Override
    public E createEntity(E entity) {
        Long entityId = getEntityRepo().create(entity);
        return getEntityRepo().get(entityId);
    }

    @Override
    public E updateEntity(Long entityId, E entity) {
        E existingEntity = getEntityRepo().get(entityId);
        checkNotNull(entityId, existingEntity, getEntityType());

        if (Status.PUBLISHED.equalsIgnoreCase(entity.getStatus())){
            checkFieldNotNull(entity.getCurrentRevisionId(), "currentRevision");
        }

        getEntityRepo().update(entity);
        return getEntityRepo().get(entityId);
    }

    @Override
    public void deleteEntity(Long entityId) {
        E existingEntity = getEntityRepo().get(entityId);
        checkNotNull(entityId, existingEntity, getEntityType());
        existingEntity.setStatus(Status.DELETED);
        getEntityRepo().update(existingEntity);
    }

    @Override
    public T getRevision(Long revisionId) {
        return getRevisionRepo().get(revisionId);
    }

    @Override
    public T createRevision(T revision) {
        Long revisionId = getRevisionRepo().create(revision);
        return getRevisionRepo().get(revisionId);
    }

    @Override
    public T updateRevision(Long revisionId, T revision) {
        T existingRevision = getRevisionRepo().get(revisionId);
        checkNotNull(revisionId, existingRevision, getRevisionType());

        getRevisionRepo().update(existingRevision);
        return getRevisionRepo().get(revisionId);
    }


    @Override
    public void deleteRevision(Long revisionId) {
        T existingRevision = getRevisionRepo().get(revisionId);
        existingRevision.setStatus(Status.DELETED);
        getRevisionRepo().update(existingRevision);
    }

    private void checkNotNull(Long entityId, BaseModel entity, String name) {
        if (entity == null) {
            throw AppErrors.INSTANCE.notFound(name, entityId).exception();
        }
    }


    protected void checkFieldNotNull(Object field, String fieldName) {
        if (field == null) {
            throw AppErrors.INSTANCE.missingField(fieldName).exception();
        }
    }

    protected void checkFieldNotEmpty(String field, String fieldName) {
        if (StringUtils.isEmpty(field)) {
            throw AppErrors.INSTANCE.missingField(fieldName).exception();
        }
    }

    protected abstract <RE extends BaseEntityRepository<E>> RE getEntityRepo();
    protected abstract <RR extends BaseRevisionRepository<T>> RR getRevisionRepo();
    protected abstract String getEntityType();
    protected abstract String getRevisionType();
}
