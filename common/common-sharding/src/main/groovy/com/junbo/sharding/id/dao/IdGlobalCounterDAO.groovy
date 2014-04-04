/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.id.dao

import com.junbo.sharding.id.model.IdGlobalCounterEntity
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/5/14.
 */
@CompileStatic
interface IdGlobalCounterDAO {
    IdGlobalCounterEntity get(Long optionMode, Long shareId)
    IdGlobalCounterEntity saveOrUpdate(IdGlobalCounterEntity entity)
    IdGlobalCounterEntity update(IdGlobalCounterEntity entity)
}
