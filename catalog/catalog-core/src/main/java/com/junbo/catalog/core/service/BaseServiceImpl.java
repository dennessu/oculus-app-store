/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.db.repo.EntityDraftRepository;
import com.junbo.catalog.db.repo.EntityRepository;
import com.junbo.catalog.spec.model.common.BaseModel;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.Status;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Base service implementation.
 * @param <T> the entity type.
 */
public class BaseServiceImpl<T extends BaseModel> {
    public List<T> getEntities(EntitiesGetOptions options,
                                                     EntityRepository<T> entityRepo,
                                                     EntityDraftRepository<T> entityDraftRepo){

        if (!CollectionUtils.isEmpty(options.getEntityIds())) {
            List<T> entities = new ArrayList<>();

            for (Long entityId : options.getEntityIds()) {
                T entity;
                if (Status.RELEASED.equalsIgnoreCase(options.getStatus())) {
                    entity = entityRepo.get(entityId, options.getTimestamp());
                } else {
                    entity = entityDraftRepo.get(entityId);
                }

                if (entity != null) {
                    entities.add(entity);
                }
            }
            return entities;
        } else {
            options.ensurePagingValid();
            List<T> draftEntities = entityDraftRepo.getEntities(options.getStart(), options.getSize());
            if (!Status.RELEASED.equalsIgnoreCase(options.getStatus())) {
                return draftEntities;
            }

            List<T> entities = new ArrayList<>();
            for (T draftEntity : draftEntities) {
                T entity = entityRepo.get(draftEntity.getId(), options.getTimestamp());
                if (entity != null) {
                    entities.add(entity);
                }
            }

            return entities;
        }
    }
}
