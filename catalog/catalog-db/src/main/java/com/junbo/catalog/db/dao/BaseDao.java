/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.BaseEntity;

/**
 *  Base DAO definition.
 * @param <T> entity to operate.
 */
public interface BaseDao<T extends BaseEntity> {
    Long create(T entity);

    T get(Long id);

    Long update(T t);

    Boolean exists(Long id);

    //void flush();
}
