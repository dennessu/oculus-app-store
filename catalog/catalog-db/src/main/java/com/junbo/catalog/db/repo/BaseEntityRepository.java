/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.common.BaseEntityModel;

/**
 * Repository interface for entity.
 * @param <T> the entity type.
 */
public interface BaseEntityRepository<T extends BaseEntityModel>  {
    Long create(T entity);
    T get(Long entityId);
    Long update(T entity);
    void delete(Long entityId);
}
