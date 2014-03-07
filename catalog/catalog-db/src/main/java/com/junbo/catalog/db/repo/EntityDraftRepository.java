/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.common.BaseModel;
import java.util.List;

/**
 * Draft repository interface.
 * @param <T> the entity type.
 */
public interface EntityDraftRepository<T extends BaseModel>  {
    Long create(T entity);

    T get(Long entityId);

    List<T> getEntities(int start, int size);

    Long update(T entity);
}
