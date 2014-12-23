/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Distributed ID Generator to generate unique IDs for the system.
 * The ID generated is a positive 64-bit integer. The ID has the following property:
 * <ul>
 * <li>The ID generated is almost sequential.</li>
 * <li>The ID generated is globally unique in the system.</li>
 * </ul>
 * <p/>
 * <pre>{@code
 *  hexValue = timeStamp << 22 | globalCounter << 10 | localCounter
 *  id = hexValue * 1000 + shardId
 *
 *  timeStamp:      The seconds since 2014/1/1. The highest bit is not used to avoid negative values, so it's 31 bits.
 *                  The value can be used until 2082.
 *  globalCounter:  A 12-bit global counter per shard + timeStamp % (2 * secondsToTrack).
 *                  the expiration of the counter is secondsToTrack.
 *  localCounter:   A 10-bit local counter implemented using AtomicInteger.
 *  shardId:        The logical shard ID used for data sharding, ranges from 0 to 999.
 *  }
 *  </pre>
 */
public class IdGeneratorImpl implements com.junbo.sharding.IdGenerator {

    private final List<IdGeneratorSlot> slots;

    private final Random random;

    private final IdSchema idSchema;

    public IdGeneratorImpl(IdSchema idSchema, TimeGenerator timeGenerator, GlobalCounter globalCounter) {

        this.idSchema = idSchema;
        slots = new ArrayList<IdGeneratorSlot>();

        for (int shardId = 0; shardId < idSchema.getNumberOfShards(); shardId++) {

            slots.add(new IdGeneratorSlotImpl(
                    shardId, idSchema, timeGenerator, globalCounter));
        }

        random = new SecureRandom();
    }

    @Override
    public long nextId() {
        int shardId = random.nextInt(slots.size());
        return nextIdByDCIdAndShardId(shardId, 0);
    }

    @Override
    public long nextId(long id) {
        ObjectId objectId = idSchema.parseObjectId(id);
        int shardId = objectId.getShardId();
        return nextIdByDCIdAndShardId(shardId, 0);
    }

    @Override
    public long nextIdByDCIdAndShardId(int dataCenterId, int shardId) {
        // This is the ordinary time based ID generator, it won't have the concept of dataCenter
        IdGeneratorSlot slot = slots.get(shardId);
        return slot.nextId();
    }
}
