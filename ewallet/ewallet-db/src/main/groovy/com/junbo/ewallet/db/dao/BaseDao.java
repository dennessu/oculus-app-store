/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.dao;

import com.junbo.ewallet.db.entity.BaseEntity;

/**
 * Created by haomin on 14-6-20.
 */
public interface BaseDao<T extends BaseEntity> {
    T get(Long id);
    void update(T entity);
    Long insert(T entity);
    void delete(Long id);
}
