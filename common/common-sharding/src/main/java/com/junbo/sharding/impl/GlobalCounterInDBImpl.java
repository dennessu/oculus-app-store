/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.impl;

/**
 * Created by liangfu on 3/5/14.
 */
public class GlobalCounterInDBImpl implements GlobalCounter {
    @Override
    public int getAndIncrease(int shardId, int timeSec) {
        // Todo:    Liangfu
        // Need to read from the db
        return 0;
    }
}
