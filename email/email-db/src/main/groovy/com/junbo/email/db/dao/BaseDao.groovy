/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao

import com.junbo.email.db.entity.BaseEntity

/**
 * Interface of BaseDao
 * @param < T > Entity Type
 */
interface BaseDao<T extends BaseEntity> {
    Long save(T entity)

    Long update(T entity)

    T get(Long id)

    void delete(T entity)
    
    void flush()
}
