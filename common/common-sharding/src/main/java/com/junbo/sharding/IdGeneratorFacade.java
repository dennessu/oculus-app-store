/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding;

import com.junbo.common.id.Id;

import java.util.Map;

/**
 * Created by liangfu on 3/10/14.
 */
public class IdGeneratorFacade {
    private final Map<Class, IdGenerator> map;

    public IdGeneratorFacade(Map<Class, IdGenerator> map) {
        if(map == null) {
            throw new IllegalArgumentException("map is null.");
        }
        this.map = map;
    }

    /**
     * Generate a new ID, the shardId is random distributed.
     * @return The new unique ID on one shard.
     */
    <T extends Id> long nextId(Class<T> cls) {
        IdGenerator idGenerator = map.get(cls);
        if(idGenerator == null) {
            throw new IllegalArgumentException("Can't find map for " + cls);
        }
        return idGenerator.nextId();
    }

    /**
     * Generate a new ID following the shard of the existing ID.
     * @param id The source(seed) ID for shard information.
     * @return The new unique ID on the same shard as the source ID.
     */
    <T extends Id> long nextId(Class<T> cls, long id) {
        IdGenerator idGenerator = map.get(cls);
        if(idGenerator == null) {
            throw new IllegalArgumentException("Can't find map for " + cls);
        }
        return idGenerator.nextId(id);
    }

    /**
     * Generate a new ID given the shard ID.
     * @param shardId The expected shardId.
     * @return The new unqiue ID on the expected shard.
     */
    <T extends Id> long nextIdByShardId(Class<T> cls, int shardId) {
        IdGenerator idGenerator = map.get(cls);
        if(idGenerator == null) {
            throw new IllegalArgumentException("Can't find map for " + cls);
        }
        return idGenerator.nextIdByShardId(shardId);
    }
}
