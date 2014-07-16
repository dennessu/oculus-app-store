/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.common.BaseRevisionModel;

/**
 * Repository interface for entity revision.
 * @param <T> the entity type.
 */
public interface BaseRevisionRepository<T extends BaseRevisionModel>  {
    T create(T revision);
    T get(String revisionId);
    T update(T revision, T oldRevision);
    void delete(String entityId);
}
