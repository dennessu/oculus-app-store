/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.impl;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Java doc.
 */
public class GlobalCounterInMemoryImpl implements GlobalCounter {
   private AtomicLong shardValue = new AtomicLong(0);

    public GlobalCounterInMemoryImpl() {
    }

    @Override
    public int getAndIncrease(int shardId, int timeSec) {
        long value = shardValue.getAndIncrement();

        return (int) (value & Integer.MAX_VALUE);
    }
}
