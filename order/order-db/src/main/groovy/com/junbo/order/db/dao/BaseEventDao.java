/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao;

import com.junbo.order.db.entity.CommonEventEntity;

/**
 * Created by linyi on 14-2-7.
 *
 * @param <T> the type of entity to work with
 */
public interface BaseEventDao<T extends CommonEventEntity> {
    Long create(T t);

    T read(long id);

    void update(T t);

    void flush();
}
