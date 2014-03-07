/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dao;

import com.junbo.sharding.model.IdGlobalCounterEntity;

/**
 * Created by liangfu on 3/5/14.
 */
public interface IdGlobalCounterDAO {
    IdGlobalCounterEntity get(Long optionMode, Long shareId);
    IdGlobalCounterEntity saveOrUpdate(IdGlobalCounterEntity entity);
    IdGlobalCounterEntity update(IdGlobalCounterEntity entity);
}
