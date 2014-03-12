/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.common.VersionedModel;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Base service.
 * @param <T> the model type.
 */
@Transactional
public interface BaseService<T extends VersionedModel> {
    T get(Long entityId, EntityGetOptions options);
    List<T> getEntities(EntitiesGetOptions options);
    T create(T entity);
    T update(Long entityId, T entity);
    T review(Long entityId);
    T release(Long entityId);
    T reject(Long entityId);
    Long remove(Long entityId);
    Long delete(Long entityId);
}
