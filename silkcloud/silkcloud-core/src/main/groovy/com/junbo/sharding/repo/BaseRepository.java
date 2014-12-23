/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.repo;

import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.annotations.DeleteMethod;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.dualwrite.annotations.WriteMethod;

/**
 * DataAccessStrategy.
 *
 * @param <T> The entity type.
 * @param <K> The entity key type.
 */
public interface BaseRepository<T extends CloudantEntity, K> {

    @ReadMethod
    Promise<T> get(K id);

    @WriteMethod
    Promise<T> create(T model);

    @WriteMethod
    Promise<T> update(T model, T oldModel);

    @DeleteMethod
    Promise<Void> delete(K id);
}
