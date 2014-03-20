/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.sharding.annotations.SeedParam;

import java.io.Serializable;

/**
 * Created by haomin on 14-3-20.
 *
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface BaseDao<T, ID extends Serializable> {
    ID save(T entity);

    T get(@SeedParam ID id);

    T update(T t);

    Boolean exists(ID id);

    void flush();
}
