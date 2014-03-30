/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.common.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Base service for revisioned entity.
 * @param <E> entity.
 * @param <T> entity revision.
 */
@Transactional
public interface BaseRevisionedService<E extends BaseEntityModel, T extends BaseRevisionModel> {
    E getEntity(Long entityId);
    List<E> getEntities(EntitiesGetOptions options);
    E createEntity(E entity);
    E updateEntity(Long entityId, E entity);
    void deleteEntity(Long entityId);

    T getRevision(Long revisionId);
    T createRevision(T revision);
    T updateRevision(Long revisionId, T revision);
    void deleteRevision(Long revisionId);
}
