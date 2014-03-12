/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.exception.CatalogException;
import com.junbo.catalog.common.exception.NotFoundException;
import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.core.BaseService;
import com.junbo.catalog.db.repo.EntityDraftRepository;
import com.junbo.catalog.db.repo.EntityRepository;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.common.id.Id;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Base service implementation.
 * @param <T> the entity type.
 */
public abstract class BaseServiceImpl<T extends BaseModel> implements BaseService<T> {
    public abstract <E extends EntityRepository<T>> E getEntityRepo();
    public abstract <E extends EntityDraftRepository<T>> E getEntityDraftRepo();

    @Override
    public T get(Long entityId, EntityGetOptions options) {
        T entity;
        if (options.getStatus() != null && !Status.RELEASED.equalsIgnoreCase(options.getStatus())) {
            entity = getEntityDraftRepo().get(entityId);
            if (!options.getStatus().equalsIgnoreCase(entity.getStatus())) {
                throw new NotFoundException(entity.getEntityType(), entityId);
            }
        } else {
            entity = getEntityRepo().get(entityId, options.getTimestamp());
        }

        checkEntityNotNull(entityId, entity);

        return entity;
    }

    @Override
    public List<T> getEntities(EntitiesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getEntityIds())) {
            List<T> entities = new ArrayList<>();

            for (Id entityId : options.getEntityIds()) {
                T entity;
                if (Status.RELEASED.equalsIgnoreCase(options.getStatus())) {
                    entity = getEntityRepo().get(entityId.getValue(), options.getTimestamp());
                } else {
                    entity = getEntityDraftRepo().get(entityId.getValue());
                }

                if (entity != null) {
                    entities.add(entity);
                }
            }
            return entities;
        } else {
            options.ensurePagingValid();
            List<T> draftEntities = getEntityDraftRepo().getEntities(options.getStart(), options.getSize());
            if (!Status.RELEASED.equalsIgnoreCase(options.getStatus())) {
                return draftEntities;
            }

            List<T> entities = new ArrayList<>();
            for (T draftEntity : draftEntities) {
                T entity = getEntityRepo().get(draftEntity.getId(), options.getTimestamp());
                if (entity != null) {
                    entities.add(entity);
                }
            }

            return entities;
        }
    }

    @Override
    public T create(T entity) {
        if (entity == null) {
            throw new CatalogException("TODO");
        }

        entity.setRevision(Constants.INITIAL_CREATION_REVISION);
        entity.setStatus(Status.DESIGN);

        Long entityId = getEntityDraftRepo().create(entity);

        return getEntityDraftRepo().get(entityId);
    }

    @Override
    public T update(Long entityId, T entity) {
        if (entity == null || entityId != entity.getId()) {
            throw new CatalogException("TODO");
        }

        getEntityDraftRepo().update(entity);
        return getEntityDraftRepo().get(entity.getId());
    }

    @Override
    public T review(Long entityId) {
        updateDraftStatus(entityId, Status.PENDING_REVIEW);
        return getEntityDraftRepo().get(entityId);
    }

    @Override
    public T release(Long entityId) {
        updateStatus(entityId, Status.RELEASED);
        return getEntityRepo().get(entityId, null);
    }

    @Override
    public T reject(Long entityId) {
        updateDraftStatus(entityId, Status.REJECTED);
        return getEntityDraftRepo().get(entityId);
    }

    /**
     * Remove the released entity. The draft entity is still kept.
     * @param entityId the id of entity to be removed.
     * @return the removed entity id.
     */
    @Override
    public Long remove(Long entityId) {
        return updateReleasedStatus(entityId, Status.DELETED);
    }

    /**
     * Delete both draft and released entity.
     * @param entityId the id of entity to be deleted.
     * @return the deleted entity id.
     */
    @Override
    public Long delete(Long entityId) {
        return updateStatus(entityId, Status.DELETED);
    }

    private Long updateStatus(Long entityId, String status) {
        T entity = getEntityDraftRepo().get(entityId);
        checkEntityNotNull(entityId, entity);
        entity.setStatus(status);
        getEntityDraftRepo().update(entity);
        getEntityRepo().create(entity);
        return entity.getId();
    }

    private Long updateReleasedStatus(Long entityId, String status) {
        T entity = getEntityRepo().get(entityId, null);
        checkEntityNotNull(entityId, entity);
        entity.setStatus(status);
        getEntityRepo().create(entity);
        return entity.getId();
    }

    private Long updateDraftStatus(Long entityId, String status) {
        T entity = getEntityDraftRepo().get(entityId);
        checkEntityNotNull(entityId, entity);
        entity.setStatus(status);
        getEntityDraftRepo().update(entity);
        return entity.getId();
    }

    private void checkEntityNotNull(Long entityId, T entity) {
        if (entity == null) {
            throw new NotFoundException(entity.getEntityType(), entityId);
        }
    }
}
