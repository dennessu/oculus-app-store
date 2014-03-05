/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.mock;

import com.junbo.sharding.IdGenerator;

/**
 * MockIdGenerator.
 */
public class MockIdGenerator implements IdGenerator {
    @Override
    public long nextId() {
        return genSimpleId();
    }

    @Override
    public long nextId(long id) {
        return genSimpleId();
    }

    @Override
    public long nextIdByShardId(int shardId) {
        return genSimpleId();
    }

    private long genSimpleId() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            //ignore
        }
        return System.currentTimeMillis();
    }
}
