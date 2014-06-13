/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.BaseRevisionedService;
import com.junbo.catalog.db.repo.BaseEntityRepository;
import com.junbo.catalog.db.repo.BaseRevisionRepository;
import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.catalog.spec.model.common.BaseRevisionModel;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.error.AppError;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Base service implementation for revisioned entity.
 * @param <E> entity.
 * @param <T> entity revision.
 */
public abstract class BaseRevisionedServiceImpl<E extends BaseEntityModel, T extends BaseRevisionModel>
        implements BaseRevisionedService<E, T> {

    @Override
    public E getEntity(String entityId) {
        E entity = getEntityRepo().get(entityId);
        checkEntityNotNull(entityId, entity, getEntityType());
        return entity;
    }

    @Override
    public E createEntity(E entity) {
        // TODO: revisions
        return getEntityRepo().create(entity);
    }

    @Override
    public E updateEntity(String entityId, E entity) {
        E existingEntity = getEntityRepo().get(entityId);
        checkEntityNotNull(entityId, existingEntity, getEntityType());

        if (!isEqual(existingEntity.getCurrentRevisionId(), entity.getCurrentRevisionId())) {
            throw AppErrors.INSTANCE.fieldNotCorrect("currentRevision", "The field should not be explicitly updated.")
                    .exception();
        }

        if (!existingEntity.getRev().equals(entity.getRev())) {
            throw AppErrors.INSTANCE
                    .fieldNotMatch("rev", entity.getRev(), existingEntity.getRev()).exception();
        }

        return getEntityRepo().update(entity);
    }

    @Override
    public void deleteEntity(String entityId) {
        E existingEntity = getEntityRepo().get(entityId);
        checkEntityNotNull(entityId, existingEntity, getEntityType());
        getEntityRepo().delete(entityId);
    }

    @Override
    public T getRevision(String revisionId) {
        T revision = getRevisionRepo().get(revisionId);
        checkEntityNotNull(revisionId, revision, getRevisionType());
        return revision;
    }

    @Override
    public T updateRevision(String revisionId, T revision) {
        if (Status.APPROVED.is(revision.getStatus())) {
            revision.setTimestamp(Utils.currentTimestamp());
        }
        getRevisionRepo().update(revision);
        if (Status.APPROVED.is(revision.getStatus())) {
            E entity = getEntityRepo().get(revision.getEntityId());
            checkEntityNotNull(revision.getEntityId(), entity, getEntityType());
            if (entity instanceof Offer) {
                ((Offer) entity).setPublished(Boolean.TRUE);
            }
            String lastRevisionId = entity.getCurrentRevisionId();
            entity.setCurrentRevisionId(revisionId);
            getEntityRepo().update(entity);
        }
        return getRevisionRepo().get(revisionId);
    }

    @Override
    public void deleteRevision(String revisionId) {
        T existingRevision = getRevisionRepo().get(revisionId);
        checkEntityNotNull(revisionId, existingRevision, getRevisionType());
        if (Status.APPROVED.is(existingRevision.getStatus())) {
            throw AppErrors.INSTANCE.validation("Cannot delete an approved revision.").exception();
        }
        getRevisionRepo().delete(revisionId);
    }

    protected void checkEntityNotNull(String entityId, BaseModel entity, String name) {
        if (entity == null) {
            throw AppErrors.INSTANCE.notFound(name, entityId).exception();
        }
    }

    protected void checkFieldNotNull(Object field, String fieldName) {
        if (field == null) {
            throw AppErrors.INSTANCE.missingField(fieldName).exception();
        }
    }

    protected void validateId(String expectedId, String actualId) {
        if (actualId == null) {
            throw AppErrors.INSTANCE.missingField("id").exception();
        }
        if (!expectedId.equals(actualId)) {
            throw AppErrors.INSTANCE.fieldNotMatch("id", actualId, expectedId).exception();
        }
    }

    protected void checkRequestNotNull(BaseModel entity) {
        if (entity == null) {
            throw AppErrors.INSTANCE.invalidJson("Invalid json.").exception();
        }
    }

    protected void checkPrice(Price price, List<AppError> errors) {
        if (!PriceType.contains(price.getPriceType())) {
            errors.add(AppErrors.INSTANCE.fieldNotCorrect("priceType", "Valid price types: " + PriceType.ALL));
        }

        if (PriceType.TIERED.is(price.getPriceType())) {
            if (!CollectionUtils.isEmpty(price.getPrices())) {
                errors.add(AppErrors.INSTANCE.unnecessaryField("prices"));
            }
        } else if (PriceType.FREE.is(price.getPriceType())) {
            if (price.getPriceTier() != null) {
                errors.add(AppErrors.INSTANCE.unnecessaryField("priceTier"));
            }
            if (!CollectionUtils.isEmpty(price.getPrices())) {
                errors.add(AppErrors.INSTANCE.unnecessaryField("prices"));
            }
        } else if (PriceType.CUSTOM.is(price.getPriceType())) {
            if (price.getPriceTier() != null) {
                errors.add(AppErrors.INSTANCE.unnecessaryField("priceTier"));
            }
        }
    }

    protected boolean isEqual(String v1, String v2) {
        if (v1==null) {
            return v2==null;
        }
        return v1.equals(v2);
    }

    protected abstract <RE extends BaseEntityRepository<E>> RE getEntityRepo();
    protected abstract <RR extends BaseRevisionRepository<T>> RR getRevisionRepo();
    protected abstract String getEntityType();
    protected abstract String getRevisionType();
}
