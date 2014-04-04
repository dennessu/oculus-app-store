/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.impl

import groovy.transform.CompileStatic
import net.spy.memcached.MemcachedClientIF

import java.security.SecureRandom

/**
 * Java doc.
 */
@CompileStatic
class GlobalCounterMemcachedImpl implements GlobalCounter {

    private final MemcachedClientIF client

    private final int secondsToTrack

    private final Random random

    GlobalCounterMemcachedImpl(MemcachedClientIF client, int secondsToTrack) {
        this.client = client

        this.secondsToTrack = secondsToTrack

        this.random = new SecureRandom()
    }

    @Override
    int getAndIncrease(int shardId, int timeSec) {

        String key = shardId + '_' + (timeSec % (2 * secondsToTrack))

        client.touch(key, secondsToTrack)
        long value = client.incr(key, 1, random.nextInt(Integer.MAX_VALUE), secondsToTrack)

        return (int) (value & Integer.MAX_VALUE)
    }
}
