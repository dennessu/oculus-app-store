/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.common.*;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base service for revisioned entity.
 * @param <E> entity.
 * @param <T> entity revision.
 */
@Transactional
public interface BaseRevisionedService<E extends BaseEntityModel, T extends BaseRevisionModel> {
    E getEntity(String entityId);
    E createEntity(E entity);
    E updateEntity(String entityId, E entity);
    void deleteEntity(String entityId);

    T getRevision(String revisionId);
    T createRevision(T revision);
    T updateRevision(String revisionId, T revision);
    void deleteRevision(String revisionId);
}
