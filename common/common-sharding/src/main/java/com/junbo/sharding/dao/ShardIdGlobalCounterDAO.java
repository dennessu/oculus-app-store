/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dao;

import com.junbo.sharding.model.ShardIdGlobalCounterEntity;

/**
 * Created by liangfu on 3/5/14.
 */
public interface ShardIdGlobalCounterDAO {
    ShardIdGlobalCounterEntity get(Long optionMode, Long shareId);
    ShardIdGlobalCounterEntity saveOrUpdate(ShardIdGlobalCounterEntity entity);
    ShardIdGlobalCounterEntity update(ShardIdGlobalCounterEntity entity);
}
