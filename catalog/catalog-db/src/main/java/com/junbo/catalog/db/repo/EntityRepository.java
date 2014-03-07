/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.spec.model.common.BaseModel;

/**
 * Entity Repository interface.
 * @param <T> the entity type.
 */
public interface EntityRepository<T extends BaseModel>  {
    Long create(T entity);
    T get(Long entityId, Long timestamp);
}
