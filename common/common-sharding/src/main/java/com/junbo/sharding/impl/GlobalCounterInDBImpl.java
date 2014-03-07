/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.impl;

import com.junbo.sharding.dao.IdGlobalCounterDAO;
import com.junbo.sharding.model.IdGlobalCounterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/5/14.
 */
@Component
public class GlobalCounterInDBImpl implements GlobalCounter {
    @Autowired
    private IdGlobalCounterDAO shardIdGlobalCounterDAO;

    @Override
    public int getAndIncrease(int shardId, int timeSec, int optionMode) {
        IdGlobalCounterEntity entity = shardIdGlobalCounterDAO.get((long)optionMode, (long)shardId);

        if(entity == null) {
            entity = new IdGlobalCounterEntity();
            entity.setOptionMode((long)optionMode);
            entity.setShardId((long)shardId);
            entity.setGlobalCounter(0L);
        }
        else {
            entity.setGlobalCounter(entity.getGlobalCounter() + 1);
        }
        return shardIdGlobalCounterDAO.saveOrUpdate(entity).getGlobalCounter().intValue();
    }
}
