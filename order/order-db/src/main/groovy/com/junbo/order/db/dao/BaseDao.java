/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao;

import com.junbo.order.db.entity.CommonDbEntityWithDate;

/**
 * Created by LinYi on 2/8/14.
 *
 * @param <T> the type of entity to work with
 */
public interface BaseDao<T extends CommonDbEntityWithDate> {

    Long create(T t);

    T read(Long id);

    void update(T t);

    void markDelete(Long id);
}
