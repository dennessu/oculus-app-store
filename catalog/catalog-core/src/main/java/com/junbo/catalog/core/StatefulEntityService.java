/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;

import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Stateful entity service.
 * @param <T> the model type.
 */
@Transactional
public interface StatefulEntityService<T extends BaseEntityModel> {
    T get(Long entityId);
    List<T> getAll(EntitiesGetOptions options);
    T create(T entity);
    T update(Long entityId, T entity);
    T publish(Long entityId);
    Long unpublish(Long entityId);
    Long delete(Long entityId);
}
