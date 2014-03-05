/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import java.io.Serializable;

/**
 * Created by xmchen on 14-1-26.
 * Base dao for hibernate Dao.
 *
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface BaseDao<T extends Serializable, ID extends Serializable> {
    ID insert(T entity);

    T get(ID id);

    T update(T t);

    Boolean exists(ID id);

    void flush();
}
