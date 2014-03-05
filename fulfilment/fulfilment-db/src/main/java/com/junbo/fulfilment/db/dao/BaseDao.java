/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.dao;

import com.junbo.fulfilment.db.entity.BaseEntity;

/**
 * BaseDao.
 *
 * @param <T> entity type
 */
public interface BaseDao<T extends BaseEntity> {
    Long create(T entity);

    T get(Long id);

    Long update(T t);

    Boolean exists(Long id);

    void flush();
}
