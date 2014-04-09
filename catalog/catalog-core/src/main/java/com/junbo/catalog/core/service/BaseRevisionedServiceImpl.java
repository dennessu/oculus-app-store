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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        checkEntityNotNull(entityId, entity, getEntityType());
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
        checkEntityNotNull(entityId, existingEntity, getEntityType());

        if (Boolean.TRUE.equals(entity.getCurated())){
            checkFieldNotNull(entity.getCurrentRevisionId(), "currentRevision");
        }

        getEntityRepo().update(entity);
        return getEntityRepo().get(entityId);
    }

    @Override
    public void deleteEntity(Long entityId) {
        E existingEntity = getEntityRepo().get(entityId);
        checkEntityNotNull(entityId, existingEntity, getEntityType());
        getEntityRepo().delete(entityId);
    }

    @Override
    public T getRevision(Long revisionId) {
        return getRevisionRepo().get(revisionId);
    }

    @Override
    public T createRevision(T revision) {
        //TODO: validation
        Long revisionId = getRevisionRepo().create(revision);
        return getRevisionRepo().get(revisionId);
    }

    @Override
    public T updateRevision(Long revisionId, T revision) {
        T existingRevision = getRevisionRepo().get(revisionId);
        checkEntityNotNull(revisionId, existingRevision, getRevisionType());

        getRevisionRepo().update(existingRevision);
        return getRevisionRepo().get(revisionId);
    }


    @Override
    public void deleteRevision(Long revisionId) {
        T existingRevision = getRevisionRepo().get(revisionId);
        checkEntityNotNull(revisionId, existingRevision, getRevisionType());
        getRevisionRepo().delete(revisionId);
    }

    protected void checkEntityNotNull(Long entityId, BaseModel entity, String name) {
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

    protected void checkFieldShouldNull(Object field, String fieldName) {
        if (field != null) {
            throw AppErrors.INSTANCE.fieldNotCorrect(fieldName, "Should be null.").exception();
        }
    }

    protected void checkFieldShouldEmpty(Collection collection, String fieldName) {
        if (!CollectionUtils.isEmpty(collection)) {
            throw AppErrors.INSTANCE.fieldNotCorrect(fieldName, "Should be null.").exception();
        }
    }

    protected void checkFieldShouldEmpty(Map map, String fieldName) {
        if (!CollectionUtils.isEmpty(map)) {
            throw AppErrors.INSTANCE.fieldNotCorrect(fieldName, "Should be null.").exception();
        }
    }

    protected void checkFieldShouldEmpty(String field, String fieldName) {
        if (!StringUtils.isEmpty(field)) {
            throw AppErrors.INSTANCE.fieldNotCorrect(fieldName, "Should be null.").exception();
        }
    }

    protected void validateId(Long expectedId, Long actualId) {
        if (actualId == null) {
            throw AppErrors.INSTANCE.missingField("id").exception();
        }
        if (!expectedId.equals(actualId)) {
            throw AppErrors.INSTANCE.fieldNotMatch("id", actualId, expectedId).exception();
        }
    }

    protected abstract <RE extends BaseEntityRepository<E>> RE getEntityRepo();
    protected abstract <RR extends BaseRevisionRepository<T>> RR getRevisionRepo();
    protected abstract String getEntityType();
    protected abstract String getRevisionType();
}
