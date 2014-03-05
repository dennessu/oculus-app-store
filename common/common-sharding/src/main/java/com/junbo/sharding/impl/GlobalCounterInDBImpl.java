/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.impl;

import com.junbo.sharding.dao.ShardIdGlobalCounterDAO;
import com.junbo.sharding.model.ShardIdGlobalCounterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 3/5/14.
 */
@Component
public class GlobalCounterInDBImpl implements GlobalCounter {
    @Autowired
    private ShardIdGlobalCounterDAO shardIdGlobalCounterDAO;

    @Override
    public int getAndIncrease(int shardId, int timeSec, int optionMode) {
        ShardIdGlobalCounterEntity entity = shardIdGlobalCounterDAO.get((long)optionMode, (long)shardId);

        if(entity == null) {
            entity = new ShardIdGlobalCounterEntity();
            entity.setShardId((long)shardId);
            entity.setGlobalCounter(0L);
        }
        else {
            entity.setGlobalCounter(entity.getGlobalCounter() + 1);
        }
        return shardIdGlobalCounterDAO.saveOrUpdate(entity).getGlobalCounter().intValue();
    }
}
